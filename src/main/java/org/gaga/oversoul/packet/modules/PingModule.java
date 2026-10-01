package org.gaga.oversoul.packet.modules;

import org.gaga.oversoul.packet.IPacketModule;
import org.gaga.oversoul.packet.request.PacketData;
import org.gaga.oversoul.player.PlayerEntity;

public final class PingModule implements IPacketModule {

    @Override
    public void handle(
            PlayerEntity player,
            PacketData packet
    ) {
        // Heartbeat packet.
        // No response is required by the client.
    }
}