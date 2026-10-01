package org.gaga.oversoul.config;

import org.gaga.oversoul.dto.config.DatabaseDTO;
import org.gaga.oversoul.dto.config.NetworkDTO;
import org.gaga.oversoul.dto.config.PacketsDTO;
import org.gaga.oversoul.dto.config.ServerDTO;

import java.nio.file.Path;

public final class ConfigRegistry {

    public static PacketsDTO PACKETS;
    public static DatabaseDTO DATABASE;
    public static NetworkDTO NETWORK;
    public static ServerDTO SERVER;

    public static void loadAll() {
        DATABASE = ConfigLoader.load(Path.of("gaga/config/database.json"), DatabaseDTO.class);
        NETWORK = ConfigLoader.load(Path.of("gaga/config/network.json"), NetworkDTO.class);
        SERVER = ConfigLoader.load(Path.of("gaga/config/server.json"), ServerDTO.class);
        PACKETS = ConfigLoader.load(Path.of("gaga/config/packets.json"), PacketsDTO.class);
    }

    /** Reloads packets.json and replaces the cached instance. */
    public static PacketsDTO reloadPackets() {
        PACKETS = ConfigLoader.reload(Path.of("gaga/config/packets.json"), PacketsDTO.class);
        return PACKETS;
    }
}
