package dev.vanqure.fallow;

import dev.vanqure.wisp.subscription.Subscriber;
import java.io.Closeable;
import java.util.concurrent.CompletableFuture;

public interface PacketBroker extends Closeable {

    void publish(String channelName, Packet packet) throws PacketPublishingException;

    <R extends Packet> CompletableFuture<R> request(String channelName, Packet packet) throws PacketRequestingException;

    void subscribe(Subscriber subscriber) throws PacketSubscribingException;
}
