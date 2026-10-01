package org.gaga.oversoul.game.shop;

/**
 * Associação entre uma loja e um personagem.
 */
public record ShopCharacterDefinition(
        long shopId,
        long characterId,
        int slotOrder
) {
}