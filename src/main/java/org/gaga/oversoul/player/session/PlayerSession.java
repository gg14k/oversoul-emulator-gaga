package org.gaga.oversoul.player.session;

import io.netty.channel.Channel;
import org.gaga.oversoul.packet.PacketLogger;
import org.gaga.oversoul.utils.GameLogger;
import org.gaga.oversoul.utils.JsonUtil;

import java.time.Instant;

public class PlayerSession {
    private final Channel channel;
    private final Instant connectedAt;

    public PlayerSession(Channel channel) {
        this.channel = channel;
        this.connectedAt = Instant.now();
    }

    public Channel getChannel() { return channel; }
    public Instant getConnectedAt() { return connectedAt; }
    public boolean isActive() { return channel != null && channel.isActive(); }

    public void sendPacket(String packet) {
        if (packet == null) {
            return;
        }

        if (!isActive()) {
            return;
        }

        PacketLogger.outbound(
                channel,
                packet
        );
        channel.writeAndFlush(
                packet + "\0"
        );
        //channel.writeAndFlush(packet);
    }

    public void sendPacket(Object packet) {
        if (packet == null) {
            return;
        }

        sendPacket(
                JsonUtil.toJson(packet)
        );
    }

    public void disconnect(String reason) {
        if (isActive()) {
            GameLogger.info("Disconnecting channel. Reason: {}", reason);
            channel.close();
        }
    }
}