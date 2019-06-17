package com.hoonzhang.netty.server.connection;

import com.hoonzhang.netty.server.codec.handler.MsgDecoder;
import com.hoonzhang.netty.server.codec.handler.MsgEncoder;
import com.hoonzhang.netty.server.codec.packet.MsgPacket;
import com.hoonzhang.netty.server.handler.ServerResponseHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

public class TcpConnection {
    private static final Logger log = LoggerFactory.getLogger(TcpConnection.class);

    private ChannelFuture channelFuture;
    private InetSocketAddress remoteSocketAddress;

    public TcpConnection(String remoteIp, int remotePort) {
        this.remoteSocketAddress = new InetSocketAddress(remoteIp, remotePort);
    }

    public TcpConnection(InetSocketAddress remoteSocketAddress) {
        this.remoteSocketAddress = remoteSocketAddress;
    }

    public void connect() {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(new NioEventLoopGroup());
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 3, 4, -7, 0));
                pipeline.addLast(new MsgDecoder());
                pipeline.addLast(new MsgEncoder());
                pipeline.addLast(new ServerResponseHandler());
            }
        });

        channelFuture = bootstrap.connect(remoteSocketAddress).addListener(new GenericFutureListener<Future<? super Void>>() {
            @Override
            public void operationComplete(Future<? super Void> future) throws Exception {
                log.info("connect complete...");
            }
        });

        channelFuture.awaitUninterruptibly();

        log.info("isDone={}, isSuccess={}", channelFuture.isDone(), channelFuture.isSuccess());
    }

    public int sendMsg(MsgPacket msg) {
        log.info("isActive:{}, isOpen:{}", channelFuture.channel().isActive(), channelFuture.channel().isOpen());
        channelFuture.channel().writeAndFlush(msg);
        return 0;
    }
}
