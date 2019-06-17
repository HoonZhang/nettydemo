package com.hoonzhang.netty.server.tasklet;

import com.hoonzhang.netty.server.codec.packet.MsgPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class TaskletUtils {
    private static final Logger log = LoggerFactory.getLogger(TaskletUtils.class);

    private static ConcurrentHashMap<Integer, Tasklet> tasklets = new ConcurrentHashMap<>();

    private static AtomicInteger seqGenerator = new AtomicInteger();

    static {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    log.info("size:{}, tasklets:{}", tasklets.size(), tasklets);
                    try {
                        Thread.sleep(5000);
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                }
            }
        }, "tasklet-clear").start();
    }

    public static void put(Tasklet tasklet) {
        tasklets.put(tasklet.getSeq(), tasklet);
    }

    public static Tasklet get(int seq) {
        return tasklets.remove(seq);
    }

    public static void execute(Tasklet tasklet, MsgPacket msg) {
        long t1 = System.currentTimeMillis();
        int ret = tasklet.doNextStep(msg);
        if (ret == 0) {
            TaskletUtils.put(tasklet);
        }
        log.info("ret:{}, cost:{}, seq:{}", ret, System.currentTimeMillis() - t1, msg.getHead().getSeq());
    }

    public static int getNextSeq() {
        return seqGenerator.incrementAndGet();
    }

}
