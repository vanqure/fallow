package io.github.fallow;

import io.github.wisp.Wisp;
import io.github.fallow.codec.PacketCodec;
import io.lettuce.core.RedisClient;
import java.time.Duration;

public final class RedisPacketBrokerProducer {

    private RedisPacketBrokerProducer() {}

    public static PacketBroker produceBroker(
            Wisp wisp,
            RedisClient redisClient,
            PacketCodec packetCodec,
            Duration requestCleanupInterval) {
        return new RedisPacketBroker(wisp, redisClient, packetCodec, requestCleanupInterval);
    }
}
