package org.gaga.oversoul.data.repository.game;

import org.gaga.oversoul.data.cache.AbstractJooqCachedRepository;
import org.gaga.oversoul.data.cache.index.GroupIndex;
import org.gaga.oversoul.data.cache.index.SecondaryIndex;
import org.gaga.oversoul.database.DatabaseKey;
import org.gaga.oversoul.database.util.JooqConverters;
import org.gaga.oversoul.game.character.CharacterDefinition;
import org.jooq.DSLContext;
import org.jooq.Field;

import java.util.List;
import java.util.Locale;

import static org.gaga.oversoul.jooq.tables.Characters.CHARACTERS;

/**
 * Cache global das definições dos personagens.
 */
public final class CharactersRepository
        extends AbstractJooqCachedRepository<Long, CharacterDefinition> {

    /**
     * Nome -> CharacterDefinition
     *
     * Exemplo:
     * "skeleton" -> Skeleton
     */
    public final SecondaryIndex<CharacterDefinition, String> byName;

    /**
     * Element ID -> Characters
     */
    public final GroupIndex<CharacterDefinition, Integer> byElement;

    /**
     * Character Type ID -> Characters
     */
    public final GroupIndex<CharacterDefinition, Integer> byType;

    public CharactersRepository() {
        super(DatabaseKey.GAME);

        byName = secondaryIndex(
                "characters.byName",
                character -> normalizeName(
                        character.name()
                ),
                SecondaryIndex.DuplicatePolicy.FAIL
        );

        byElement = groupIndex(
                "characters.byElement",
                CharacterDefinition::elementId
        );

        byType = groupIndex(
                "characters.byType",
                CharacterDefinition::characterTypeId
        );
    }

    @Override
    protected List<CharacterDefinition> fetchAll(
            DSLContext dsl
    ) {
        /*
         * Isso evita depender de como o MySQL/jOOQ
         * resolveu mapear BOOLEAN/TINYINT.
         */
        Field<Boolean> SELLABLE =
                CHARACTERS.SELLABLE.cast(Boolean.class);

        return dsl
                .select(
                        CHARACTERS.CHARACTER_ID,
                        CHARACTERS.NAME,
                        CHARACTERS.CHARACTER_TYPE_ID,
                        CHARACTERS.PREREQUISITE_CHARACTER_ID,
                        CHARACTERS.PREREQUISITE_ITEM_ID,
                        CHARACTERS.FILENAME,
                        CHARACTERS.ATTACH,
                        CHARACTERS.ELEMENT_ID,
                        CHARACTERS.CAPTURE_RATE,
                        CHARACTERS.COST,
                        CHARACTERS.LEVEL_REQUIREMENT,
                        CHARACTERS.ALIGNMENT,
                        CHARACTERS.STORE_GOLD,
                        CHARACTERS.STORE_GEM,
                        SELLABLE
                )
                .from(CHARACTERS)
                .fetch(record ->
                        new CharacterDefinition(
                                JooqConverters.toLong(
                                        record.get(
                                                CHARACTERS.CHARACTER_ID
                                        )
                                ),

                                record.get(
                                        CHARACTERS.NAME
                                ),

                                JooqConverters.toInt(
                                        record.get(
                                                CHARACTERS.CHARACTER_TYPE_ID
                                        )
                                ),

                                JooqConverters.toNullableLong(
                                        record.get(
                                                CHARACTERS.PREREQUISITE_CHARACTER_ID
                                        )
                                ),

                                record.get(
                                        CHARACTERS.PREREQUISITE_ITEM_ID
                                ),

                                record.get(
                                        CHARACTERS.FILENAME
                                ),

                                record.get(
                                        CHARACTERS.ATTACH
                                ),

                                JooqConverters.toInt(
                                        record.get(
                                                CHARACTERS.ELEMENT_ID
                                        )
                                ),

                                JooqConverters.toInt(
                                        record.get(
                                                CHARACTERS.CAPTURE_RATE
                                        )
                                ),

                                JooqConverters.toInt(
                                        record.get(
                                                CHARACTERS.COST
                                        )
                                ),

                                JooqConverters.toInt(
                                        record.get(
                                                CHARACTERS.LEVEL_REQUIREMENT
                                        )
                                ),

                                record.get(
                                        CHARACTERS.ALIGNMENT
                                ),

                                JooqConverters.toInt(
                                        record.get(
                                                CHARACTERS.STORE_GOLD
                                        )
                                ),

                                JooqConverters.toInt(
                                        record.get(
                                                CHARACTERS.STORE_GEM
                                        )
                                ),

                                Boolean.TRUE.equals(
                                        record.get(SELLABLE)
                                )
                        )
                );
    }

    @Override
    protected Long keyOf(
            CharacterDefinition character
    ) {
        return character.id();
    }

    /**
     * Busca por nome ignorando maiúsculas/minúsculas
     * e espaços extras.
     */
    public CharacterDefinition getByName(
            String name
    ) {
        if (name == null) {
            return null;
        }

        return byName.get(
                normalizeName(name)
        );
    }

    private static String normalizeName(
            String value
    ) {
        if (value == null) {
            return null;
        }

        return value
                .trim()
                .toLowerCase(Locale.ROOT);
    }
}