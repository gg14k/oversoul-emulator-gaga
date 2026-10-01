package org.gaga.oversoul.dto.client.player;

public record LoginCharacterInventoryDTO(
        long idAdjPlayerCharacter,
        long idPlayer,
        long idCharacter,
        int intUnlocked,
        int intLevel,
        int intExp,
        boolean bDefault
) {
}