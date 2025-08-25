package dev.vanqure.fallow;

public final class PacketBrokerException extends IllegalStateException {

    PacketBrokerException(final String message, final Throwable throwable) {
        super(message, throwable);
    }
}
