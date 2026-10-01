package org.gaga.oversoul.repository;

import org.gaga.oversoul.player.data.PlayerProfileData;
import org.jooq.DSLContext;
import org.jooq.types.ULong;

import java.util.Optional;

import static org.gaga.oversoul.database.util.JooqConverters.toLong;
import static org.gaga.oversoul.jooq.tables.PlayerProfiles.PLAYER_PROFILES;

public final class PlayerProfileRepository {

    private final DSLContext dsl;

    public PlayerProfileRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    public Optional<PlayerProfileData> findByPlayerId(
            long playerId
    ) {
        var record = dsl
                .selectFrom(PLAYER_PROFILES)
                .where(
                        PLAYER_PROFILES.PLAYER_ID.eq(
                                ULong.valueOf(playerId)
                        )
                )
                .fetchOne();

        if (record == null) {
            return Optional.empty();
        }

        return Optional.of(
                new PlayerProfileData(
                        toLong(record.getPlayerId()),

                        record.getDisplayName(),

                        record.getGold().intValue(),
                        record.getGems().intValue(),
                        record.getAlignment(),
                        record.getAp(),
                        record.getBlock(),
                        record.getChaos(),
                        record.getCriticalHit(),
                        record.getDp(),
                        record.getEarth(),
                        record.getEnergy(),
                        record.getFire(),
                        record.getHits(),
                        record.getHomeTown(),
                        record.getIce(),
                        record.getInitiative(),
                        record.getLight(),
                        record.getLuck(),
                        record.getNeutral(),
                        record.getPower(),
                        record.getStamina(),
                        record.getShadow(),
                        record.getWater()
                )
        );
    }
}