package org.gaga.oversoul.packet.mapper;

import org.gaga.oversoul.dto.client.login.LoginDataDTO;
import org.gaga.oversoul.game.cache.GameCaches;
import org.gaga.oversoul.player.PlayerEntity;

import java.util.List;

public final class LoginPacketMapper {

    private LoginPacketMapper() {
    }

    public static LoginDataDTO data(
            PlayerEntity player,
            GameCaches caches
    ) {
        return new LoginDataDTO(
                1,
                "login",

                PlayerPacketMapper.loginPlayer(
                        player
                ),

                PlayerPacketMapper.characters(
                        player,
                        caches
                ),

                PlayerPacketMapper.inventory(
                        player
                ),

                List.of(),

                List.of(),

                CardPacketMapper.cards(
                        player,
                        caches
                ),

                CardPacketMapper.inventory(
                        player,
                        caches
                )
        );
    }
}