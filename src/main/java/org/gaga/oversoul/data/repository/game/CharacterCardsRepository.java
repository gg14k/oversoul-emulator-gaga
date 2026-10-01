package org.gaga.oversoul.data.repository.game;

import org.gaga.oversoul.data.cache.AbstractJooqCachedRepository;
import org.gaga.oversoul.data.cache.index.GroupIndex;
import org.gaga.oversoul.database.DatabaseKey;
import org.gaga.oversoul.game.card.CharacterCardDefinition;
import org.gaga.oversoul.game.card.CharacterCardKey;
import org.jooq.DSLContext;
import org.jooq.types.UShort;

import java.util.List;

import static org.gaga.oversoul.database.util.JooqConverters.toInt;
import static org.gaga.oversoul.database.util.JooqConverters.toLong;
import static org.gaga.oversoul.jooq.tables.CharacterCards.CHARACTER_CARDS;

public final class CharacterCardsRepository
        extends AbstractJooqCachedRepository<
        CharacterCardKey,
        CharacterCardDefinition> {

    public final GroupIndex<CharacterCardDefinition, Long> byCharacter;
    public final GroupIndex<CharacterCardDefinition, Long> byCard;

    public CharacterCardsRepository() {
        super(DatabaseKey.GAME);

        byCharacter = groupIndex(
                "characterCards.byCharacter",
                CharacterCardDefinition::characterId
        );

        byCard = groupIndex(
                "characterCards.byCard",
                CharacterCardDefinition::cardId
        );
    }

    @Override
    protected List<CharacterCardDefinition> fetchAll(
            DSLContext dsl
    ) {
        return dsl
                .selectFrom(CHARACTER_CARDS)
                .orderBy(
                        CHARACTER_CARDS.CHARACTER_ID.asc(),
                        CHARACTER_CARDS.SLOT_ORDER.asc()
                )
                .fetch(record ->
                        new CharacterCardDefinition(
                                toLong(
                                        record.get(
                                                CHARACTER_CARDS.CHARACTER_ID
                                        )
                                ),

                                toLong(
                                        record.get(
                                                CHARACTER_CARDS.CARD_ID
                                        )
                                ),

                                toInt(record.get(
                                        CHARACTER_CARDS.SLOT_ORDER
                                ))
                        )
                );
    }

    @Override
    protected CharacterCardKey keyOf(
            CharacterCardDefinition value
    ) {
        return new CharacterCardKey(
                value.characterId(),
                value.cardId()
        );
    }

    public List<CharacterCardDefinition> getByCharacter(
            long characterId
    ) {
        return byCharacter.get(characterId);
    }

    public List<CharacterCardDefinition> getByCard(
            long cardId
    ) {
        return byCard.get(cardId);
    }
}