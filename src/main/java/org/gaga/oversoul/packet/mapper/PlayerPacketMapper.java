package org.gaga.oversoul.packet.mapper;

import org.gaga.oversoul.dto.client.player.LoginCharacterDTO;
import org.gaga.oversoul.dto.client.player.LoginCharacterInventoryDTO;
import org.gaga.oversoul.dto.client.player.LoginPlayerDTO;
import org.gaga.oversoul.game.cache.GameCaches;
import org.gaga.oversoul.game.character.CharacterDefinition;
import org.gaga.oversoul.player.PlayerEntity;
import org.gaga.oversoul.player.character.PlayerCharacter;
import org.gaga.oversoul.player.data.AccountData;
import org.gaga.oversoul.player.data.PlayerProfileData;
import org.gaga.oversoul.utils.GameLogger;

import java.util.Comparator;
import java.util.List;

public final class PlayerPacketMapper {

    private PlayerPacketMapper() {
    }

    public static LoginPlayerDTO loginPlayer(PlayerEntity player) {
        PlayerProfileData profile = player.getProfileData();

        return new LoginPlayerDTO(
                profile.displayName(),
                50,
                null,
                1,
                player.getPlayerId(),
                profile.gold(),
                profile.gems(),
                null,
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
                profile.light(),
                profile.luck(),
                profile.neutral(),
                profile.power(),
                profile.stamina(),
                profile.shadow(),
                profile.water(),
                null,
                null
        );
    }

    public static List<LoginCharacterInventoryDTO> inventory(PlayerEntity player) {
        Long activeId = player.getActivePlayerCharacterId();

        return player
                .getCharacters()
                .stream()
                .sorted(
                        Comparator.comparingLong(
                                PlayerCharacter::playerCharacterId
                        )
                )
                .map(character ->
                        inventoryEntry(
                                player,
                                character,
                                activeId != null
                                        && activeId == character.playerCharacterId()
                        )
                )
                .toList();
    }

    public static List<LoginCharacterInventoryDTO> activeInventory(
            PlayerEntity player
    ) {
        PlayerCharacter character =
                player.getActiveCharacter();

        if (character == null) {
            return List.of();
        }

        return List.of(
                new LoginCharacterInventoryDTO(
                        character.playerCharacterId(),
                        player.getPlayerId(),
                        character.characterId(),
                        1,
                        character.level(),
                        character.experience(),
                        true
                )
        );
    }

    public static List<LoginCharacterDTO> characters(
            PlayerEntity player,
            GameCaches caches
    ) {
        return player
                .getCharacters()
                .stream()
                .map(PlayerCharacter::characterId)
                .distinct()
                .map(caches.characters::get)
                .filter(character -> {
                    if (character != null) {
                        return true;
                    }

                    GameLogger.warning(
                            "Character definition missing from cache."
                    );

                    return false;
                })
                .map(PlayerPacketMapper::character)
                .toList();
    }

    public static LoginCharacterDTO character(
            CharacterDefinition character
    ) {
        long prerequisiteCharacter =
                character.prerequisiteCharacterId() == null
                        ? -1
                        : character.prerequisiteCharacterId();

        long prerequisiteItem =
                character.prerequisiteItemId() == null
                        ? -1
                        : character.prerequisiteItemId();

        long owner =
                character.prerequisiteCharacterId() == null
                        ? character.id()
                        : character.prerequisiteCharacterId();

        return new LoginCharacterDTO(
                character.id(),
                character.name(),
                character.characterTypeId(),
                prerequisiteCharacter,
                owner,
                prerequisiteItem,
                character.filename(),
                character.attach(),
                character.elementId(),
                character.captureRate(),
                character.cost(),
                character.levelRequirement(),
                character.alignment(),
                character.storeGold(),
                character.storeGem(),
                character.sellable()
        );
    }

    private static LoginCharacterInventoryDTO inventoryEntry(
            PlayerEntity player,
            PlayerCharacter character,
            boolean selected
    ) {
        return new LoginCharacterInventoryDTO(
                character.playerCharacterId(),
                player.getPlayerId(),
                character.characterId(),
                1,
                character.level(),
                character.experience(),
                selected
        );
    }
}