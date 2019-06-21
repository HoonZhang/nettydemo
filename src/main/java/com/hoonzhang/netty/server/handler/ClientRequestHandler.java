package com.hoonzhang.netty.server.handler;

import com.hoonzhang.netty.server.codec.packet.MsgPacket;
import com.hoonzhang.netty.server.tasklet.Tasklet;
import com.hoonzhang.netty.server.tasklet.TaskletFactory;
import com.hoonzhang.netty.server.tasklet.WorkThreadPoolService;
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

        Tasklet tasklet = TaskletFactory.getInstance().create(msg, ctx);
        if (tasklet != null) {
            WorkThreadPoolService.addTask(tasklet, msg);
        }

/*        long t1 = System.currentTimeMillis();
        int ret = tasklet.doNext(msg);
        if (ret == 0) {
            TaskletUtils.put(tasklet);
        }
        log.info("ret:{}, cost:{}, seq:{}", ret, System.currentTimeMillis() - t1, msg.getHead().getSeq());*/
    }
}
