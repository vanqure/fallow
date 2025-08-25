package dev.vanqure.fallow;

import dev.vanqure.fallow.codec.PacketCodec;
import dev.vanqure.fallow.codec.RedisBinaryCodec;
import dev.vanqure.wisp.Wisp;
import dev.vanqure.wisp.subscription.Subscriber;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import java.time.Duration;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public final class RedisPacketBroker implements PacketBroker {

    private final Wisp wisp;
    private final PacketCodec packetCodec;
    private final RedisClient redisClient;
    private final Duration requestCleanupInterval;
    private final StatefulRedisConnection<String, byte[]> connection;
    private final StatefulRedisPubSubConnection<String, byte[]> pubSubConnection;

    private final Set<String> subscribedTopics;

    private RedisPacketBroker(
            final Wisp wisp,
            final RedisClient redisClient,
            final PacketCodec packetCodec,
            final Duration requestCleanupInterval) {
        this.wisp = wisp.result(Packet.class, (event, response) -> {
            if (event instanceof final Packet request) {
                response.setReplyTo(request.getReplyTo());
            }
            publish(response.getReplyTo(), response);
        });
        this.redisClient = redisClient;
        final RedisBinaryCodec codec = RedisBinaryCodec.INSTANCE;
        this.connection = redisClient.connect(codec);
        this.pubSubConnection = redisClient.connectPubSub(codec);
        this.packetCodec = packetCodec;
        this.requestCleanupInterval = requestCleanupInterval;
        this.subscribedTopics = ConcurrentHashMap.newKeySet();
    }

    @Override
    public void publish(final String channelName, final Packet packet) {
        try {
            final byte[] payload = packetCodec.serialize(packet);
            connection.sync().publish(channelName, payload);
        } catch (final Exception exception) {
            throw new PacketPublishingException("Couldn't publish packet over the packet broker.", exception);
        }
    }

    public static PacketBroker create(
            final Wisp wisp,
            final RedisClient redisClient,
            final PacketCodec packetCodec,
            final Duration requestCleanupInterval) {
        return new RedisPacketBroker(wisp, redisClient, packetCodec, requestCleanupInterval);
    }

    @Override
    public <R extends Packet> CompletableFuture<R> request(final String channelName, final Packet request) {
        try {
            final String replyTo = UUID.randomUUID().toString();
            request.setReplyTo(replyTo);

            final CompletableFuture<byte[]> responseFuture = new CompletableFuture<>();
            responseFuture.orTimeout(requestCleanupInterval.toMillis(), TimeUnit.MILLISECONDS);

            pubSubConnection.addListener(new RedisPacketSubscriber(replyTo, responseFuture::complete));
            pubSubConnection.sync().subscribe(replyTo);

            responseFuture.whenCompleteAsync(
                    (response, throwable) -> pubSubConnection.sync().unsubscribe(replyTo));

            publish(channelName, request);

            return responseFuture.thenApply(packetCodec::deserialize).thenApply(response -> {
                //noinspection unchecked
                return (R) response;
            });
        } catch (final Exception exception) {
            throw new PacketRequestingException(
                    "Couldn't get response over the packet broker from %s.".formatted(channelName), exception);
        }
    }

    @Override
    public void subscribe(final Subscriber subscriber) {
        final var topic = subscriber.topic();
        if (topic == null || topic.isEmpty()) {
            throw new PacketSubscribingException("Subscriber's topic cannot be null or empty");
        }

        wisp.subscribe(subscriber);

        if (subscribedTopics.contains(topic)) {
            return;
        }

        pubSubConnection.addListener(new RedisPacketSubscriber(topic, payload -> {
            final var packet = packetCodec.deserialize(payload);
            wisp.publish(packet, topic);
        }));
        pubSubConnection.sync().subscribe(topic);
    }

    @Override
    public void close() throws PacketBrokerException {
        try {
            redisClient.close();
            connection.close();
            pubSubConnection.close();
            subscribedTopics.clear();
        } catch (final Exception exception) {
            throw new PacketBrokerException("Couldn't close nats packet broker.", exception);
        }
    }
}
