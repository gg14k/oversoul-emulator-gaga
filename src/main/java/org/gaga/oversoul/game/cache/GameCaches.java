package org.gaga.oversoul.game.cache;

import org.gaga.oversoul.data.cache.CacheRegistry;
import org.gaga.oversoul.data.repository.game.CardsRepository;
import org.gaga.oversoul.data.repository.game.CharacterCardsRepository;
import org.gaga.oversoul.data.repository.game.CharactersRepository;
import org.gaga.oversoul.data.repository.game.MapsRepository;
import org.gaga.oversoul.data.repository.game.ShopCharactersRepository;
import org.gaga.oversoul.data.repository.game.ShopsRepository;

public final class GameCaches {

    private final CacheRegistry registry = new CacheRegistry();

    public final CharactersRepository characters;

    public final CardsRepository cards;

    public final CharacterCardsRepository characterCards;

    public final MapsRepository maps;

    public final ShopsRepository shops;

    public final ShopCharactersRepository shopCharacters;

    public GameCaches() {

        /*
         * Definições principais primeiro.
         */
        characters = registry.register(
                new CharactersRepository()
        );

        cards = registry.register(
                new CardsRepository()
        );

        /*
         * Relações dependentes.
         */
        characterCards = registry.register(
                new CharacterCardsRepository()
        );

        maps = registry.register(
                new MapsRepository()
        );

        shops = registry.register(
                new ShopsRepository()
        );

        shopCharacters = registry.register(
                new ShopCharactersRepository()
        );
    }

    public void loadAll() {
        registry.loadAll();
    }

    public boolean reloadAll() {
        return registry.reloadAllSafely();
    }

    public boolean reload(
            String name
    ) {
        return registry.reload(name);
    }

    public CacheRegistry registry() {
        return registry;
    }
}