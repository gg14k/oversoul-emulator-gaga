package org.gaga.oversoul.network.session;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;

import org.gaga.oversoul.player.PlayerEntity;
import org.gaga.oversoul.player.session.PlayerSession;
import org.gaga.oversoul.service.GlobalService;
import org.gaga.oversoul.utils.GameLogger;

public class SessionManager {

    // Group to manage all active network channels for broadcasts
    private static final ChannelGroup ALL_CHANNELS = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    // The key used to attach our new PlayerEntity to the Netty Channel
    public static final AttributeKey<PlayerEntity> PLAYER_KEY = AttributeKey.valueOf("player_entity");

    /**
     * Called the moment a client connects to the TCP Server.
     */
    public static void addSession(Channel channel) {
        ALL_CHANNELS.add(channel);

        // 1. Initialize the network session wrapper
        PlayerSession session = new PlayerSession(channel);

        // 2. Initialize the unified Player Domain Entity
        PlayerEntity player = new PlayerEntity(session);

        // 3. Attach the player to the Netty Channel
        channel.attr(PLAYER_KEY).set(player);

        GameLogger.debug("Session established. Total online connections: {}", ALL_CHANNELS.size());
    }

    /**
     * Called when a client disconnects or the socket drops.
     */
    public static void removeSession(
            Channel channel
    ) {
        PlayerEntity player =
                getPlayer(
                        channel
                );

        if (player != null) {

            GameLogger.info(
                    "Cleaning up session for player: {}",
                    player.getUsername()
            );

            GlobalService
                    .roomService()
                    .leave(
                            player
                    );

            channel
                    .attr(PLAYER_KEY)
                    .set(null);
        }

        GameLogger.debug(
                "Session dropped. Total online connections: {}",
                ALL_CHANNELS.size()
        );
    }

    /**
     * Utility to safely retrieve the PlayerEntity from a raw Channel.
     */
    public static PlayerEntity getPlayer(Channel channel) {
        if (channel != null && channel.hasAttr(PLAYER_KEY)) {
            return channel.attr(PLAYER_KEY).get();
        }
        return null;
    }

    /**
     * Sends a raw JSON packet to every single connected client instantly.
     */
    public static void globalAnnounce(String jsonPacket) {
        GameLogger.info("[OUT] BROADCAST: {}", jsonPacket);
        ALL_CHANNELS.writeAndFlush(jsonPacket);
    }

    public static int getOnlineCount() {
        return ALL_CHANNELS.size();
    }
}