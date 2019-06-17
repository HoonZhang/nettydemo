package com.hoonzhang.netty.server.tasklet;

import com.hoonzhang.netty.server.codec.packet.MsgPacket;
import io.netty.channel.ChannelHandlerContext;

public abstract class Tasklet {
    //当前step的seq
    private int seq;
    private int step;

    private ChannelHandlerContext ctx;

    public Tasklet(int step) {
        this.step = step;
    }

    public abstract int doNextStep(MsgPacket msg);

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }
}
