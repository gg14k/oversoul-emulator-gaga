package org.gaga.oversoul.database.repository;

import org.gaga.oversoul.database.DatabaseConnectionHandler;
import org.gaga.oversoul.database.DatabaseKey;
import org.jooq.impl.DSL;
import org.jooq.types.ULong;

import static org.gaga.oversoul.jooq.tables.PlayerCharacters.PLAYER_CHARACTERS;
import static org.gaga.oversoul.jooq.tables.Players.PLAYERS;

public final class PlayerRepository {

    public boolean equipCharacter(
            long playerId,
            long playerCharacterId,
            long characterId
    ) {
        int affected =
                DatabaseConnectionHandler
                        .dsl(DatabaseKey.GAME)
                        .update(PLAYERS)
                        .set(
                                PLAYERS.ACTIVE_PLAYER_CHARACTER_ID,
                                ULong.valueOf(playerCharacterId)
                        )
                        .where(
                                PLAYERS.PLAYER_ID.eq(
                                        ULong.valueOf(playerId)
                                )
                        )
                        .and(
                                DSL.exists(
                                        DSL.selectOne()
                                                .from(PLAYER_CHARACTERS)
                                                .where(
                                                        PLAYER_CHARACTERS
                                                                .PLAYER_CHARACTER_ID
                                                                .eq(
                                                                        ULong.valueOf(playerCharacterId)
                                                                )
                                                )
                                                .and(
                                                        PLAYER_CHARACTERS
                                                                .PLAYER_ID
                                                                .eq(
                                                                        ULong.valueOf(playerId)
                                                                )
                                                )
                                                .and(
                                                        PLAYER_CHARACTERS
                                                                .CHARACTER_ID
                                                                .eq(
                                                                        ULong.valueOf(characterId)
                                                                )
                                                )
                                )
                        )
                        .execute();

        return affected == 1;
    }
}