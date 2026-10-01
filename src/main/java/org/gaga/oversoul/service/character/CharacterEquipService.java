package org.gaga.oversoul.service.character;

import org.gaga.oversoul.database.repository.PlayerRepository;
import org.gaga.oversoul.dto.client.equip.EquipCharacterDTO;
import org.gaga.oversoul.game.cache.GameCaches;
import org.gaga.oversoul.player.PlayerEntity;
import org.gaga.oversoul.player.character.PlayerCharacter;
import org.gaga.oversoul.service.room.RoomService;
import org.gaga.oversoul.utils.GameLogger;
import org.gaga.oversoul.world.room.GameRoom;

public final class CharacterEquipService {

    private final PlayerRepository playerRepository;
    private final RoomService roomService;
    private final GameCaches gameCaches;

    public CharacterEquipService(
            PlayerRepository playerRepository,
            RoomService roomService,
            GameCaches gameCaches
    ) {
        this.playerRepository =
                playerRepository;

        this.roomService =
                roomService;

        this.gameCaches =
                gameCaches;
    }

    public boolean equip(
            PlayerEntity player,
            long playerCharacterId,
            long characterId
    ) {
        PlayerCharacter ownedCharacter =
                player.getOwnedCharacter(
                        playerCharacterId
                );

        if (ownedCharacter == null) {
            GameLogger.warning(
                    "Player '{}' attempted to equip unknown player character {}.",
                    player.getUsername(),
                    playerCharacterId
            );

            return false;
        }

        if (ownedCharacter.characterId()
                != characterId) {

            GameLogger.warning(
                    "Player '{}' sent mismatched character equip data. PlayerCharacter={}, requestedCharacter={}.",
                    player.getUsername(),
                    playerCharacterId,
                    characterId
            );

            return false;
        }

        if (gameCaches
                .characters
                .get(characterId) == null) {

            GameLogger.warning(
                    "Character definition {} was not found in cache.",
                    characterId
            );

            return false;
        }

        boolean persisted =
                playerRepository
                        .equipCharacter(
                                player.getPlayerId(),
                                playerCharacterId,
                                characterId
                        );

        if (!persisted) {
            GameLogger.warning(
                    "Failed to persist character equip for player '{}'.",
                    player.getUsername()
            );

            return false;
        }

        player.setActivePlayerCharacterId(
                playerCharacterId
        );

        EquipCharacterDTO response =
                new EquipCharacterDTO(
                        "equip",
                        "char",
                        player
                                .getProfileData()
                                .displayName(),
                        characterId
                );

        broadcast(
                player,
                response
        );

        GameLogger.info(
                "Player '{}' equipped character {} using player character {}.",
                player.getUsername(),
                characterId,
                playerCharacterId
        );

        return true;
    }

    private void broadcast(
            PlayerEntity player,
            EquipCharacterDTO packet
    ) {
        long roomId =
                player
                        .getContext()
                        .getCurrentRoomId();

        GameRoom room =
                roomService.getRoom(
                        roomId
                );

        if (room == null) {
            player.send(packet);
            return;
        }

        room.broadcast(packet);
    }
}