package org.gaga.oversoul.player.context;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class PlayerContext {

    private volatile long currentRoomId = -1;

    private volatile String location;

    private volatile boolean hidden;

    private volatile PlayerState state =
            PlayerState.LOADING;

    private final Map<String, Long> cooldowns =
            new ConcurrentHashMap<>();

    public enum PlayerState {
        LOADING,
        IDLE,
        IN_COMBAT,
        TRADING
    }

    public void changeRoom(
            long roomId
    ) {
        this.currentRoomId = roomId;
        this.location = null;
        this.hidden = false;
    }

    public void leaveRoom() {
        this.currentRoomId = -1;
        this.location = null;
        this.hidden = false;
    }

    public long getCurrentRoomId() {
        return currentRoomId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(
            String location
    ) {
        this.location = location;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(
            boolean hidden
    ) {
        this.hidden = hidden;
    }

    public PlayerState getState() {
        return state;
    }

    public void setState(
            PlayerState state
    ) {
        this.state = state;
    }

    public boolean tryConsumeCooldown(
            String action,
            long intervalMs
    ) {
        long now =
                System.currentTimeMillis();

        long lastUsed =
                cooldowns.getOrDefault(
                        action,
                        0L
                );

        if (now - lastUsed < intervalMs) {
            return false;
        }

        cooldowns.put(
                action,
                now
        );

        return true;
    }
}