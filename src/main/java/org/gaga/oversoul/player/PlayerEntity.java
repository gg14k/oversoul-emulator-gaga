package org.gaga.oversoul.player;

import org.gaga.oversoul.player.character.PlayerCharacter;
import org.gaga.oversoul.player.context.PlayerContext;
import org.gaga.oversoul.player.data.AccountData;
import org.gaga.oversoul.player.data.PlayerProfileData;
import org.gaga.oversoul.player.session.PlayerSession;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class PlayerEntity {

    private final PlayerSession session;
    private final PlayerContext context;

    private AccountData accountData;
    private PlayerProfileData profileData;

    private final Map<Long, PlayerCharacter> characters = new LinkedHashMap<>();

    private volatile Long activePlayerCharacterId;

    public PlayerEntity(PlayerSession session) {
        this.session = session;
        this.context = new PlayerContext();
    }

    /**
     * Initializes the persistent player state after authentication.
     */
    public void initialize(
            AccountData accountData,
            PlayerProfileData profileData,
            Collection<PlayerCharacter> ownedCharacters
    ) {
        this.accountData = accountData;
        this.profileData = profileData;
        this.activePlayerCharacterId =
                accountData.activePlayerCharacterId();

        characters.clear();

        for (PlayerCharacter character : ownedCharacters) {
            characters.put(
                    character.playerCharacterId(),
                    character
            );
        }

        context.setState(
                PlayerContext.PlayerState.IDLE
        );
    }

    public Long getActivePlayerCharacterId() {
        return activePlayerCharacterId;
    }

    public void setActivePlayerCharacterId(
            Long activePlayerCharacterId
    ) {
        this.activePlayerCharacterId =
                activePlayerCharacterId;
    }

    public PlayerCharacter getActiveCharacter() {
        if (activePlayerCharacterId == null) {
            return null;
        }

        return characters.get(activePlayerCharacterId);
    }

    public PlayerCharacter getOwnedCharacter(
            long playerCharacterId
    ) {
        return characters.get(playerCharacterId);
    }

    public boolean isLoaded() {
        return accountData != null
                && profileData != null;
    }

    public long getPlayerId() {
        return accountData != null
                ? accountData.playerId()
                : -1;
    }

    public String getUsername() {
        return accountData != null
                ? accountData.username()
                : "Unknown";
    }

    public PlayerCharacter getPlayerCharacter(
            long playerCharacterId
    ) {
        return characters.get(
                playerCharacterId
        );
    }

    public Collection<PlayerCharacter> getCharacters() {
        return Collections.unmodifiableCollection(
                characters.values()
        );
    }

    public PlayerSession getSession() {
        return session;
    }

    public PlayerContext getContext() {
        return context;
    }

    public AccountData getAccountData() {
        return accountData;
    }

    public PlayerProfileData getProfileData() {
        return profileData;
    }

    public void send(Object packet) {
        session.sendPacket(packet);
    }

    public void send(String packet) {
        session.sendPacket(packet);
    }
}