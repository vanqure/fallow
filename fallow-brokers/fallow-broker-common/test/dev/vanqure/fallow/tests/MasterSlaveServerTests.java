package dev.vanqure.fallow.tests;

import dev.vanqure.fallow.PacketBroker;
import dev.vanqure.fallow.RedisPacketBroker;
import dev.vanqure.fallow.codec.JacksonPacketCodecFactory;
import dev.vanqure.fallow.codec.PacketCodec;
import dev.vanqure.wisp.Wisp;
import io.lettuce.core.RedisClient;
import java.time.Duration;

public final class MasterSlaveServerTests {

    private MasterSlaveServerTests() {}

    @SuppressWarnings({"BusyWait", "InfiniteLoopStatement"})
    public static void main(final String[] args) {
        final PacketCodec packetCodec = JacksonPacketCodecFactory.create();
        final PacketBroker packetBroker = new RedisPacketBroker(
                Wisp.create(), RedisClient.create("redis://localhost:6379"), packetCodec, Duration.ofSeconds(30L));

        packetBroker.subscribe(new MasterSlaveServerSubscriber());

        while (true) {
            try {
                Thread.sleep(1000L);
            } catch (final InterruptedException exception) {
                exception.printStackTrace();
            }
        }
    }
}
