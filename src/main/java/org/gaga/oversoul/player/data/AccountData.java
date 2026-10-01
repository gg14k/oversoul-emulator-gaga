package org.gaga.oversoul.player.data;

public record AccountData(
        long playerId,
        String username,
        Long activePlayerCharacterId
) {
}