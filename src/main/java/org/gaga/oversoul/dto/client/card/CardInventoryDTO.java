package org.gaga.oversoul.dto.client.card;

public record CardInventoryDTO(
        long idPlayerCard,
        long idPlayer,
        long idCard,
        int intCount,
        int intEquipped
) {
}