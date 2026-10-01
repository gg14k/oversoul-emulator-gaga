package org.gaga.oversoul.packet;

import io.netty.channel.Channel;
import org.gaga.oversoul.utils.GameLogger;

import java.util.regex.Pattern;

public final class PacketLogger {

    private static final boolean ENABLED = true;

    /*
     * Authentication tokens should not be written to logs.
     * The packet structure remains intact for protocol debugging.
     */
    private static final Pattern TOKEN_PATTERN =
            Pattern.compile(
                    "(\"Token\"\\s*:\\s*\")[^\"]*(\")",
                    Pattern.CASE_INSENSITIVE
            );

    private PacketLogger() {
    }

    public static void inbound(
            Channel channel,
            String packet
    ) {
        if (!ENABLED || packet == null) {
            return;
        }

        GameLogger.debug(
                "[PACKET-IN ] ["
                        + remoteAddress(channel)
                        + "] ["
                        + packet.length()
                        + " chars] "
                        + sanitize(packet)
        );
    }

    public static void outbound(
            Channel channel,
            String packet
    ) {
        if (!ENABLED || packet == null) {
            return;
        }

        GameLogger.debug(
                "[PACKET-OUT] ["
                        + remoteAddress(channel)
                        + "] ["
                        + packet.length()
                        + " chars] "
                        + sanitize(packet)
        );
    }

    private static String sanitize(
            String packet
    ) {
        return TOKEN_PATTERN
                .matcher(packet)
                .replaceAll("$1***$2")
                .replace("\0", "\\0");
    }

    private static String remoteAddress(
            Channel channel
    ) {
        if (channel == null
                || channel.remoteAddress() == null) {
            return "unknown";
        }

        return channel
                .remoteAddress()
                .toString();
    }
}