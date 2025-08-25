package dev.vanqure.fallow;

import dev.vanqure.wisp.event.Event;

public abstract class Packet implements Event {

    private String replyTo;

    public String getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(final String replyTo) {
        this.replyTo = replyTo;
    }
}
