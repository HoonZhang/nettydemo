package com.hoonzhang.nettt.test;

import com.hoonzhang.netty.server.codec.packet.MsgPacket;
import com.hoonzhang.netty.server.tasklet.Tasklet;
import io.netty.channel.ChannelHandlerContext;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestTasklet extends Tasklet {
    private static final Logger log = LoggerFactory.getLogger(TestTasklet.class);

    private MsgPacket reqMsg;

    private ChannelHandlerContext reqCtx;

    public TestTasklet(MsgPacket reqMsg, ChannelHandlerContext reqCtx) {
        this.reqMsg = reqMsg;
        this.reqCtx = reqCtx;
    }

    @Override
    public int doNext(MsgPacket msgPacket) {
        long t1 = System.currentTimeMillis();
        try {
            MsgPacket resp = (MsgPacket) msgPacket.clone();
            long t2 = System.currentTimeMillis();
            log.info("clone cost:{}", t2 - t1);
            reqCtx.writeAndFlush(resp);
//            reqCtx.write(resp);
            long t3 = System.currentTimeMillis();
            log.info("writeAndFlush cost:{}", t3 - t2);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        //修改命令字，直接回包
//        resp.getHead().setCmd(2);
        return 1;
    }
}
