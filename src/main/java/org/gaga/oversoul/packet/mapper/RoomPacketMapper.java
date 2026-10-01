package org.gaga.oversoul.packet.mapper;

import org.gaga.oversoul.dto.client.room.RoomAddUserDTO;
import org.gaga.oversoul.dto.client.room.RoomDropUserDTO;
import org.gaga.oversoul.dto.client.room.RoomInfoDTO;
import org.gaga.oversoul.dto.client.room.RoomPacketDTO;
import org.gaga.oversoul.dto.client.room.RoomPlayerDTO;
import org.gaga.oversoul.dto.client.room.RoomPlayerDataDTO;
import org.gaga.oversoul.player.PlayerEntity;
import org.gaga.oversoul.player.data.PlayerProfileData;
import org.gaga.oversoul.world.room.GameRoom;

import java.util.Comparator;
import java.util.List;

public final class RoomPacketMapper {

    private RoomPacketMapper() {
    }

    public static RoomInfoDTO room(
            GameRoom room
    ) {
        return new RoomInfoDTO(
                room.id(),
                room.map().name(),
                room.map().filename(),
                room.instance()
        );
    }

    public static RoomPlayerDTO player(
            PlayerEntity player
    ) {
        PlayerProfileData profile =
                player.getProfileData();

        RoomPlayerDataDTO data =
                new RoomPlayerDataDTO(
                        profile.displayName(),
                        null,
                        0,
                        1,
                        player.getPlayerId(),
                        profile.alignment(),
                        profile.ap(),
                        profile.block(),
                        profile.chaos(),
                        profile.criticalHit(),
                        profile.dp(),
                        profile.earth(),
                        profile.energy(),
                        profile.fire(),
                        profile.hits(),
                        profile.homeTown(),
                        profile.ice(),
                        profile.initiative(),
                        profile.luck(),
                        profile.neutral(),
                        profile.power(),
                        profile.stamina(),
                        profile.shadow(),
                        profile.water()
                );

        return new RoomPlayerDTO(
                data,
                player
                        .getContext()
                        .getLocation(),
                player
                        .getContext()
                        .isHidden(),
                PlayerPacketMapper.activeInventory(
                        player
                )
        );
    }

    public static RoomPacketDTO join(
            GameRoom room,
            List<PlayerEntity> existingPlayers
    ) {
        return packet(
                "join",
                room,
                existingPlayers
        );
    }

    public static RoomPacketDTO list(
            GameRoom room
    ) {
        return packet(
                "list",
                room,
                room.players()
        );
    }

    public static RoomAddUserDTO addUser(
            GameRoom room,
            PlayerEntity player
    ) {
        RoomPlayerDTO roomPlayer =
                player(player);

        return new RoomAddUserDTO(
                0,
                "room",
                "adduser",
                "SERVER",
                room(room),
                roomPlayer,
                roomPlayer.location()
        );
    }

    public static RoomDropUserDTO dropUser(
            GameRoom room,
            String playerName
    ) {
        return new RoomDropUserDTO(
                0,
                "room",
                "dropuser",
                "SERVER",
                room(room),
                playerName
        );
    }

    private static RoomPacketDTO packet(
            String command,
            GameRoom room,
            List<PlayerEntity> players
    ) {
        List<RoomPlayerDTO> list =
                players
                        .stream()
                        .sorted(
                                Comparator.comparingLong(
                                        PlayerEntity::getPlayerId
                                )
                        )
                        .map(
                                RoomPacketMapper::player
                        )
                        .toList();

        return new RoomPacketDTO(
                0,
                "room",
                command,
                "SERVER",
                room(room),
                list
        );
    }
}