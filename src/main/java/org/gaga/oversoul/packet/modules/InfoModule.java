package org.gaga.oversoul.packet.modules;

import org.gaga.oversoul.dto.client.info.InfoCharactersDTO;
import org.gaga.oversoul.dto.client.player.LoginCharacterDTO;
import org.gaga.oversoul.game.character.CharacterDefinition;
import org.gaga.oversoul.packet.IPacketModule;
import org.gaga.oversoul.packet.mapper.PlayerPacketMapper;
import org.gaga.oversoul.packet.request.PacketData;
import org.gaga.oversoul.player.PlayerEntity;
import org.gaga.oversoul.service.GlobalService;

import java.util.Arrays;
import java.util.List;

public final class InfoModule
        implements IPacketModule {

    @Override
    public void handle(
            PlayerEntity player,
            PacketData packet
    ) {
        String command =
                packet.getString("cmd");

        if (!"char".equals(command)) {
            return;
        }

        String body =
                packet.getString(
                        "body",
                        ""
                );

        List<Long> ids =
                Arrays
                        .stream(
                                body.split(",")
                        )
                        .map(String::trim)
                        .filter(value ->
                                !value.isEmpty()
                        )
                        .map(Long::parseLong)
                        .distinct()
                        .sorted()
                        .toList();

        List<LoginCharacterDTO> characters =
                ids
                        .stream()
                        .map(id ->
                                GlobalService
                                        .gameCaches()
                                        .characters
                                        .get(id)
                        )
                        .filter(character ->
                                character != null
                        )
                        .map(PlayerPacketMapper::character)
                        .toList();

        player.send(
                new InfoCharactersDTO(
                        "info",
                        "char",
                        characters
                )
        );
    }
}