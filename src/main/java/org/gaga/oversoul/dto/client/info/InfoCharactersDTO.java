package org.gaga.oversoul.dto.client.info;

import org.gaga.oversoul.dto.client.player.LoginCharacterDTO;

import java.util.List;

public record InfoCharactersDTO(
        String type,
        String cmd,
        List<LoginCharacterDTO> characters
) {
}