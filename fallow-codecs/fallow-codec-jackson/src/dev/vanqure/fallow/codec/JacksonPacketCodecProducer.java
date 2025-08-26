package dev.vanqure.fallow.codec;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
import com.fasterxml.jackson.databind.json.JsonMapper;

public final class JacksonPacketCodecProducer {

    private JacksonPacketCodecProducer() {}

    public static PacketCodec produceCodec() {
        final JsonMapper mapper =
                JsonMapper.builder()
                        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                        .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                        .enable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN)
                        .build();
        mapper.setVisibility(
                VisibilityChecker.Std.defaultInstance()
                        .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                        .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                        .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                        .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
        return produceCodec(mapper);
    }

    public static PacketCodec produceCodec(final ObjectMapper objectMapper) {
        return new JacksonPacketCodec(objectMapper);
    }
}
