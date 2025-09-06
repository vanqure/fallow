package io.github.fallow;

import io.github.wisp.Wisp;
import io.github.fallow.codec.PacketCodec;
import io.nats.client.Connection;
import java.time.Duration;

public final class NatsPacketBrokerProducer {

    private NatsPacketBrokerProducer() {}

    public static PacketBroker produceBroker(
            Wisp wisp, Connection connection, PacketCodec packetCodec, Duration requestCleanupInterval) {
        return new NatsPacketBroker(wisp, connection, packetCodec, requestCleanupInterval);
    }
}
