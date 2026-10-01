package org.gaga.oversoul.dto.config;

import java.util.List;

public class PacketsDTO {
    public List<PacketEntry> packets = List.of();

    public static class PacketEntry {
        public String type;
        public String class_name;
        public long cooldown_ms;
        public boolean debug;
    }
}
