package io.github.fallow;

import io.github.wisp.Wisp;
import io.github.wisp.subscription.Subscriber;
import io.github.fallow.codec.PacketCodec;
import io.github.fallow.codec.RedisBinaryCodec;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import java.time.Duration;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

final class RedisPacketBroker implements PacketBroker {

    private final Wisp wisp;
    private final PacketCodec packetCodec;
    private final RedisClient redisClient;
    private final Duration requestCleanupInterval;
    private final StatefulRedisConnection<String, byte[]> connection;
    private final StatefulRedisPubSubConnection<String, byte[]> pubSubConnection;

    private final Set<String> subscribedTopics;

    RedisPacketBroker(
            Wisp wisp,
            RedisClient redisClient,
            PacketCodec packetCodec,
            Duration requestCleanupInterval) {
        this.wisp = wisp.result(Packet.class, (event, response) -> {
            if (event instanceof Packet request) {
                response.setReplyTo(request.getReplyTo());
            }
            publish(response.getReplyTo(), response);
        });
        this.redisClient = redisClient;
        RedisBinaryCodec codec = RedisBinaryCodec.INSTANCE;
        this.connection = redisClient.connect(codec);
        this.pubSubConnection = redisClient.connectPubSub(codec);
        this.packetCodec = packetCodec;
        this.requestCleanupInterval = requestCleanupInterval;
        this.subscribedTopics = ConcurrentHashMap.newKeySet();
    }

    @Override
    public void publish(String topic, Packet packet) throws PacketPublishingException {
        try {
            byte[] payload = packetCodec.serialize(packet);
            connection.sync().publish(topic, payload);
        } catch (Exception exception) {
            throw new PacketPublishingException("Couldn't publish packet over the packet broker.", exception);
        }
    }

    @Override
    public <R extends Packet> CompletableFuture<R> request(String topic, Packet request)
            throws PacketRequestingException {
        try {
            String replyTo = UUID.randomUUID().toString();
            request.setReplyTo(replyTo);

            CompletableFuture<byte[]> responseFuture = new CompletableFuture<>();
            responseFuture.orTimeout(requestCleanupInterval.toMillis(), TimeUnit.MILLISECONDS);

            pubSubConnection.addListener(new RedisPacketSubscriber(replyTo, responseFuture::complete));
            pubSubConnection.sync().subscribe(replyTo);

            responseFuture.whenCompleteAsync(
                    (response, throwable) -> pubSubConnection.sync().unsubscribe(replyTo));

            publish(topic, request);

            return responseFuture.thenApply(packetCodec::deserialize).thenApply(response -> {
                //noinspection unchecked
                return (R) response;
            });
        } catch (Exception exception) {
            throw new PacketRequestingException(
                    "Couldn't get response over the packet broker from %s.".formatted(topic), exception);
        }
    }

    @Override
    public void subscribe(Subscriber subscriber) throws PacketSubscribingException {
        var topic = subscriber.topic();
        if (topic == null || topic.isEmpty()) {
            throw new PacketSubscribingException("Subscriber's topic cannot be null or empty");
        }

        try {
            wisp.subscribe(subscriber);

            if (subscribedTopics.contains(topic)) {
                return;
            }

            subscribedTopics.add(topic);
            pubSubConnection.addListener(new RedisPacketSubscriber(topic, payload -> {
                var packet = packetCodec.deserialize(payload);
                wisp.publish(packet, topic);
            }));
            pubSubConnection.sync().subscribe(topic);

        } catch (Exception exception) {
            throw new PacketSubscribingException(
                    "Couldn't create a listener to topic %s with subscriber %s.".formatted(topic, subscriber),
                    exception);
        }
    }

    @Override
    public void close() throws PacketBrokerException {
        try {
            redisClient.close();
            connection.close();
            pubSubConnection.close();
            subscribedTopics.clear();
        } catch (Exception exception) {
            throw new PacketBrokerException("Couldn't close nats packet broker.", exception);
        }
    }
}
