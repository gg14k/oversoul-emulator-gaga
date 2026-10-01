package org.gaga.oversoul.service;

import org.gaga.oversoul.config.ConfigRegistry;
import org.gaga.oversoul.database.DatabaseConnectionHandler;
import org.gaga.oversoul.database.DatabaseKey;
import org.gaga.oversoul.database.repository.PlayerRepository;
import org.gaga.oversoul.game.cache.GameCaches;
import org.gaga.oversoul.packet.registry.PacketRegistry;
import org.gaga.oversoul.repository.PlayerCharactersRepository;
import org.gaga.oversoul.repository.PlayerProfileRepository;
import org.gaga.oversoul.repository.PlayersRepository;
import org.gaga.oversoul.service.character.CharacterEquipService;
import org.gaga.oversoul.service.login.LoginService;
import org.gaga.oversoul.service.room.RoomService;
import org.jooq.DSLContext;

public final class GlobalService {

    private static LoginService loginService;
    private static RoomService ROOM_SERVICE;
    private static GameCaches GAME_CACHES;

    private static CharacterEquipService CHARACTER_EQUIP_SERVICE;

    public GlobalService() {
        ConfigRegistry.loadAll();
        DatabaseConnectionHandler.init(ConfigRegistry.DATABASE);
        initializeGameCaches();
        initializeServices();
        PacketRegistry.initialize();

    }

    private void initializeGameCaches() {
        GAME_CACHES = new GameCaches();
        GAME_CACHES.loadAll();
    }

    private void initializeServices() {
        DSLContext database = DatabaseConnectionHandler.dsl(DatabaseKey.GAME);
        PlayersRepository playersRepository = new PlayersRepository(database);
        PlayerProfileRepository profileRepository = new PlayerProfileRepository(database);
        PlayerCharactersRepository charactersRepository = new PlayerCharactersRepository(database);
        loginService = new LoginService(playersRepository, profileRepository, charactersRepository);
        ROOM_SERVICE = new RoomService(GAME_CACHES);
        CHARACTER_EQUIP_SERVICE = new CharacterEquipService(new PlayerRepository(), ROOM_SERVICE, GAME_CACHES);

    }

    public static GameCaches gameCaches() { return GAME_CACHES; }
    public static LoginService loginService() { return loginService; }
    public static RoomService roomService() { return ROOM_SERVICE; }

    public static CharacterEquipService characterEquipService() { return CHARACTER_EQUIP_SERVICE; }
}