package com.hoonzhang.netty.server.tasklet;

import com.hoonzhang.netty.server.codec.packet.MsgPacket;
import io.netty.channel.ChannelHandlerContext;

public abstract class Tasklet {
    private int seq;

    private ChannelHandlerContext ctx;

    public abstract int doNext(MsgPacket msgPacket);

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }
}
