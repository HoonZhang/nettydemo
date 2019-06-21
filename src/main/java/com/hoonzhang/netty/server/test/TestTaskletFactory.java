package com.hoonzhang.netty.server.test;

import com.hoonzhang.netty.server.codec.packet.MsgPacket;
import com.hoonzhang.netty.server.tasklet.Tasklet;
import com.hoonzhang.netty.server.tasklet.ITaskletFactory;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestTaskletFactory implements ITaskletFactory {
    private static final Logger log = LoggerFactory.getLogger(TestTaskletFactory.class);

    @Override
    public Tasklet create(MsgPacket msg, ChannelHandlerContext ctx) {
        if (msg == null || msg.getHead() == null) {
            log.error("invalid msg:{}", msg);
            return null;
        }

        log.info("--- create tasklet, seq:{}", msg.getHead().getSeq());
        Tasklet tasklet = null;

        switch (msg.getHead().getCmd()) {
            case 101:
                tasklet = new TestTasklet(msg, ctx);
                break;
            case 201:
                tasklet = new TestTasklet2(msg, ctx);
                break;
            default:
                log.error("unknown cmd:{}", msg.getHead().getCmd());
        }
        return tasklet;
    }
}
