package io.github.fallow.codec;

public final class PacketDeserializationException extends RuntimeException {

    PacketDeserializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
