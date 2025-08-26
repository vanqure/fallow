package dev.vanqure.fallow;

public final class PacketSubscribingException extends RuntimeException {

    PacketSubscribingException(final String message) {
        super(message);
    }

    PacketSubscribingException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
