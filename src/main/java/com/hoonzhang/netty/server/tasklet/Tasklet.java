package com.hoonzhang.netty.server.tasklet;

import com.hoonzhang.netty.server.codec.packet.MsgPacket;
import io.netty.channel.ChannelHandlerContext;

public abstract class Tasklet {
    //当前step的seq
    private int seq;
    private int step;
    protected long timestemp;
    protected long createTimestamp;
    protected long addTimestamp;

    private ChannelHandlerContext ctx;

    public Tasklet(int step) {
        this.step = step;
    }

    public abstract int doNextStep(MsgPacket msg);

    public void onExpire() {
    }

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

    public long getTimestemp() {
        return timestemp;
    }

    public void setTimestemp(long timestemp) {
        this.timestemp = timestemp;
    }
}
