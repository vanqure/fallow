package dev.vanqure.fallow.codec;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import dev.vanqure.fallow.Packet;
import java.io.Serializable;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public abstract class JacksonPacket extends Packet implements Serializable {}
