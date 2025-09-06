package io.github.fallow;

public final class PacketSubscribingException extends RuntimeException {

    PacketSubscribingException(String message) {
        super(message);
    }

    PacketSubscribingException(String message, Throwable cause) {
        super(message, cause);
    }
}
