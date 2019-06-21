package com.hoonzhang.netty.server;

import com.hoonzhang.netty.server.codec.handler.MsgDecoder;
import com.hoonzhang.netty.server.codec.handler.MsgEncoder;
import com.hoonzhang.netty.server.handler.ClientRequestHandler;
import com.hoonzhang.netty.server.tasklet.WorkThreadPoolService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;

public class NettyServer {
    private static final Logger log = LoggerFactory.getLogger(NettyServer.class);

    private int port;
    private int acceptorThreadPoolSize;
    private int ioThreadPoolSize;
    private int workThreadPoolSize;

    public NettyServer(int port, int acceptorThreadPoolSize, int ioThreadPoolSize, int workThreadPoolSize) {
        this.port = port;
        this.acceptorThreadPoolSize = acceptorThreadPoolSize;
        this.ioThreadPoolSize = ioThreadPoolSize;
        this.workThreadPoolSize = workThreadPoolSize;
        log.info("port={}, acceptorThreadPoolSize={}, ioThreadPoolSize={}, workThreadPoolSize={}",
                port, acceptorThreadPoolSize, ioThreadPoolSize, workThreadPoolSize);

//        start();
    }

    public void start() {
        final CountDownLatch latch = new CountDownLatch(1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                startServer(latch);
            }
        }, "netty-server").start();

        try {
            latch.await();
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
    }

    public void startServer(CountDownLatch latch) {
        log.info("start");

        WorkThreadPoolService.init(workThreadPoolSize);

        EventLoopGroup acceptorGroup = new NioEventLoopGroup(acceptorThreadPoolSize);
        EventLoopGroup ioGroup = new NioEventLoopGroup(ioThreadPoolSize);
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(acceptorGroup, ioGroup);
        bootstrap.channel(NioServerSocketChannel.class);
//        bootstrap.option(ChannelOption.)
        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                ChannelPipeline pipeline = socketChannel.pipeline();
                pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 3, 4, -7, 0));
                pipeline.addLast("MsgDecoder", new MsgDecoder());
                pipeline.addLast("MsgEncoder", new MsgEncoder());
                pipeline.addLast("ClientRequestHandler", new ClientRequestHandler());
            }
        });
        try {
            ChannelFuture channelFuture = bootstrap.bind(port).sync();
            latch.countDown();
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            acceptorGroup.shutdownGracefully();
            ioGroup.shutdownGracefully();
        }
    }
}
