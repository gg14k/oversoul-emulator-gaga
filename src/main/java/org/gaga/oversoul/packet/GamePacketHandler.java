package org.gaga.oversoul.packet;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.gaga.oversoul.network.session.SessionManager;
import org.gaga.oversoul.packet.registry.PacketRegistry;
import org.gaga.oversoul.packet.request.InvalidPacketException;
import org.gaga.oversoul.packet.request.PacketData;
import org.gaga.oversoul.player.PlayerEntity;
import org.gaga.oversoul.service.GlobalService;
import org.gaga.oversoul.utils.GameLogger;

/**
 * Handler responsável por receber os packets do client
 * e encaminhá-los para o módulo correspondente.
 */
public final class GamePacketHandler
        extends SimpleChannelInboundHandler<String> {

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        GameLogger.info(
                "Client connected: {}",
                ctx.channel().remoteAddress()
        );

        SessionManager.addSession(
                ctx.channel()
        );
    }

    @Override
    public void handlerRemoved(
            ChannelHandlerContext ctx
    ) {
        PlayerEntity player =
                SessionManager.getPlayer(
                        ctx.channel()
                );

        if (player != null) {
            GlobalService
                    .roomService()
                    .leave(player);
        }

        SessionManager.removeSession(
                ctx.channel()
        );
    }

    @Override
    protected void channelRead0(
            ChannelHandlerContext ctx,
            String rawPacket
    ) {

        try {
            PacketLogger.inbound(
                    ctx.channel(),
                    rawPacket
            );
            JsonObject json = JsonParser
                    .parseString(rawPacket)
                    .getAsJsonObject();

            PacketData packet =
                    PacketData.of(json);

            String type =
                    packet.getString("type");

            if (type.isBlank()) {
                throw new InvalidPacketException(
                        "Packet type cannot be empty."
                );
            }

            var wrapper =
                    PacketRegistry.getWrapper(type);

            if (wrapper == null) {
                GameLogger.warning(
                        "Unhandled packet type received: {}",
                        type
                );

                return;
            }

            PlayerEntity player =
                    SessionManager.getPlayer(
                            ctx.channel()
                    );

            if (player == null) {
                GameLogger.warning(
                        "Packet received without PlayerEntity: {}",
                        type
                );

                return;
            }

            if (wrapper.config().debug) {
                GameLogger.debug(
                        "[DEBUG-PACKET] Routing '{}' to {}",
                        type,
                        wrapper.config().class_name
                );
            }

            wrapper
                    .moduleInstance()
                    .handle(
                            player,
                            packet
                    );

        } catch (InvalidPacketException e) {
            GameLogger.warning(
                    "Invalid packet received from {}: {}",
                    ctx.channel().remoteAddress(),
                    e.getMessage()
            );

        } catch (JsonSyntaxException
                 | IllegalStateException e) {

            GameLogger.warning(
                    "Malformed JSON packet received from {}: {}",
                    ctx.channel().remoteAddress(),
                    rawPacket
            );

        } catch (Exception e) {
            GameLogger.error(
                    "Critical error while routing packet",
                    e
            );
        }
    }

    @Override
    public void exceptionCaught(
            ChannelHandlerContext ctx,
            Throwable cause
    ) {
        GameLogger.error(
                "Network exception",
                cause
        );

        ctx.close();
    }
}