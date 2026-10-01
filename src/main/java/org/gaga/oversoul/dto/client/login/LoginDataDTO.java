package org.gaga.oversoul.dto.client.login;

import org.gaga.oversoul.dto.client.card.CardDTO;
import org.gaga.oversoul.dto.client.card.CardInventoryDTO;
import org.gaga.oversoul.dto.client.player.LoginCharacterDTO;
import org.gaga.oversoul.dto.client.player.LoginCharacterInventoryDTO;
import org.gaga.oversoul.dto.client.player.LoginPlayerDTO;

import java.util.List;

public record LoginDataDTO(
        int status,
        String type,
        LoginPlayerDTO player,
        List<LoginCharacterDTO> characters,
        List<LoginCharacterInventoryDTO> charInventory,
        List<Object> items,
        List<Object> itemInventory,
        List<CardDTO> cards,
        List<CardInventoryDTO> cardInventory
) {
}