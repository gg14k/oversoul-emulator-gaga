package org.gaga.oversoul.game.card;

/**
 * Associação entre um personagem e uma carta.
 */
public record CharacterCardDefinition(
        long characterId,
        long cardId,
        int slotOrder
) {
}