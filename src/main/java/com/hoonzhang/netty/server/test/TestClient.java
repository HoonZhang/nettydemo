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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class TestClient {
    private static final Logger log = LoggerFactory.getLogger(TestClient.class);

    private static final int cmd = 101;

    private static int threadSize = 1;
    private static int num = 1;
    private static ConcurrentHashMap<Integer, Integer> seqs = new ConcurrentHashMap<>(threadSize * num);
    private static AtomicInteger seqGenerator = new AtomicInteger();
    private static int count = 0;

    synchronized private static void addCount() {
        ++count;
    }

    public static int f() {
        log.info("f...");
        return 10;
    }

    public static int aa(final CountDownLatch latch) {

        final List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);

        int i = 1;
        try {

            for (int threadIndex = 0; threadIndex < 10; ++threadIndex) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Random random = new Random();
                        if (random.nextInt() % 2 == 0) {
                            list.add(1);
                        } else {
                            for (Integer a : list) {
                                log.info("list size:{}, value:{}", list.size(), a);
                            }
                        }
                        latch.countDown();
                    }
                }, "thread-" + (threadIndex + 1)).start();
            }

        } catch (Exception e) {
            log.info("catch...");
        } finally {
            log.info("finally..., i={}", i);
        }

        log.info("end...");
        return i;
    }

    public static void main1(String[] args) {
        final CountDownLatch latch = new CountDownLatch(10);

        aa(latch);

        try {
            latch.await();
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }

        for (Map.Entry<Integer, Integer> entry : seqs.entrySet()) {
            log.info("seq:{}, cnt:{}", entry.getKey(), entry.getValue());
        }
    }

    public static void main(String[] args) {
        long t1 = System.currentTimeMillis();

        String ip = "127.0.0.1";
        int port = 10086;

        if (args.length == 3) {
            threadSize = Integer.parseInt(args[0]);
            num = Integer.parseInt(args[1]);
            ip = args[2];
        }

        final String serverIp = ip;

        log.error("------------ client start, t1={}, threadSize:{}", t1, threadSize);

        final CountDownLatch latch = new CountDownLatch(threadSize);

        for (int i = 0; i < threadSize; ++i) {
            Thread thread = new Thread(() -> {
                try {
                    TestClient.send(serverIp, port, num);
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

        log.error("------------client end, cost:{}, count:{}", System.currentTimeMillis() - t1, count);
    }

    private static void send(String ip, int port, int num) throws IOException {
        Socket socket = new Socket(ip, port);
        OutputStream out = socket.getOutputStream();
        InputStream in = socket.getInputStream();
        log.info("{}", socket.getLocalSocketAddress());

        for (int k = 0; k < num; ++k) {
            Head head = new Head();
            head.setCmd(cmd);
            head.setSeq(seqGenerator.incrementAndGet());

            MsgPacket msg = new MsgPacket();
            msg.setHead(head);

            log.info("send msg head:{}", msg.getHead());

            out.write(MsgPacketEncoder.encodeToBytes(msg));
            out.flush();

            byte[] buf = new byte[10240];
            int readLen = 0;
            while (readLen < 20) {
                int len = in.read(buf, readLen, 1024);
                if (len <= 0) {
                    log.info("len={}\n", len);
                    break;
                }
                readLen += len;
            }

            MsgPacket respMsg = new MsgPacket();
            MsgPacketDecoder.decode(buf, respMsg);
//        System.out.println(respMsg);
            log.info("receive len={}, seq:{}", readLen, respMsg.getHead().getSeq());
        }
        out.close();
        in.close();
        socket.close();

        addCount();
    }
}
