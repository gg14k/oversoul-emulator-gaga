package org.gaga.oversoul.player.data;

public record PlayerProfileData(
        long playerId,
        String displayName,
        int gold,
        int gems,
        int alignment,
        int ap,
        int block,
        int chaos,
        int criticalHit,
        int dp,
        int earth,
        int energy,
        int fire,
        int hits,
        int homeTown,
        int ice,
        int initiative,
        int light,
        int luck,
        int neutral,
        int power,
        int stamina,
        int shadow,
        int water
) {
}