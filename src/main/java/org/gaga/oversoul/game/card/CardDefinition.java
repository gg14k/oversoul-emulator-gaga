package org.gaga.oversoul.game.card;

public record CardDefinition(
        long id,
        String name,
        String description,
        int cardTypeId,
        int energyCost,
        int elementId,
        String skill,
        String filename,
        String attach,
        int defaultCount
) {
}