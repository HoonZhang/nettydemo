package com.hoonzhang.netty.server.test;

import com.hoonzhang.netty.server.codec.decode.MsgPacketDecoder;
import com.hoonzhang.netty.server.codec.encode.MsgPacketEncoder;
import com.hoonzhang.netty.server.codec.header.Head;
import com.hoonzhang.netty.server.codec.packet.MsgPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;

public class TestTaskletClient {
    private static final Logger log = LoggerFactory.getLogger(TestTaskletClient.class);

    public static void main(String[] args) {
        long t1 = System.currentTimeMillis();
        log.info("------------ client start, t1={}", t1);

        String ip = "127.0.0.1";
        int port = 10086;
        int threadSize = 10000;
        CountDownLatch latch = new CountDownLatch(threadSize);

        for (int i = 0; i < threadSize; ++i) {
            Thread thread = new Thread(() -> {
                try {
                    TestTaskletClient.send(ip, port);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                } finally {
                    latch.countDown();
                }
            }, "client=" + i);
            thread.start();
        }

        try {
            latch.await();

        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }

        log.info("------------client end, cost:{}", System.currentTimeMillis() - t1);
    }

    private static void send(String ip, int port) throws IOException {
        Socket socket = new Socket(ip, port);
        OutputStream out = socket.getOutputStream();
        InputStream in = socket.getInputStream();
        log.info("{}", socket.getLocalSocketAddress());

        Head head = new Head();
        head.setCmd(101);

        MsgPacket msg = new MsgPacket();
        msg.setHead(head);

//        log.info("msg=", msg);

        out.write(MsgPacketEncoder.encodeToBytes(msg));
        out.flush();

        byte[] buf = new byte[10240];
        int readLen = 0;
        while (readLen < 20) {
            int len = in.read(buf, readLen, 1024);
            if (len <= 0) {
                log.info("len=%d\n", len);
                break;
            }
            readLen += len;
        }

        MsgPacket respMsg = new MsgPacket();
        MsgPacketDecoder.decode(buf, respMsg);
//        System.out.println(respMsg);

        out.close();
        in.close();
        socket.close();
    }
}
