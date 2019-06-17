package com.hoonzhang.netty.server.test;

import com.hoonzhang.netty.server.codec.header.Head;
import com.hoonzhang.netty.server.codec.packet.MsgPacket;
import com.hoonzhang.netty.server.connection.TcpConnectionPool;
import com.hoonzhang.netty.server.tasklet.Tasklet;
import com.hoonzhang.netty.server.tasklet.TaskletUtils;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

public class TestTasklet2 extends Tasklet {
    private static final Logger log = LoggerFactory.getLogger(TestTasklet2.class);

    private MsgPacket reqMsg;

    private ChannelHandlerContext reqCtx;

    interface TestStep {
        int STEP_KV_REQ = 1001;
        int STEP_KV_RSP = 1002;
    }

    private static InetSocketAddress kvRemoteAddress = new InetSocketAddress("127.0.0.1", 10087);

    public TestTasklet2(MsgPacket reqMsg, ChannelHandlerContext reqCtx) {
        super(TestStep.STEP_KV_RSP);
        this.reqMsg = reqMsg;
        this.reqCtx = reqCtx;
    }

    @Override
    public int doNextStep(MsgPacket msg) {
        int ret = 1;
        switch (getStep()) {
            case TestStep.STEP_KV_REQ:
                ret = on_kv_req(msg);
                setStep(TestStep.STEP_KV_RSP);
                break;

            case TestStep.STEP_KV_RSP:
                on_kv_rsp(msg);
        }
        log.info("ret:{}, step:{}, head:{}", ret, getStep(), msg.getHead());
        return ret;
    }

    public int on_kv_req(MsgPacket msg) {
        log.info("send start..., seq:{}", msg.getHead().getSeq());
        Head head = new Head();
        head.setCmd(101);
        head.setSeq(TaskletUtils.getNextSeq());

        MsgPacket req = new MsgPacket();
        req.setHead(head);

        TcpConnectionPool.getConnection(kvRemoteAddress).sendMsg(req);
        log.info("send end..., seq:{}", head.getSeq());

        return 0;
    }

    public int on_kv_rsp(MsgPacket msg) {
        long t1 = System.currentTimeMillis();
        try {
            final MsgPacket resp = (MsgPacket) msg.clone();
            resp.getHead().setSeq(reqMsg.getHead().getSeq());
            final long t2 = System.currentTimeMillis();
            log.info("clone cost:{}, isWriteble:{}, inEventLoop:{}, channel:{}, seq:{}, byte1:{}, byte2:{}",
                    t2 - t1, reqCtx.channel().isWritable(), reqCtx.executor().inEventLoop(), reqCtx.channel(), msg.getHead().getSeq(),
                    reqCtx.channel().bytesBeforeWritable(), reqCtx.channel().bytesBeforeUnwritable());
            reqCtx.writeAndFlush(resp);

            long t3 = System.currentTimeMillis();
            log.info("writeAndFlush cost:{}, seq:{}", t3 - t2, resp.getHead().getSeq());
        } catch (Exception e) {
            e.printStackTrace();
        }
//        //修改命令字，直接回包
//        resp.getHead().setCmd(2);
        return 1;
    }
}
