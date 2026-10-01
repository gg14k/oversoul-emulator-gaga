package org.gaga.oversoul.dto.client.equip;

public record EquipCharacterDTO(
        String type,
        String cmd,
        String senderName,
        long body
) {
}