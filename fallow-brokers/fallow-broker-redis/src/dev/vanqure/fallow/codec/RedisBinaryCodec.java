package dev.vanqure.fallow.codec;

import io.lettuce.core.codec.RedisCodec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public final class RedisBinaryCodec implements RedisCodec<String, byte[]> {

    public static final RedisBinaryCodec INSTANCE = new RedisBinaryCodec();

    RedisBinaryCodec() {}

    @Override
    public String decodeKey(final ByteBuffer buffer) {
        return StandardCharsets.UTF_8.decode(buffer).toString();
    }

    @Override
    public byte[] decodeValue(final ByteBuffer buffer) {
        final byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        return bytes;
    }

    @Override
    public ByteBuffer encodeKey(final String value) {
        return ByteBuffer.wrap(value.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public ByteBuffer encodeValue(final byte[] value) {
        return ByteBuffer.wrap(value);
    }
}
