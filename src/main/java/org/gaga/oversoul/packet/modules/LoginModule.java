package org.gaga.oversoul.packet.modules;

import org.gaga.oversoul.packet.IPacketModule;
import org.gaga.oversoul.packet.mapper.LoginPacketMapper;
import org.gaga.oversoul.packet.request.PacketData;
import org.gaga.oversoul.player.PlayerEntity;
import org.gaga.oversoul.service.GlobalService;
import org.gaga.oversoul.service.login.LoginResult;
import org.gaga.oversoul.utils.GameLogger;

public final class LoginModule
        implements IPacketModule {

    @Override
    public void handle(
            PlayerEntity player,
            PacketData packet
    ) {
        long playerId =
                packet.getLong("id");

        String token =
                packet.getString("Token");

        LoginResult result =
                GlobalService
                        .loginService()
                        .login(
                                player,
                                playerId,
                                token
                        );

        if (!result.isSuccess()) {
            GameLogger.warning(
                    "Login rejected for player {}: {}",
                    playerId,
                    result.status()
            );

            player
                    .getSession()
                    .disconnect(
                            "Login rejected"
                    );

            return;
        }

        player.send(
                LoginPacketMapper.data(
                        player,
                        GlobalService.gameCaches()
                )
        );

        boolean joined =
                GlobalService
                        .roomService()
                        .join(
                                player,
                                "solace"
                        );

        if (!joined) {
            GameLogger.error(
                    "Unable to place player '"
                            + player.getUsername()
                            + "' in the initial map.", null
            );

            player
                    .getSession()
                    .disconnect(
                            "Initial map unavailable"
                    );

            return;
        }

        GameLogger.info(
                "Login flow completed for player '{}'.",
                player.getUsername()
        );
    }
}