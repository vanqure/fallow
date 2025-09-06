package io.github.fallow;

import io.lettuce.core.pubsub.RedisPubSubListener;
import java.util.function.Consumer;

final class RedisPacketSubscriber implements RedisPubSubListener<String, byte[]> {

    private final String subscribedTopic;
    private final Consumer<byte[]> receiver;

    RedisPacketSubscriber(String subscribedTopic, Consumer<byte[]> receiver) {
        this.subscribedTopic = subscribedTopic;
        this.receiver = receiver;
    }

    @Override
    public void message(String pattern, String channelName, byte[] message) {
        message("%s:%s".formatted(pattern, channelName), message);
    }

    @Override
    public void message(String channelName, byte[] message) {
        if (subscribedTopic.equals(channelName)) {
            receiver.accept(message);
        }
    }

    @Override
    public void subscribed(String channel, long count) {}

    @Override
    public void psubscribed(String pattern, long count) {}

    @Override
    public void unsubscribed(String channel, long count) {}

    @Override
    public void punsubscribed(String pattern, long count) {}
}
