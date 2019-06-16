package com.hoonzhang.netty.server.tasklet;

import com.hoonzhang.netty.server.codec.packet.MsgPacket;
import io.netty.channel.ChannelHandlerContext;

public interface TaskletFactory {
    public Tasklet create(MsgPacket msg, ChannelHandlerContext ctx);
}
