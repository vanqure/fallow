package dev.vanqure.fallow.tests;

import dev.vanqure.fallow.codec.JacksonPacket;

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
