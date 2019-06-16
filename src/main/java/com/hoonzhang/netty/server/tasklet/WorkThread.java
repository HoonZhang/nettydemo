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
        int ret = tasklet.doNext(msg);
        if (ret == 0) {
            TaskletUtils.put(tasklet);
        }
        log.info("ret:{},  cost:{}", ret, System.currentTimeMillis() - t1);
    }
}
