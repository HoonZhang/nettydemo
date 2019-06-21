package com.hoonzhang.netty.server.test;

import com.hoonzhang.netty.server.codec.header.Head;
import com.hoonzhang.netty.server.codec.packet.MsgPacket;
import com.hoonzhang.netty.server.connection.TcpConnection;
import com.hoonzhang.netty.server.connection.TcpConnectionPool;
import com.hoonzhang.netty.server.tasklet.Tasklet;
import com.hoonzhang.netty.server.tasklet.TaskletUtils;
import com.xxmm.zhibo.oss.stat.StatWatcher;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

public class TestTasklet extends Tasklet {
    private static final Logger log = LoggerFactory.getLogger(TestTasklet.class);

    private MsgPacket reqMsg;

    private ChannelHandlerContext reqCtx;

    private StatWatcher watcher;

    interface TestStep {
        int STEP_KV_REQ = 1001;
        int STEP_KV_RSP = 1002;
    }

    private static InetSocketAddress kvRemoteAddress = new InetSocketAddress("127.0.0.1", 10087);

    public TestTasklet(MsgPacket reqMsg, ChannelHandlerContext reqCtx) {
        super(TestStep.STEP_KV_REQ);
        this.reqMsg = reqMsg;
        this.reqCtx = reqCtx;
        watcher = new StatWatcher();
        createTimestamp = System.currentTimeMillis();
        this.timestemp = createTimestamp;
    }

    @Override
    public String toString() {
        return "TestTasklet{" +
                "reqMsg=" + reqMsg +
                "seq=" + getSeq() +
                '}';
    }

    @Override
    public void onExpire() {
        log.error("step:{}, reqMsg:{}", getStep(), reqMsg);
    }

    @Override
    public int doNextStep(MsgPacket msg) {
        int ret = 1;
        int step = getStep();
        switch (step) {
            case TestStep.STEP_KV_REQ:
                ret = on_kv_req(msg);
                setStep(TestStep.STEP_KV_RSP);
                break;

            case TestStep.STEP_KV_RSP:
                on_kv_rsp(msg);
        }
        log.info("ret:{}, step:{}, clientSeq:{}, msgHead:{}, curSeq:{}", ret, step, reqMsg.getHead().getSeq(), msg
                .getHead(), getSeq());
        return ret;
    }

    public int on_kv_req(MsgPacket msg) {
        watcher.begin("test.tasklet1");
        Head head = new Head();
        head.setCmd(201);
        setSeq(TaskletUtils.getNextSeq());
        head.setSeq(getSeq());

        MsgPacket req = new MsgPacket();
        req.setHead(head);

        TaskletUtils.put(this);

        TcpConnectionPool.getConnection(kvRemoteAddress).sendMsg(req);
        return 0;
    }

    public int on_kv_rsp(MsgPacket msg) {
        long t1 = System.currentTimeMillis();
        try {
            final MsgPacket resp = (MsgPacket) msg.clone();
            resp.getHead().setSeq(reqMsg.getHead().getSeq());
            reqCtx.writeAndFlush(resp);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        //修改命令字，直接回包
//        resp.getHead().setCmd(2);
        watcher.end(0);

        return 1;
    }
}
