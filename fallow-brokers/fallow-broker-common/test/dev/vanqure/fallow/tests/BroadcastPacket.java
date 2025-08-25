package dev.vanqure.fallow.tests;

import dev.vanqure.fallow.codec.JacksonPacket;

public class BroadcastPacket extends JacksonPacket {

    private final String content;

    public BroadcastPacket(final String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return "BroadcastPacket{" + "content='" + content + '\'' + "} " + super.toString();
    }
}
