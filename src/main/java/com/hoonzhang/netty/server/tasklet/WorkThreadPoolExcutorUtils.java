package com.hoonzhang.netty.server.tasklet;

import com.hoonzhang.netty.server.codec.packet.MsgPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WorkThreadPoolExcutorUtils {
    private static final Logger log = LoggerFactory.getLogger(WorkThreadPoolExcutorUtils.class);
    private static ExecutorService executorService;

    public static void init(int threadSize) {
        executorService = Executors.newFixedThreadPool(threadSize);
    }

    public static void addTask(MsgPacket msg) {
        Tasklet tasklet = TaskletUtils.get(msg.getHead().getSeq());

        if (tasklet != null) {
            addTask(tasklet, msg);
        }
        log.info("tasklet={}, msg head:{}", tasklet, msg.getHead());
    }

    public static void addTask(Tasklet tasklet, MsgPacket msg) {
        executorService.execute(new WorkThread(tasklet, msg));
    }

}
