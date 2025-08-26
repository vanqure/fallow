package dev.vanqure.fallow.codec;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.vanqure.fallow.Packet;
import java.util.Arrays;

final class JacksonPacketCodec implements PacketCodec {

    private final ObjectMapper mapper;

    JacksonPacketCodec(final ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Packet deserialize(final byte[] serializedData) throws PacketDeserializationException {
        try {
            return mapper.readValue(serializedData, JacksonPacket.class);
        } catch (final Exception exception) {
            throw new PacketDeserializationException(
                    "Couldn't deserialize packet %s using Jackson codec".formatted(Arrays.toString(serializedData)),
                    exception);
        }
    }

    @Override
    public byte[] serialize(final Packet packet) throws PacketSerializationException {
        try {
            return mapper.writeValueAsBytes(packet);
        } catch (final Exception exception) {
            throw new PacketSerializationException(
                    "Couldn't serialize packet %s using Jackson codec".formatted(packet), exception);
        }
    }
}
