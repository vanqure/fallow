package dev.vanqure.fallow;

import io.lettuce.core.pubsub.RedisPubSubListener;
import java.util.function.Consumer;

final class RedisPacketSubscriber implements RedisPubSubListener<String, byte[]> {

    private final String subscribedTopic;
    private final Consumer<byte[]> receiver;

    RedisPacketSubscriber(final String subscribedTopic, final Consumer<byte[]> receiver) {
        this.subscribedTopic = subscribedTopic;
        this.receiver = receiver;
    }

    @Override
    public void message(final String pattern, final String channelName, final byte[] message) {
        message("%s:%s".formatted(pattern, channelName), message);
    }

    @Override
    public void message(final String channelName, final byte[] message) {
        if (subscribedTopic.equals(channelName)) {
            receiver.accept(message);
        }
    }

    @Override
    public void subscribed(final String channel, final long count) {}

    @Override
    public void psubscribed(final String pattern, final long count) {}

    @Override
    public void unsubscribed(final String channel, final long count) {}

    @Override
    public void punsubscribed(final String pattern, final long count) {}
}
