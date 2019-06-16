package com.hoonzhang.netty.server.handler;

import com.hoonzhang.netty.server.codec.packet.MsgPacket;
import com.hoonzhang.netty.server.tasklet.Tasklet;
import com.hoonzhang.netty.server.tasklet.TaskletFactoryUtils;
import com.hoonzhang.netty.server.tasklet.WorkThreadPoolExcutorUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientRequestHandler extends SimpleChannelInboundHandler<MsgPacket> {
    private static final Logger log = LoggerFactory.getLogger(ClientRequestHandler.class);

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("", cause);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MsgPacket msg) throws Exception {
//        MsgPacket resp = (MsgPacket) msg.clone();
//        //修改命令字，直接回包
//        resp.getHead().setCmd(2);
//        ctx.writeAndFlush(resp);

        Tasklet tasklet = TaskletFactoryUtils.getTaskletFactory().create(msg, ctx);
        WorkThreadPoolExcutorUtils.addTask(tasklet, msg);
    }
}
