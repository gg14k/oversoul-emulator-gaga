package org.gaga.oversoul.data.repository.game;

import org.gaga.oversoul.data.cache.AbstractJooqCachedRepository;
import org.gaga.oversoul.data.cache.index.GroupIndex;
import org.gaga.oversoul.data.cache.index.SecondaryIndex;
import org.gaga.oversoul.database.DatabaseKey;
import org.gaga.oversoul.game.card.CardDefinition;
import org.jooq.DSLContext;

import java.util.List;
import java.util.Locale;

import static org.gaga.oversoul.database.util.JooqConverters.toInt;
import static org.gaga.oversoul.database.util.JooqConverters.toLong;
import static org.gaga.oversoul.jooq.tables.Cards.CARDS;

public final class CardsRepository
        extends AbstractJooqCachedRepository<Long, CardDefinition> {

    public final GroupIndex<CardDefinition, String> byName;
    public final GroupIndex<CardDefinition, Integer> byType;
    public final GroupIndex<CardDefinition, Integer> byElement;

    public CardsRepository() {
        super(DatabaseKey.GAME);

        byName = groupIndex(
                "cards.byName",
                card -> normalizeName(card.name())
        );

        byType = groupIndex(
                "cards.byType",
                CardDefinition::cardTypeId
        );

        byElement = groupIndex(
                "cards.byElement",
                CardDefinition::elementId
        );
    }

    @Override
    protected List<CardDefinition> fetchAll(DSLContext dsl) {
        return dsl
                .selectFrom(CARDS)
                .fetch(record ->
                        new CardDefinition(
                                toLong(record.get(CARDS.CARD_ID)),
                                record.get(CARDS.NAME),
                                record.get(CARDS.DESCRIPTION),
                                toInt(record.get(CARDS.CARD_TYPE_ID)),
                                toInt(record.get(CARDS.ENERGY_COST)),
                                toInt(record.get(CARDS.ELEMENT_ID)),
                                record.get(CARDS.SKILL),
                                record.get(CARDS.FILENAME),
                                record.get(CARDS.ATTACH),
                                toInt(record.get(CARDS.DEFAULT_COUNT))
                        )
                );
    }

    @Override
    protected Long keyOf(CardDefinition card) { return card.id(); }

    public List<CardDefinition> getByName(
            String name
    ) {
        if (name == null) {
            return List.of();
        }

        return byName.get(
                normalizeName(name)
        );
    }

    private static String normalizeName(String name) {
        if (name == null) { return null; }
        return name.trim().toLowerCase(Locale.ROOT);
    }
}