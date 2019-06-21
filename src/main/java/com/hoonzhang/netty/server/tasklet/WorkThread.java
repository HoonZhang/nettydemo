package com.hoonzhang.netty.server.tasklet;

import com.hoonzhang.netty.server.codec.packet.MsgPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkThread implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(WorkThread.class);
    private Tasklet tasklet;
    private MsgPacket msg;

    public WorkThread(Tasklet tasklet, MsgPacket msg) {
        this.tasklet = tasklet;
        this.msg = msg;
    }

    @Override
    public void run() {
        long t1 = System.currentTimeMillis();
        int ret = tasklet.doNextStep(msg);
        long t2 = System.currentTimeMillis();
        log.error("ret:{}, msgSeq:{}, taskletSeq:{}, taskletSize:{}, doDiff:{}, addDiff:{}, putDiff:{}, createDiff:{}",
                ret, msg.getHead().getSeq(), tasklet.getSeq(), TaskletUtils.getSize(), t2 - t1,
                t2 - tasklet.addTimestamp, t2 - tasklet.timestemp, t2 - tasklet.createTimestamp);
    }

}
