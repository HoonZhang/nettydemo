package com.hoonzhang.netty.server.tasklet;

import com.hoonzhang.netty.server.codec.packet.MsgPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class TaskletUtils {
    private static final Logger log = LoggerFactory.getLogger(TaskletUtils.class);

//    private static Object lock = new Object();

    //    private static Map<Integer, Tasklet> tasklets = new HashMap<>();
    private static ConcurrentHashMap<Integer, Tasklet> tasklets = new ConcurrentHashMap<>();

    private static AtomicInteger seqGenerator = new AtomicInteger();

    static {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    long curTime = System.currentTimeMillis();
                    if (tasklets.size() > 0) {
                        int i = 0;
                        Iterator<Map.Entry<Integer, Tasklet>> itr = tasklets.entrySet().iterator();
                        while (itr.hasNext()) {
                            Map.Entry<Integer, Tasklet> entry = itr.next();
                            if (curTime - entry.getValue().getTimestemp() > 3000) {
                                entry.getValue().onExpire();
                                log.error("remove i:{}, seq:{}, tasklet:{}", ++i, entry.getKey(), entry.getValue());
                                itr.remove();
                            }
                        }
                        if (tasklets.size() > 20) {
                            log.error("taskletSize:{}", tasklets.size());
                        }
                    }
                    try {
                        Thread.sleep(20);
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                }
            }
        }, "tasklet-clear").start();
    }

    public static void put(Tasklet tasklet) {
        tasklet.setTimestemp(System.currentTimeMillis());
        tasklets.put(tasklet.getSeq(), tasklet);
        log.info("add tasklet, seq:{}", tasklet.getSeq());
    }

    public static Tasklet getAndRemove(int seq) {
        return tasklets.remove(seq);
    }

    public static int getSize() {
        return tasklets.size();
    }

    public static int getNextSeq() {
        return seqGenerator.incrementAndGet();
    }

}
