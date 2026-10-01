package org.gaga.oversoul.network.codec;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import java.util.List;

// Automatically appends \0 to everything sent from the server
public class NullByteEncoder extends MessageToMessageEncoder<String> {
    @Override
    protected void encode(ChannelHandlerContext ctx, String msg, List<Object> out) {
        out.add(msg + "\0");
    }
}