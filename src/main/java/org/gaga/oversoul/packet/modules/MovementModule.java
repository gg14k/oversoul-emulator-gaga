package org.gaga.oversoul.packet.modules;

import org.gaga.oversoul.dto.client.movement.MovementDTO;
import org.gaga.oversoul.packet.IPacketModule;
import org.gaga.oversoul.packet.request.PacketData;
import org.gaga.oversoul.player.PlayerEntity;
import org.gaga.oversoul.service.GlobalService;
import org.gaga.oversoul.world.room.GameRoom;

public final class MovementModule
        implements IPacketModule {

    @Override
    public void handle(
            PlayerEntity player,
            PacketData packet
    ) {
        String command =
                packet.getString(
                        "cmd"
                );

        if (!"mv".equals(command)) {
            return;
        }

        long roomId =
                packet.getLong(
                        "RoomID"
                );

        String body =
                packet.getString(
                        "body"
                );

        if (player
                .getContext()
                .getCurrentRoomId() != roomId) {
            return;
        }

        GameRoom room =
                GlobalService
                        .roomService()
                        .getRoom(
                                roomId
                        );

        if (room == null) {
            return;
        }

        player
                .getContext()
                .setLocation(
                        body
                );

        MovementDTO response =
                new MovementDTO(
                        "movement",
                        "mv",
                        player
                                .getProfileData()
                                .displayName(),
                        room.id(),
                        body
                );

        room.broadcast(
                response
        );
    }
}