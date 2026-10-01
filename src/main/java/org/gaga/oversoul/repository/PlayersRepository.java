package org.gaga.oversoul.repository;

import org.gaga.oversoul.player.data.AccountData;
import org.jooq.DSLContext;
import org.jooq.types.ULong;

import java.util.Optional;

import static org.gaga.oversoul.database.util.JooqConverters.toLong;
import static org.gaga.oversoul.database.util.JooqConverters.toNullableLong;
import static org.gaga.oversoul.jooq.tables.Players.PLAYERS;

public final class PlayersRepository {

    private final DSLContext dsl;

    public PlayersRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    /**
     * Legacy-compatible game login.
     *
     * The Flash client currently sends the stored password hash
     * as its Token. This can later be replaced by game sessions
     * without changing the packet protocol.
     */
    public Optional<AccountData> findByIdAndToken(
            long playerId,
            String token
    ) {
        var record = dsl
                .select(
                        PLAYERS.PLAYER_ID,
                        PLAYERS.USERNAME,
                        PLAYERS.ACTIVE_PLAYER_CHARACTER_ID
                )
                .from(PLAYERS)
                .where(
                        PLAYERS.PLAYER_ID.eq(
                                ULong.valueOf(playerId)
                        )
                )
                .and(
                        PLAYERS.PASSWORD_HASH.eq(token)
                )
                .fetchOne();

        if (record == null) {
            return Optional.empty();
        }

        return Optional.of(
                new AccountData(
                        toLong(
                                record.get(
                                        PLAYERS.PLAYER_ID
                                )
                        ),

                        record.get(
                                PLAYERS.USERNAME
                        ),

                        toNullableLong(
                                record.get(
                                        PLAYERS.ACTIVE_PLAYER_CHARACTER_ID
                                )
                        )
                )
        );
    }
}