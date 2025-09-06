package io.github.fallow.codec;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.github.fallow.Packet;
import java.io.Serializable;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public abstract class JacksonPacket extends Packet implements Serializable {}
