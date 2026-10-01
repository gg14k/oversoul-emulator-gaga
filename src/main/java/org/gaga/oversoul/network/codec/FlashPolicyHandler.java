package org.gaga.oversoul.network.codec;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.gaga.oversoul.utils.GameLogger;

// Intercepts XML, allowing JSON to pass through to the next handler
public class FlashPolicyHandler extends SimpleChannelInboundHandler<String> {

    private static final String POLICY_RESPONSE = "<cross-domain-policy><allow-access-from domain='*' to-ports='*'/></cross-domain-policy>";

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String packet) {
        if (packet.startsWith("<policy")) {
            GameLogger.debug("Received Flash policy request from {}", ctx.channel().remoteAddress());
            ctx.writeAndFlush(POLICY_RESPONSE);
            // Flash usually disconnects after receiving the policy, or opens a new socket
        } else {
            // Not a policy request, pass the packet to the next handler (GamePacketHandler)
            ctx.fireChannelRead(packet);
        }
    }
}