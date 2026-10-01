package org.gaga.oversoul.dto.client.room;

import org.gaga.oversoul.dto.client.player.LoginCharacterInventoryDTO;

import java.util.List;

public record RoomPlayerDTO(
        RoomPlayerDataDTO data,
        String location,
        boolean hidden,
        List<LoginCharacterInventoryDTO> charInventory
) {
}