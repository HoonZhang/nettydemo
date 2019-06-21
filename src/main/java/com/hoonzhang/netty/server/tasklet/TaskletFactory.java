package com.hoonzhang.netty.server.tasklet;

import com.hoonzhang.netty.server.codec.packet.MsgPacket;
import io.netty.channel.ChannelHandlerContext;

public class TaskletFactory implements ITaskletFactory {
    private static ITaskletFactory factory;

    public static void init(ITaskletFactory taskletFactory) {
        TaskletFactory.factory = taskletFactory;
    }

    public static ITaskletFactory getInstance() {
        return TaskletFactory.factory;
    }

    @Override
    public Tasklet create(MsgPacket msg, ChannelHandlerContext ctx) {
        return TaskletFactory.factory.create(msg, ctx);
    }
}
