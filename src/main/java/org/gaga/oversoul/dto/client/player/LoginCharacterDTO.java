package org.gaga.oversoul.dto.client.player;

public record LoginCharacterDTO(
        long idCharacter,
        String strName,
        int idCharacterType,
        long idCharacterPrereq,
        long idCharacterOwner,
        long idItemPrereq,
        String strFilename,
        String strAttach,
        int idElement,
        int intCaptureRate,
        int intCost,
        int intLevel,
        Integer intAlignment,
        Integer intStoreGold,
        Integer intStoreGem,
        Boolean bSellable
) {
}