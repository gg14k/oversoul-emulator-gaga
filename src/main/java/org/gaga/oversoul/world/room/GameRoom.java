package org.gaga.oversoul.world.room;

import org.gaga.oversoul.game.map.MapDefinition;
import org.gaga.oversoul.player.PlayerEntity;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class GameRoom {

    private final long id;
    private final int instance;
    private final MapDefinition map;
    private final int capacity;

    private final ConcurrentMap<Long, PlayerEntity> players =
            new ConcurrentHashMap<>();

    public GameRoom(
            long id,
            int instance,
            MapDefinition map,
            int capacity
    ) {
        this.id = id;
        this.instance = instance;
        this.map = map;
        this.capacity = capacity;
    }

    public synchronized boolean add(
            PlayerEntity player
    ) {
        if (players.containsKey(
                player.getPlayerId()
        )) {
            return false;
        }

        if (players.size() >= capacity) {
            return false;
        }

        players.put(
                player.getPlayerId(),
                player
        );

        return true;
    }

    public synchronized boolean remove(
            PlayerEntity player
    ) {
        return players.remove(
                player.getPlayerId()
        ) != null;
    }

    public boolean contains(
            PlayerEntity player
    ) {
        return players.containsKey(
                player.getPlayerId()
        );
    }

    public void broadcast(
            Object packet
    ) {
        for (PlayerEntity player : players.values()) {
            player.send(packet);
        }
    }

    public void broadcastExcept(
            PlayerEntity excluded,
            Object packet
    ) {
        for (PlayerEntity player : players.values()) {

            if (player == excluded) {
                continue;
            }

            player.send(packet);
        }
    }

    public List<PlayerEntity> players() {
        return List.copyOf(
                players.values()
        );
    }

    public int size() {
        return players.size();
    }

    public boolean isEmpty() {
        return players.isEmpty();
    }

    public boolean hasCapacity() {
        return players.size() < capacity;
    }

    public long id() {
        return id;
    }

    public int instance() {
        return instance;
    }

    public MapDefinition map() {
        return map;
    }

    public int capacity() {
        return capacity;
    }
}