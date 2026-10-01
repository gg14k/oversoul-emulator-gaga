package org.gaga.oversoul.dto.client.room;

public record RoomPlayerDataDTO(
        String name,
        Boolean bitFounder,
        int intAccessLevel,
        int element,
        long idPlayer,
        int intAlignment,
        int intAP,
        int intBlock,
        int intChaos,
        int intCriticalHit,
        int intDP,
        int intEarth,
        int intEnergy,
        int intFire,
        int intHits,
        int intHometown,
        int intIce,
        int intInitiative,
        int intLuck,
        int intNeutral,
        int intPower,
        int intStamina,
        int intShadow,
        int intWater
) {
}