package org.gaga.oversoul.network;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.MultiThreadIoEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.nio.NioIoHandler;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.gaga.oversoul.utils.GameLogger;

public class GameServer {
    private final int port;

    public GameServer(int port) {
        this.port = port;
    }

    public void start() {

        EventLoopGroup bossGroup = new MultiThreadIoEventLoopGroup(1, NioIoHandler.newFactory());
        EventLoopGroup workerGroup = new MultiThreadIoEventLoopGroup(NioIoHandler.newFactory());

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new GameChannelInitializer());

            ChannelFuture future = bootstrap.bind(port).sync();
            GameLogger.info("TCP Server successfully started on port {}", port);

            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            GameLogger.error("Server interrupted", e);
            Thread.currentThread().interrupt();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}