package io.github.fallow.codec;

import io.github.fallow.Packet;

public interface PacketCodec {

    Packet deserialize(byte[] serializedData) throws PacketDeserializationException;

    byte[] serialize(Packet packet) throws PacketSerializationException;
}
