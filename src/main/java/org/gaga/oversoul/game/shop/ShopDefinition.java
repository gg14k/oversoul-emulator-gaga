package org.gaga.oversoul.game.shop;

/**
 * Definição global de uma loja.
 */
public record ShopDefinition(
        long id,
        int shopType,
        int shopIndex,
        String name,
        String description
) {
}