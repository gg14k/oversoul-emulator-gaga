package org.gaga.oversoul.game.character;

public record CharacterDefinition(
        long id,
        String name,
        int characterTypeId,
        Long prerequisiteCharacterId,
        Long prerequisiteItemId,
        String filename,
        String attach,
        int elementId,
        int captureRate,
        int cost,
        int levelRequirement,
        Integer alignment,
        Integer storeGold,
        Integer storeGem,
        Boolean sellable
) {
}