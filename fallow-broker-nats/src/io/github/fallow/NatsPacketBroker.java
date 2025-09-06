package io.github.fallow;

import io.github.wisp.Wisp;
import io.github.wisp.subscription.Subscriber;
import io.github.fallow.codec.PacketCodec;
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

    NatsPacketBroker(Wisp wisp, Connection connection, PacketCodec packetCodec, Duration requestCleanupInterval) {
        this.wisp = wisp.result(Packet.class, (event, response) -> {
            if (event instanceof Packet request) {
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
    public void publish(String topic, Packet packet) throws PacketPublishingException {
        try {
            byte[] payload = packetCodec.serialize(packet);
            connection.publish(topic, payload);
        } catch (Exception exception) {
            throw new PacketPublishingException("Couldn't publish packet over the packet broker.", exception);
        }
    }

    @Override
    public <R extends Packet> CompletableFuture<R> request(String topic, Packet packet)
            throws PacketRequestingException {
        try {
            byte[] payload = packetCodec.serialize(packet);
            return connection
                    .requestWithTimeout(topic, payload, requestCleanupInterval)
                    .thenApply(Message::getData)
                    .thenApply(packetCodec::deserialize)
                    .thenApply(response -> {
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
            connection
                    .createDispatcher(message -> {
                        var payload = message.getData();

                        var packet = packetCodec.deserialize(payload);
                        packet.setReplyTo(message.getReplyTo());

                        wisp.publish(packet, topic);
                    })
                    .subscribe(topic);
        } catch (Exception exception) {
            throw new PacketSubscribingException(
                    "Couldn't create a dispatcher and subscribe to topic %s with subscriber %s."
                            .formatted(topic, subscriber),
                    exception);
        }
    }

    @Override
    public void close() throws PacketBrokerException {
        try {
            connection.close();
        } catch (InterruptedException exception) {
            throw new PacketBrokerException("Couldn't close nats packet broker.", exception);
        }
    }
}
