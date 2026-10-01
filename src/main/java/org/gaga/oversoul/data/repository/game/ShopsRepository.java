package org.gaga.oversoul.data.repository.game;

import org.gaga.oversoul.data.cache.AbstractJooqCachedRepository;
import org.gaga.oversoul.data.cache.index.GroupIndex;
import org.gaga.oversoul.database.DatabaseKey;
import org.gaga.oversoul.game.shop.ShopDefinition;
import org.jooq.DSLContext;

import java.util.List;

import static org.gaga.oversoul.database.util.JooqConverters.toInt;
import static org.gaga.oversoul.database.util.JooqConverters.toLong;
import static org.gaga.oversoul.jooq.tables.Shops.SHOPS;

public final class ShopsRepository
        extends AbstractJooqCachedRepository<Long, ShopDefinition> {

    public final GroupIndex<ShopDefinition, Integer> byType;

    public ShopsRepository() {
        super(DatabaseKey.GAME);

        byType = groupIndex(
                "shops.byType",
                ShopDefinition::shopType
        );
    }

    @Override
    protected List<ShopDefinition> fetchAll(
            DSLContext dsl
    ) {
        return dsl
                .selectFrom(SHOPS)
                .fetch(record ->
                        new ShopDefinition(
                                toLong(
                                        record.get(SHOPS.SHOP_ID)
                                ),

                                toInt(record.get(
                                        SHOPS.SHOP_TYPE
                                )),

                                toInt(record.get(
                                        SHOPS.SHOP_INDEX
                                )),

                                record.get(
                                        SHOPS.NAME
                                ),

                                record.get(
                                        SHOPS.DESCRIPTION
                                )
                        )
                );
    }

    @Override
    protected Long keyOf(ShopDefinition shop) {
        return shop.id();
    }
}