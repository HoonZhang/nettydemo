package com.hoonzhang.nettt.test;

import com.hoonzhang.netty.server.NettyServer;
import com.hoonzhang.netty.server.codec.decode.MsgPacketDecoder;
import com.hoonzhang.netty.server.codec.encode.MsgPacketEncoder;
import com.hoonzhang.netty.server.codec.header.Head;
import com.hoonzhang.netty.server.codec.packet.MsgPacket;
import com.hoonzhang.netty.server.tasklet.TaskletFactoryUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteOrder;

public class TestMain {
    private static final Logger log = LoggerFactory.getLogger(TestMain.class);

    public static void main(String[] args) {
        System.out.println(ByteOrder.nativeOrder());
        log.info("------------ server:");

        NettyServer nettyServer = new NettyServer(10086, 1, 10, 10);
        nettyServer.start();
        TaskletFactoryUtils.init(new TestTaskletFactory());

    }

}
