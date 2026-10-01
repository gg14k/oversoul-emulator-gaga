package org.gaga.oversoul.network;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import java.nio.charset.StandardCharsets;

import org.gaga.oversoul.network.codec.FlashPolicyHandler;
import org.gaga.oversoul.network.codec.NullByteEncoder;
import org.gaga.oversoul.packet.GamePacketHandler;

public class GameChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();

        // 1. Automatically splits incoming bytes by '\0'
        pipeline.addLast(new DelimiterBasedFrameDecoder(8192, Unpooled.copiedBuffer(new byte[]{0})));

        // 2. Converts bytes to Java Strings automatically
        pipeline.addLast(new StringDecoder(StandardCharsets.UTF_8));

        // 3. Converts outgoing Java Strings back to bytes
        pipeline.addLast(new StringEncoder(StandardCharsets.UTF_8));

        // 4. Appends '\0' to every outgoing packet automatically
        pipeline.addLast(new NullByteEncoder());

        // 5. Intercepts Flash '<policy-file-request/>'
        pipeline.addLast(new FlashPolicyHandler());

        // 6. Handles actual game JSON packets
        pipeline.addLast(new GamePacketHandler());
    }
}