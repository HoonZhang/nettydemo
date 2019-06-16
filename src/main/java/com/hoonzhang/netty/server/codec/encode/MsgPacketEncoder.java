package com.hoonzhang.netty.server.codec.encode;

import com.hoonzhang.netty.server.codec.header.Head;
import com.hoonzhang.netty.server.codec.header.HeadConstant;
import com.hoonzhang.netty.server.codec.packet.MsgPacket;
import com.hoonzhang.netty.server.utils.ByteUtils;

import java.util.Arrays;

public class MsgPacketEncoder {
    public static byte[] encodeToBytes(MsgPacket pkt) {
        int bodyLen = (pkt.getBody() != null) ? pkt.getBody().length : 0;
        int packetLen = HeadConstant.kEmptyPacketLen + bodyLen;
        Head head = pkt.getHead();
        int pos = 0;
        byte[] buf = new byte[packetLen];
        buf[pos++] = head.getSoh();

        ByteUtils.writeShortBe(head.getVersion(), buf, pos);
        pos += 2;

        ByteUtils.writeIntBe(packetLen, buf, pos);
        pos += 4;

        ByteUtils.writeIntBe(head.getCmd(), buf, pos);
        pos += 4;

        ByteUtils.writeIntBe(head.getSeq(), buf, pos);
        pos += 4;

        ByteUtils.writeIntBe(head.getErrorCode(), buf, pos);
        pos += 4;

        if (bodyLen > 0) {
            System.arraycopy(pkt.getBody(), 0, buf, pos, bodyLen);
            pos += bodyLen;
        }

        buf[pos] = head.getEot();

//        System.out.println(Arrays.toString(buf));

        return buf;
    }
}
