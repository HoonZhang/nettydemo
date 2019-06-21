package com.hoonzhang.netty.server.handler;

import com.hoonzhang.netty.server.codec.packet.MsgPacket;
import com.hoonzhang.netty.server.tasklet.WorkThreadPoolService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerResponseHandler extends SimpleChannelInboundHandler<MsgPacket> {
    private static final Logger log = LoggerFactory.getLogger(ServerResponseHandler.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("channel active...");
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("channel inactive...");
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error(cause.getMessage(), cause);
        super.exceptionCaught(ctx, cause);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MsgPacket msg) throws Exception {
        WorkThreadPoolService.addTask(msg);
    }
}