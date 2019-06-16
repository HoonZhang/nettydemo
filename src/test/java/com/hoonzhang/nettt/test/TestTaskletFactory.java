package com.hoonzhang.nettt.test;

import com.hoonzhang.netty.server.codec.packet.MsgPacket;
import com.hoonzhang.netty.server.tasklet.Tasklet;
import com.hoonzhang.netty.server.tasklet.TaskletFactory;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestTaskletFactory implements TaskletFactory {
    private static final Logger log = LoggerFactory.getLogger(TestTaskletFactory.class);

    @Override
    public Tasklet create(MsgPacket msg, ChannelHandlerContext ctx) {
        log.info("--- create tasklet");
        Tasklet tasklet = new TestTasklet(msg, ctx);
        return tasklet;
    }
}
