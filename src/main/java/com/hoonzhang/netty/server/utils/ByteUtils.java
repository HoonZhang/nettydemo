package com.hoonzhang.netty.server.utils;

public class ByteUtils {
    public static short readShortBe(byte[] buf, int offset) {
        return (short) (((buf[offset] & 0xFF) << 8) | (buf[offset + 1] & 0xFF));
    }

    public static int readIntBe(byte[] buf, int offset) {
        return ((buf[offset] & 0xFF) << 24) | ((buf[offset + 1] & 0xFF) << 16) | ((buf[offset + 2] & 0xFF) << 8) | (buf[offset + 3] & 0xFF);
    }

    /**
     * 大端模式，short写入字符数组
     *
     * @param value
     * @param buf
     * @param offset
     */
    public static void writeShortBe(short value, byte[] buf, int offset) {
        buf[offset] = (byte) (value >> 8 & 0xFF);
        buf[offset + 1] = (byte) (value & 0xFF);
    }

    /**
     * 大端模式，int写入字符数组
     *
     * @param value
     * @param buf
     * @param offset
     */
    public static void writeIntBe(int value, byte[] buf, int offset) {
//        System.out.printf("value:%08x\n", value);
        buf[offset] = (byte) (value >> 24 & 0xFF);
        buf[offset + 1] = (byte) (value >> 16 & 0xFF);
        buf[offset + 2] = (byte) (value >> 8 & 0xFF);
        buf[offset + 3] = (byte) (value & 0xFF);
//        System.out.printf("write buf:%08x %08x %08x %08x\n", buf[offset], buf[offset + 1], buf[offset + 2], buf[offset + 3]);
    }
}
