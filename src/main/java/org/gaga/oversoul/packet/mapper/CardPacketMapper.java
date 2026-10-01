package org.gaga.oversoul.packet.mapper;

import org.gaga.oversoul.dto.client.card.CardDTO;
import org.gaga.oversoul.dto.client.card.CardInventoryDTO;
import org.gaga.oversoul.game.cache.GameCaches;
import org.gaga.oversoul.game.card.CardDefinition;
import org.gaga.oversoul.game.card.CharacterCardDefinition;
import org.gaga.oversoul.player.PlayerEntity;
import org.gaga.oversoul.player.character.PlayerCharacter;
import org.gaga.oversoul.utils.GameLogger;

import java.util.List;

public final class CardPacketMapper {

    private CardPacketMapper() {
    }

    public static List<CardDTO> cards(
            PlayerEntity player,
            GameCaches caches
    ) {
        PlayerCharacter activeCharacter =
                player.getActiveCharacter();

        if (activeCharacter == null) {
            return List.of();
        }

        return caches
                .characterCards
                .getByCharacter(
                        activeCharacter.characterId()
                )
                .stream()
                .map(CharacterCardDefinition::cardId)
                .distinct()
                .map(caches.cards::get)
                .filter(card -> {
                    if (card != null) {
                        return true;
                    }

                    GameLogger.warning(
                            "Missing card definition in cache."
                    );

                    return false;
                })
                .map(CardPacketMapper::card)
                .toList();
    }

    public static List<CardInventoryDTO> inventory(
            PlayerEntity player,
            GameCaches caches
    ) {
        PlayerCharacter activeCharacter =
                player.getActiveCharacter();

        if (activeCharacter == null) {
            return List.of();
        }

        return caches
                .characterCards
                .getByCharacter(
                        activeCharacter.characterId()
                )
                .stream()
                .map(CharacterCardDefinition::cardId)
                .distinct()
                .map(caches.cards::get)
                .filter(card ->
                        card != null
                                && card.defaultCount() > 0
                )
                .map(card ->
                        inventoryEntry(
                                player,
                                card
                        )
                )
                .toList();
    }

    public static CardDTO card(
            CardDefinition card
    ) {
        return new CardDTO(
                card.id(),
                card.name(),
                card.description(),
                card.cardTypeId(),
                card.energyCost(),
                card.elementId(),
                card.skill(),
                card.filename(),
                card.attach()
        );
    }

    private static CardInventoryDTO inventoryEntry(
            PlayerEntity player,
            CardDefinition card
    ) {
        int count =
                card.defaultCount();

        return new CardInventoryDTO(
                temporaryPlayerCardId(
                        player.getPlayerId(),
                        card.id()
                ),
                player.getPlayerId(),
                card.id(),
                count,
                count
        );
    }

    /**
     * Generates a stable compatibility identifier until
     * persistent player card inventory is implemented.
     */
    private static long temporaryPlayerCardId(
            long playerId,
            long cardId
    ) {
        return (playerId << 32)
                | (cardId & 0xFFFFFFFFL);
    }
}