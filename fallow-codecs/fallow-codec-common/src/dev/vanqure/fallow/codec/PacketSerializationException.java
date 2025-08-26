package dev.vanqure.fallow.codec;

public final class PacketSerializationException extends RuntimeException {

    PacketSerializationException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
