package com.hoonzhang.netty.server.codec.header;

public class HeadConstant {
    //不含body的数据包长度
    public static final byte kEmptyPacketLen = 20;
    public static final byte kHeadSoh = 0x01;
    public static final byte kHeadEot = 0x10;
}
