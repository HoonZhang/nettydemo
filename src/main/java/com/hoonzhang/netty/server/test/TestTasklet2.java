package com.hoonzhang.netty.server.test;

import com.hoonzhang.netty.server.codec.header.Head;
import com.hoonzhang.netty.server.codec.packet.MsgPacket;
import com.hoonzhang.netty.server.connection.TcpConnectionPool;
import com.hoonzhang.netty.server.tasklet.Tasklet;
import com.hoonzhang.netty.server.tasklet.TaskletUtils;
import com.hoonzhang.netty.server.utils.OkhttpUtils;
import com.xxmm.zhibo.oss.stat.StatWatcher;
import io.netty.channel.ChannelHandlerContext;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;

public class TestTasklet2 extends Tasklet {
    private static final Logger log = LoggerFactory.getLogger(TestTasklet2.class);

    private MsgPacket reqMsg;

    private ChannelHandlerContext reqCtx;

    StatWatcher watcher;

    interface TestStep {
        int STEP_HTTP_REQ = 1001;
        int STEP_HTTP_RSP = 1002;
    }

    public TestTasklet2(MsgPacket reqMsg, ChannelHandlerContext reqCtx) {
        super(TestStep.STEP_HTTP_REQ);
        this.reqMsg = reqMsg;
        this.reqCtx = reqCtx;
        createTimestamp = System.currentTimeMillis();
        this.timestemp = createTimestamp;
        this.watcher = new StatWatcher();
    }

    @Override
    public int doNextStep(MsgPacket msg) {
        int ret = 1;
        switch (getStep()) {
            case TestStep.STEP_HTTP_REQ:
                ret = on_http_req(msg);
                setStep(TestStep.STEP_HTTP_RSP);
                break;

            case TestStep.STEP_HTTP_RSP:
                on_kv_rsp(msg);
        }
        log.info("ret:{}, step:{}, head:{}", ret, getStep(), msg.getHead());
        return ret;
    }

    public int on_http_req(MsgPacket msg) {
        this.watcher.begin("test.tasklet2");

        log.info("send start..., seq:{}", msg.getHead().getSeq());

        final int seq = TaskletUtils.getNextSeq();
        this.setSeq(seq);

        String url = "http://www.baidu.com";
        Request request = new Request.Builder().url(url).build();

        final StatWatcher stat = new StatWatcher();
        stat.begin("http");

        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                stat.end(123123);
                log.error("seq:{}, isCanceled:{}, isExecuted:{}, timeout:{}, message:{}",
                        seq, call.isCanceled(), call.isExecuted(), call.timeout(), e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                stat.end(0);
                log.info("http response..., code:{}", response.code());
                Tasklet tasklet = TaskletUtils.getAndRemove(seq);
                if (tasklet != null) {
                    Head head = new Head();
                    head.setCmd(111111);
                    MsgPacket msg = new MsgPacket();
                    msg.setHead(head);
                    tasklet.doNextStep(msg);
                }
                response.close();
            }
        };

        TaskletUtils.put(this);

        OkhttpUtils.sendReq(request, callback);

        log.info("send http end..., seq:{}", seq);

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
            log.info("writeAndFlush cost:{}, client seq:{}", t3 - t2, resp.getHead().getSeq());
        } catch (Exception e) {
            e.printStackTrace();
        }
//        //修改命令字，直接回包
//        resp.getHead().setCmd(2);
        watcher.end(0);
        return 1;
    }
}
