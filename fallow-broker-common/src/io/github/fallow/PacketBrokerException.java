package io.github.fallow;

public final class PacketBrokerException extends IllegalStateException {

    PacketBrokerException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
