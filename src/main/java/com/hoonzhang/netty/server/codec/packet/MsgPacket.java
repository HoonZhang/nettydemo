package com.hoonzhang.netty.server.codec.packet;

import com.hoonzhang.netty.server.codec.header.Head;

import java.util.Arrays;

public class MsgPacket implements Cloneable {
    private Head head;
    private byte[] body;

    @Override
    public Object clone() throws CloneNotSupportedException {
        MsgPacket msgPacket = (MsgPacket) super.clone();
        if (head != null) {
            msgPacket.setHead((Head) head.clone());
        }
        if (body != null) {
            msgPacket.setBody(body.clone());
        }
        return super.clone();
    }

    @Override
    public String toString() {
        return "MsgPacket{" +
                "head=" + head +
                ", body=" + Arrays.toString(body) +
                '}';
    }

    public Head getHead() {
        return head;
    }

    public void setHead(Head head) {
        this.head = head;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }
}
