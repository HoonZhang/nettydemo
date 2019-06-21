package com.hoonzhang.netty.server.connection;

import com.hoonzhang.netty.server.codec.handler.MsgDecoder;
import com.hoonzhang.netty.server.codec.handler.MsgEncoder;
import com.hoonzhang.netty.server.codec.packet.MsgPacket;
import com.hoonzhang.netty.server.handler.ServerResponseHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
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

    Bootstrap bootstrap;
    private Channel channel;
    private InetSocketAddress remoteSocketAddress;

    public TcpConnection(String remoteIp, int remotePort) {
        this.remoteSocketAddress = new InetSocketAddress(remoteIp, remotePort);
    }

    public TcpConnection(InetSocketAddress remoteSocketAddress) {
        this.remoteSocketAddress = remoteSocketAddress;
    }

    public void connect() {
        this.bootstrap = new Bootstrap();
        this.bootstrap.group(new NioEventLoopGroup());
        this.bootstrap.channel(NioSocketChannel.class);
        this.bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 3, 4, -7, 0));
                pipeline.addLast(new MsgDecoder());
                pipeline.addLast(new MsgEncoder());
                pipeline.addLast(new ServerResponseHandler());
            }
        });

        ChannelFuture channelFuture = this.bootstrap.connect(remoteSocketAddress).addListener(
                new GenericFutureListener<Future<? super Void>>() {
                    @Override
                    public void operationComplete(Future<? super Void> future) throws Exception {
                        log.info("connect complete...");
                    }
                });

        channelFuture.awaitUninterruptibly();

        this.channel = channelFuture.channel();

        log.info("isDone={}, isSuccess={}", channelFuture.isDone(), channelFuture.isSuccess());
    }

    public void reconnect() {
        //关闭之前的连接
        this.channel.close();

        ChannelFuture channelFuture = this.bootstrap.connect().addListener(
                new GenericFutureListener<Future<? super Void>>() {
                    @Override
                    public void operationComplete(Future<? super Void> future) throws Exception {
                        log.info("reconnect complete...");
                    }
                });

        channelFuture.awaitUninterruptibly();

        this.channel = channelFuture.channel();

        log.info("isDone={}, isSuccess={}", channelFuture.isDone(), channelFuture.isSuccess());
    }

    public boolean isConnected() {
        return this.channel != null && this.channel.isActive();
    }

    public int sendMsg(MsgPacket msg) {
        log.info("isActive:{}, isOpen:{}, head:{}", channel.isActive(), channel.isOpen(), msg.getHead());
        channel.writeAndFlush(msg).addListener(new GenericFutureListener<Future<? super Void>>() {
            @Override
            public void operationComplete(Future<? super Void> future) throws Exception {
                if (!future.isSuccess()) {
                    log.error("operation failed..., isSuccess:{}", future.isSuccess());
                }
            }
        });
        return 0;
    }
}
