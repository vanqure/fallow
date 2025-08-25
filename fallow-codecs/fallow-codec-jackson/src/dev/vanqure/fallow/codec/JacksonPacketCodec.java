package dev.vanqure.fallow.codec;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.vanqure.fallow.Packet;
import java.util.Arrays;

final class JacksonPacketCodec implements PacketCodec {

    private final ObjectMapper mapper;

    JacksonPacketCodec(final ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Packet deserialize(final byte[] serializedData) throws PacketCodecException {
        try {
            return mapper.readValue(serializedData, JacksonPacket.class);
        } catch (final Exception exception) {
            throw new PacketCodecException(
                    "Couldn't deserialize packet %s using Jackson codec".formatted(Arrays.toString(serializedData)),
                    exception);
        }
    }

    @Override
    public byte[] serialize(final Packet packet) throws PacketCodecException {
        try {
            return mapper.writeValueAsBytes(packet);
        } catch (final JsonProcessingException exception) {
            throw new PacketCodecException(
                    "Couldn't serialize packet %s using Jackson codec".formatted(packet), exception);
        }
    }
}
