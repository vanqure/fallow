package dev.vanqure.fallow.tests;

import dev.vanqure.wisp.subscription.Subscribe;
import dev.vanqure.wisp.subscription.Subscriber;

public final class MasterSlaveServerSubscriber implements Subscriber {

    @Override
    public String topic() {
        return "tests";
    }

    @Subscribe
    public MasterSlaveResponsePacket receive(final MasterSlaveRequestPacket request) {
        // method can be a void, no need to return any packets,
        // if response cannot be sent it's also
        // fine you can return null
        return new MasterSlaveResponsePacket(request.getContent() + " Pong!");
    }

    @Subscribe
    public void receive(final BroadcastPacket packet) {
        System.out.printf("Received p2p packet: %s%n", packet.getContent());
    }
}