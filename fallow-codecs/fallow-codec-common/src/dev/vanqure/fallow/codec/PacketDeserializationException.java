package dev.vanqure.fallow.codec;

public final class PacketDeserializationException extends RuntimeException {

    PacketDeserializationException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
