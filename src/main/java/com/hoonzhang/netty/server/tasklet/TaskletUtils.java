package com.hoonzhang.netty.server.tasklet;

import java.util.concurrent.ConcurrentHashMap;

public class TaskletUtils {
    private static ConcurrentHashMap<Integer, Tasklet> tasklets = new ConcurrentHashMap<>();

    public static void put(Tasklet tasklet) {
        tasklets.put(tasklet.getSeq(), tasklet);
    }

    public static Tasklet get(int seq) {
        return tasklets.get(seq);
    }

}
