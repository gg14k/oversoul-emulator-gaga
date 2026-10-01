package org.gaga.oversoul.repository;

import org.gaga.oversoul.player.character.PlayerCharacter;
import org.jooq.DSLContext;
import org.jooq.types.ULong;

import java.util.List;

import static org.gaga.oversoul.database.util.JooqConverters.toLong;
import static org.gaga.oversoul.jooq.tables.PlayerCharacters.PLAYER_CHARACTERS;

public final class PlayerCharactersRepository {

    private final DSLContext dsl;

    public PlayerCharactersRepository(
            DSLContext dsl
    ) {
        this.dsl = dsl;
    }

    public List<PlayerCharacter> findByPlayerId(
            long playerId
    ) {
        return dsl
                .selectFrom(PLAYER_CHARACTERS)
                .where(
                        PLAYER_CHARACTERS.PLAYER_ID.eq(
                                ULong.valueOf(playerId)
                        )
                )
                .orderBy(
                        PLAYER_CHARACTERS.PLAYER_CHARACTER_ID.asc()
                )
                .fetch(record ->
                        new PlayerCharacter(
                                toLong(
                                        record.getPlayerCharacterId()
                                ),

                                toLong(
                                        record.getCharacterId()
                                ),

                                record
                                        .getLevel()
                                        .intValue(),

                                record
                                        .getExperience()
                                        .intValue()
                        )
                );
    }
}