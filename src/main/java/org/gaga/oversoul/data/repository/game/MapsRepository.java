package org.gaga.oversoul.data.repository.game;

import org.gaga.oversoul.data.cache.AbstractJooqCachedRepository;
import org.gaga.oversoul.data.cache.index.GroupIndex;
import org.gaga.oversoul.data.cache.index.SecondaryIndex;
import org.gaga.oversoul.database.DatabaseKey;
import org.gaga.oversoul.game.map.MapDefinition;
import org.jooq.DSLContext;

import java.util.List;
import java.util.Locale;

import static org.gaga.oversoul.database.util.JooqConverters.toInt;
import static org.gaga.oversoul.database.util.JooqConverters.toLong;
import static org.gaga.oversoul.jooq.tables.Maps.MAPS;

public final class MapsRepository
        extends AbstractJooqCachedRepository<Long, MapDefinition> {

    public final SecondaryIndex<MapDefinition, String> byName;
    public final GroupIndex<MapDefinition, Integer> byInstanceType;

    public MapsRepository() {
        super(DatabaseKey.GAME);

        byName = secondaryIndex(
                "maps.byName",
                map -> normalizeName(map.name()),
                SecondaryIndex.DuplicatePolicy.FAIL
        );

        byInstanceType = groupIndex(
                "maps.byInstanceType",
                MapDefinition::instanceType
        );
    }

    @Override
    protected List<MapDefinition> fetchAll(
            DSLContext dsl
    ) {
        return dsl
                .selectFrom(MAPS)
                .fetch(record ->
                        new MapDefinition(
                                toLong(
                                        record.get(MAPS.MAP_ID)
                                ),

                                toInt(record.get(
                                        MAPS.INSTANCE_TYPE
                                )),

                                record.get(
                                        MAPS.FILENAME
                                ),

                                record.get(
                                        MAPS.NAME
                                )
                        )
                );
    }

    @Override
    protected Long keyOf(MapDefinition map) {
        return map.id();
    }

    public MapDefinition getByName(String name) {
        if (name == null) {
            return null;
        }

        return byName.get(
                normalizeName(name)
        );
    }

    private static String normalizeName(String name) {
        if (name == null) {
            return null;
        }

        return name
                .trim()
                .toLowerCase(Locale.ROOT);
    }
}