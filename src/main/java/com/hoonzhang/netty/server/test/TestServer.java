package com.hoonzhang.netty.server.test;

import com.hoonzhang.netty.server.NettyServer;
import com.hoonzhang.netty.server.tasklet.TaskletFactoryUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteOrder;

public class TestServer {
    private static final Logger log = LoggerFactory.getLogger(TestServer.class);

    public static void main(String[] args) {
        System.out.println(ByteOrder.nativeOrder());
        int port = 10086;
        int accThreadPoolSize = 1;
        int ioThreadPoolSize = 2;
        int workThreadPoosSize = 2;

        if (args.length == 4) {
            port = Integer.parseInt(args[0]);
            accThreadPoolSize = Integer.parseInt(args[1]);
            ioThreadPoolSize = Integer.parseInt(args[2]);
            workThreadPoosSize = Integer.parseInt(args[3]);
        }

        log.info("------------ server:");

        NettyServer nettyServer = new NettyServer(port, accThreadPoolSize, ioThreadPoolSize, workThreadPoosSize);
        nettyServer.start();
        TaskletFactoryUtils.init(new TestTaskletFactory());

    }

}
