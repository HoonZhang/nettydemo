package com.hoonzhang.netty.server.tasklet;

public class TaskletFactoryUtils {
    private static TaskletFactory taskletFactory;

    public static void init(TaskletFactory taskletFactory) {
        TaskletFactoryUtils.taskletFactory = taskletFactory;
    }

    public static TaskletFactory getTaskletFactory() {
        return taskletFactory;
    }

}
