package org.gaga.oversoul.dto.client.card;

public record CardDTO(
        long idCard,
        String strName,
        String strDescription,
        int idCardType,
        int intCost,
        int idElement,
        String strSkill,
        String strFilename,
        String strAttach
) {
}