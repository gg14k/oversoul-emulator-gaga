package org.gaga.oversoul.packet.registry;

import java.util.List;

// Maps the root JSON object
public record PacketConfigDTO(List<PacketSpec> packets) {}