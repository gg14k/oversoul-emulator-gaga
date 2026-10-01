package org.gaga.oversoul.data.repository.game;

import org.gaga.oversoul.data.cache.AbstractJooqCachedRepository;
import org.gaga.oversoul.data.cache.index.GroupIndex;
import org.gaga.oversoul.database.DatabaseKey;
import org.gaga.oversoul.game.shop.ShopCharacterDefinition;
import org.gaga.oversoul.game.shop.ShopCharacterKey;
import org.jooq.DSLContext;

import java.util.List;

import static org.gaga.oversoul.database.util.JooqConverters.toInt;
import static org.gaga.oversoul.database.util.JooqConverters.toLong;
import static org.gaga.oversoul.jooq.tables.ShopCharacters.SHOP_CHARACTERS;

public final class ShopCharactersRepository
        extends AbstractJooqCachedRepository<
        ShopCharacterKey,
        ShopCharacterDefinition> {

    public final GroupIndex<ShopCharacterDefinition, Long> byShop;
    public final GroupIndex<ShopCharacterDefinition, Long> byCharacter;

    public ShopCharactersRepository() {
        super(DatabaseKey.GAME);

        byShop = groupIndex(
                "shopCharacters.byShop",
                ShopCharacterDefinition::shopId
        );

        byCharacter = groupIndex(
                "shopCharacters.byCharacter",
                ShopCharacterDefinition::characterId
        );
    }

    @Override
    protected List<ShopCharacterDefinition> fetchAll(
            DSLContext dsl
    ) {
        return dsl
                .selectFrom(SHOP_CHARACTERS)
                .orderBy(
                        SHOP_CHARACTERS.SHOP_ID.asc(),
                        SHOP_CHARACTERS.SLOT_ORDER.asc()
                )
                .fetch(record ->
                        new ShopCharacterDefinition(
                                toLong(
                                        record.get(
                                                SHOP_CHARACTERS.SHOP_ID
                                        )
                                ),

                                toLong(
                                        record.get(
                                                SHOP_CHARACTERS.CHARACTER_ID
                                        )
                                ),

                                toInt(record.get(
                                        SHOP_CHARACTERS.SLOT_ORDER
                                ))
                        )
                );
    }

    @Override
    protected ShopCharacterKey keyOf(
            ShopCharacterDefinition value
    ) {
        return new ShopCharacterKey(
                value.shopId(),
                value.characterId()
        );
    }

    public List<ShopCharacterDefinition> getByShop(
            long shopId
    ) {
        return byShop.get(shopId);
    }

    public List<ShopCharacterDefinition> getByCharacter(
            long characterId
    ) {
        return byCharacter.get(characterId);
    }
}