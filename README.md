## fallow

An experimental messaging framework, designed for elegant and efficient communication.

### Why Fallow?

- **Flexible**: Supports Redis and NATS out of the box.
- **Agnostic**: Use JSON, MsgPack, or plug in your own.
- **Built on [Wisp](https://github.com/vanqure/wisp)**: Minimal, high-performance event bus powering Fallow’s internals.
- **Simple & Expressive**: Clean annotations and subscribers, no boilerplate.

### Get started

You can build dependency and append it to your local .m2 directory, by using: `./gradlew publishToMavenLocal`

### Using Fallow

Fallow in action:

```java
final PacketBroker packetBroker = RedisPacketBroker.create(
        Wisp.create(), RedisClient.create("redis://localhost:6379"), 
        JacksonPacketCodecProducer.produceCodec(), 
        Duration.ofSeconds(30L));

packetBroker         
        .<MasterSlaveResponsePacket>request("tests", new MasterSlaveRequestPacket("Ping!"))
        .thenAccept(packet -> System.out.printf("Received: %s", packet))
        .join();
packetBroker.subscribe(new MasterSlaveServerSubscriber());
packetBroker.publish("tests", new BroadcastPacket("Hello from client!"));

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

public class MasterSlaveResponsePacket extends JacksonPacket {

    private String content;

    public MasterSlaveResponsePacket() {}

    public MasterSlaveResponsePacket(final String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return "MasterSlaveResponsePacket{" + "content='" + content + '\'' + "} " + super.toString();
    }
}

public class MasterSlaveRequestPacket extends JacksonPacket {

    private String content;

    public MasterSlaveRequestPacket() {}

    public MasterSlaveRequestPacket(final String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return "MasterSlaveRequestPacket{" + "content='" + content + '\'' + "} " + super.toString();
    }
}

public class BroadcastPacket extends JacksonPacket {

    private String content;

    public BroadcastPacket() {}

    public BroadcastPacket(final String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return "BroadcastPacket{" +
                "content='" + content + '\'' +
                "} " + super.toString();
    }
}
```

---

![Visitor Count](https://visitor-badge.laobi.icu/badge?page_id=vanqure.fallow)
