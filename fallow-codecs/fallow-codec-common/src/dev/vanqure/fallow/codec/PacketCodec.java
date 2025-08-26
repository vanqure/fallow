package dev.vanqure.fallow.codec;

import dev.vanqure.fallow.Packet;

public interface PacketCodec {

    Packet deserialize(byte[] serializedData) throws PacketDeserializationException;

    byte[] serialize(Packet packet) throws PacketSerializationException;
}
