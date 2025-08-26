package dev.vanqure.fallow;

import dev.vanqure.fallow.codec.PacketCodec;
import dev.vanqure.wisp.Wisp;
import dev.vanqure.wisp.subscription.Subscriber;
import io.nats.client.Connection;
import io.nats.client.Message;
import java.time.Duration;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

final class NatsPacketBroker implements PacketBroker {

    private final Wisp wisp;
    private final Connection connection;
    private final PacketCodec packetCodec;
    private final Duration requestCleanupInterval;

    private final Set<String> subscribedTopics;

    NatsPacketBroker(
            final Wisp wisp,
            final Connection connection,
            final PacketCodec packetCodec,
            final Duration requestCleanupInterval) {
        this.wisp = wisp.result(Packet.class, (event, response) -> {
            if (event instanceof final Packet request) {
                response.setReplyTo(request.getReplyTo());
            }
            publish(response.getReplyTo(), response);
        });
        this.connection = connection;
        this.packetCodec = packetCodec;
        this.requestCleanupInterval = requestCleanupInterval;
        this.subscribedTopics = ConcurrentHashMap.newKeySet();
    }

    @Override
    public void publish(final String channelName, final Packet packet) {
        try {
            final byte[] payload = packetCodec.serialize(packet);
            connection.publish(channelName, payload);
        } catch (final Exception exception) {
            throw new PacketPublishingException("Couldn't publish packet over the packet broker.", exception);
        }
    }

    @Override
    public <R extends Packet> CompletableFuture<R> request(final String channelName, final Packet packet) {
        try {
            final byte[] payload = packetCodec.serialize(packet);
            return connection
                    .requestWithTimeout(channelName, payload, requestCleanupInterval)
                    .thenApply(Message::getData)
                    .thenApply(packetCodec::deserialize)
                    .thenApply(response -> {
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

        connection
                .createDispatcher(message -> {
                    final var payload = message.getData();

                    final var packet = packetCodec.deserialize(payload);
                    packet.setReplyTo(message.getReplyTo());

                    wisp.publish(packet, topic);
                })
                .subscribe(topic);
    }

    @Override
    public void close() throws PacketBrokerException {
        try {
            connection.close();
        } catch (final InterruptedException exception) {
            throw new PacketBrokerException("Couldn't close nats packet broker.", exception);
        }
    }
}
