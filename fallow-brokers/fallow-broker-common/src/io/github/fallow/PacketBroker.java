package io.github.fallow;

import io.github.wisp.subscription.Subscriber;
import java.io.Closeable;
import java.util.concurrent.CompletableFuture;

public interface PacketBroker extends Closeable {

    void publish(String topic, Packet packet) throws PacketPublishingException;

    <R extends Packet> CompletableFuture<R> request(String topic, Packet packet) throws PacketRequestingException;

    void subscribe(Subscriber subscriber) throws PacketSubscribingException;
}
