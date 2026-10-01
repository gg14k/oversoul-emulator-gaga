package org.gaga.oversoul.packet.modules;

import org.gaga.oversoul.packet.IPacketModule;
import org.gaga.oversoul.packet.request.PacketData;
import org.gaga.oversoul.player.PlayerEntity;
import org.gaga.oversoul.service.GlobalService;

public final class EquipModule
        implements IPacketModule {

    @Override
    public void handle(
            PlayerEntity player,
            PacketData packet
    ) {
        String command =
                packet.getString("cmd");

        if (!"char".equals(command)) {
            return;
        }

        long characterId =
                packet.getLong("body");

        long playerCharacterId =
                packet.getLong(
                        "idAdjPlayerCharacter"
                );

        GlobalService
                .characterEquipService()
                .equip(
                        player,
                        playerCharacterId,
                        characterId
                );
    }
}