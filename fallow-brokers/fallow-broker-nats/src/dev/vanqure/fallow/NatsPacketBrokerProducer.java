package dev.vanqure.fallow;

import dev.vanqure.fallow.codec.PacketCodec;
import dev.vanqure.wisp.Wisp;
import io.nats.client.Connection;
import java.time.Duration;

public final class NatsPacketBrokerProducer {

    private NatsPacketBrokerProducer() {}

    public static PacketBroker produceBroker(
            final Wisp wisp,
            final Connection connection,
            final PacketCodec packetCodec,
            final Duration requestCleanupInterval) {
        return new NatsPacketBroker(wisp, connection, packetCodec, requestCleanupInterval);
    }
}
