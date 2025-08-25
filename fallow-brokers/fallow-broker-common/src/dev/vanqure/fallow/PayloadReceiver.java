package dev.vanqure.fallow;

@FunctionalInterface
public interface PayloadReceiver {

    void receive(String channelName, byte[] payload);
}
