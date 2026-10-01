package org.gaga.oversoul.player.character;

/**
 * Represents a character owned by a player.
 */
public record PlayerCharacter(
        long playerCharacterId,
        long characterId,
        int level,
        int experience
) {
}