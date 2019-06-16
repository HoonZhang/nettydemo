package com.hoonzhang.netty.server.handler;

import com.hoonzhang.netty.server.codec.packet.MsgPacket;
import com.hoonzhang.netty.server.tasklet.WorkThreadPoolExcutorUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ServerResponseHandler extends SimpleChannelInboundHandler<MsgPacket> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MsgPacket msg) throws Exception {
        WorkThreadPoolExcutorUtils.addTask(msg);
    }
}