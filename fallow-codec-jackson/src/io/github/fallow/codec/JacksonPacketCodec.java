package io.github.fallow.codec;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.fallow.Packet;
import java.util.Arrays;

final class JacksonPacketCodec implements PacketCodec {

    private final ObjectMapper mapper;

    JacksonPacketCodec(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Packet deserialize(byte[] serializedData) throws PacketDeserializationException {
        try {
            return mapper.readValue(serializedData, JacksonPacket.class);
        } catch (Exception exception) {
            throw new PacketDeserializationException(
                    "Couldn't deserialize packet %s using Jackson codec".formatted(Arrays.toString(serializedData)),
                    exception);
        }
    }

    @Override
    public byte[] serialize(Packet packet) throws PacketSerializationException {
        try {
            return mapper.writeValueAsBytes(packet);
        } catch (Exception exception) {
            throw new PacketSerializationException(
                    "Couldn't serialize packet %s using Jackson codec".formatted(packet), exception);
        }
    }
}
