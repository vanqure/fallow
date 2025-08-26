package dev.vanqure.fallow;

import dev.vanqure.fallow.codec.PacketCodec;
import dev.vanqure.wisp.Wisp;
import io.lettuce.core.RedisClient;
import java.time.Duration;

public final class RedisPacketBrokerProducer {

    private RedisPacketBrokerProducer() {}

    public static PacketBroker produceBroker(
            final Wisp wisp,
            final RedisClient redisClient,
            final PacketCodec packetCodec,
            final Duration requestCleanupInterval) {
        return new RedisPacketBroker(wisp, redisClient, packetCodec, requestCleanupInterval);
    }
}
