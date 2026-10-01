package org.gaga.oversoul.service.room;

import org.gaga.oversoul.game.cache.GameCaches;
import org.gaga.oversoul.game.map.MapDefinition;
import org.gaga.oversoul.packet.mapper.RoomPacketMapper;
import org.gaga.oversoul.player.PlayerEntity;
import org.gaga.oversoul.utils.GameLogger;
import org.gaga.oversoul.world.room.GameRoom;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public final class RoomService {

    private static final int DEFAULT_CAPACITY = 10;

    private final GameCaches gameCaches;

    private final AtomicLong roomIdSequence =
            new AtomicLong(1);

    private final Map<Long, GameRoom> roomsById =
            new ConcurrentHashMap<>();

    private final Map<String, List<GameRoom>> roomsByMap =
            new ConcurrentHashMap<>();

    private final Object roomCreationLock =
            new Object();

    public RoomService(
            GameCaches gameCaches
    ) {
        this.gameCaches = gameCaches;
    }

    public boolean join(
            PlayerEntity player,
            String mapName
    ) {
        MapDefinition map =
                gameCaches
                        .maps
                        .getByName(mapName);

        if (map == null) {
            GameLogger.warning(
                    "Map '{}' was not found.",
                    mapName
            );

            return false;
        }

        leave(player);

        JoinResult result =
                selectAndAddRoom(
                        player,
                        map
                );

        if (result == null) {
            return false;
        }

        GameRoom room =
                result.room();

        player
                .getContext()
                .changeRoom(
                        room.id()
                );

        /*
         * Notify players already inside the room.
         */
        Object addUserPacket =
                RoomPacketMapper.addUser(
                        room,
                        player
                );

        for (PlayerEntity existing :
                result.existingPlayers()) {

            existing.send(
                    addUserPacket
            );
        }

        /*
         * The joining player receives everyone who was
         * already inside the room.
         */
        player.send(
                RoomPacketMapper.join(
                        room,
                        result.existingPlayers()
                )
        );

        GameLogger.info(
                "Player '{}' joined map '{}' room {} instance {}. Players: {}",
                player.getUsername(),
                map.name(),
                room.id(),
                room.instance(),
                room.size()
        );

        return true;
    }

    public boolean sendRoomList(
            PlayerEntity player,
            long requestedRoomId
    ) {
        long currentRoomId =
                player
                        .getContext()
                        .getCurrentRoomId();

        if (currentRoomId != requestedRoomId) {
            GameLogger.warning(
                    "Player '{}' requested room list for {}, but is currently in {}.",
                    player.getUsername(),
                    requestedRoomId,
                    currentRoomId
            );

            return false;
        }

        GameRoom room =
                roomsById.get(
                        currentRoomId
                );

        if (room == null) {
            return false;
        }

        player.send(
                RoomPacketMapper.list(
                        room
                )
        );

        return true;
    }

    public void leave(
            PlayerEntity player
    ) {
        if (player == null) {
            return;
        }

        long roomId =
                player
                        .getContext()
                        .getCurrentRoomId();

        if (roomId < 0) {
            return;
        }

        GameRoom room =
                roomsById.get(
                        roomId
                );

        String playerName =
                player.getUsername();

        player
                .getContext()
                .leaveRoom();

        if (room == null) {
            return;
        }

        boolean removed =
                room.remove(player);

        if (!removed) {
            return;
        }

        /*
         * Inform everyone remaining in the room.
         */
        room.broadcast(
                RoomPacketMapper.dropUser(
                        room,
                        playerName
                )
        );

        GameLogger.info(
                "Player '{}' left room {}. Players remaining: {}",
                playerName,
                room.id(),
                room.size()
        );

        if (room.isEmpty()) {
            removeRoom(room);
        }
    }

    public GameRoom getRoom(
            long roomId
    ) {
        return roomsById.get(
                roomId
        );
    }

    private JoinResult selectAndAddRoom(
            PlayerEntity player,
            MapDefinition map
    ) {
        synchronized (roomCreationLock) {

            String key =
                    normalize(
                            map.name()
                    );

            List<GameRoom> rooms =
                    roomsByMap.computeIfAbsent(
                            key,
                            ignored ->
                                    new ArrayList<>()
                    );

            for (GameRoom room : rooms) {

                if (!room.hasCapacity()) {
                    continue;
                }

                List<PlayerEntity> existingPlayers =
                        room.players();

                if (!room.add(player)) {
                    continue;
                }

                return new JoinResult(
                        room,
                        existingPlayers
                );
            }

            int instance =
                    nextInstance(
                            rooms
                    );

            GameRoom room =
                    new GameRoom(
                            roomIdSequence
                                    .getAndIncrement(),
                            instance,
                            map,
                            DEFAULT_CAPACITY
                    );

            if (!room.add(player)) {
                return null;
            }

            rooms.add(room);

            roomsById.put(
                    room.id(),
                    room
            );

            return new JoinResult(
                    room,
                    List.of()
            );
        }
    }

    private int nextInstance(
            List<GameRoom> rooms
    ) {
        int instance = 1;

        while (true) {

            final int candidate =
                    instance;

            boolean exists =
                    rooms
                            .stream()
                            .anyMatch(room ->
                                    room.instance()
                                            == candidate
                            );

            if (!exists) {
                return candidate;
            }

            instance++;
        }
    }

    private void removeRoom(
            GameRoom room
    ) {
        synchronized (roomCreationLock) {

            roomsById.remove(
                    room.id()
            );

            String key =
                    normalize(
                            room.map().name()
                    );

            List<GameRoom> rooms =
                    roomsByMap.get(
                            key
                    );

            if (rooms == null) {
                return;
            }

            rooms.remove(room);

            if (rooms.isEmpty()) {
                roomsByMap.remove(
                        key
                );
            }

            GameLogger.debug(
                    "Destroyed empty room {} instance {} for map '{}'.",
                    room.id(),
                    room.instance(),
                    room.map().name()
            );
        }
    }

    private String normalize(
            String value
    ) {
        return value
                .trim()
                .toLowerCase(
                        Locale.ROOT
                );
    }

    private record JoinResult(
            GameRoom room,
            List<PlayerEntity> existingPlayers
    ) {
    }
}