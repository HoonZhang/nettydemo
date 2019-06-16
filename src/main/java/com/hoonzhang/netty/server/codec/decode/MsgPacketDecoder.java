package com.hoonzhang.netty.server.codec.decode;

import com.hoonzhang.netty.server.codec.header.Head;
import com.hoonzhang.netty.server.codec.header.HeadConstant;
import com.hoonzhang.netty.server.codec.packet.MsgPacket;
import com.hoonzhang.netty.server.utils.ByteUtils;

public class MsgPacketDecoder {
    public static int decode(byte[] buf, MsgPacket msg) {
        if (buf == null) {
            return -1;
        }

        if (buf.length < HeadConstant.kEmptyPacketLen) {
            return -1;
        }

        int pos = 0;
        Head head = new Head();
        head.setSoh(buf[pos++]);

        head.setVersion(ByteUtils.readShortBe(buf, pos));
        pos += 2;

        head.setLength(ByteUtils.readIntBe(buf, pos));
        pos += 4;

        if (buf.length < head.getLength()) {
            return -1;
        }

        head.setCmd(ByteUtils.readIntBe(buf, pos));
        pos += 4;

        head.setSeq(ByteUtils.readIntBe(buf, pos));
        pos += 4;

        head.setErrorCode(ByteUtils.readIntBe(buf, pos));
        pos += 4;

        int bodyLen = head.getLength() - HeadConstant.kEmptyPacketLen;
        if (bodyLen > 0) {
            byte[] body = new byte[bodyLen];
            System.arraycopy(buf, pos, body, 0, bodyLen);
            msg.setBody(body);
            pos += bodyLen;
        }

        head.setEot(buf[pos]);
        msg.setHead(head);

        return 0;
    }

}
