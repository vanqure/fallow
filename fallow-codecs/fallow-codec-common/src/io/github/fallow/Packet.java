package io.github.fallow;

import io.github.wisp.event.Event;

public abstract class Packet implements Event {

    private String replyTo;

    public String getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(String replyTo) {
        this.replyTo = replyTo;
    }
}
