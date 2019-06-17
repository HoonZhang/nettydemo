package com.hoonzhang.netty.server.codec.handler;

import com.hoonzhang.netty.server.codec.header.Head;
import com.hoonzhang.netty.server.codec.header.HeadConstant;
import com.hoonzhang.netty.server.codec.packet.MsgPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class MsgDecoder extends ByteToMessageDecoder {
    private static final Logger log = LoggerFactory.getLogger(MsgDecoder.class);

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int readableLen = in.readableBytes();
        if (readableLen < HeadConstant.kEmptyPacketLen) {
            log.error("invalid readable len:{}", readableLen);
            return;
        }
        Head head = new Head();
        //开始字符
        head.setSoh(in.readByte());
        //如果不等于自定义的常量就丢弃
        if (head.getSoh() != HeadConstant.kHeadSoh) {
            log.error("invalid soh:{}", head.getSoh());
            return;
        }
        head.setVersion(in.readShort());
        head.setLength(in.readInt());
        if (readableLen < head.getLength()) {
            log.error("invalid readableLen:{} less than length:{}", readableLen, head.getLength());
            return;
        }

        head.setCmd(in.readInt());
        head.setSeq(in.readInt());
        head.setErrorCode(in.readInt());

        MsgPacket msgPacket = new MsgPacket();
        int bodyLen = head.getLength() - HeadConstant.kEmptyPacketLen;
        if (bodyLen > 0) {
            byte[] body = new byte[bodyLen];
            in.readBytes(body);
            msgPacket.setBody(body);
        }

        head.setEot(in.readByte());

        msgPacket.setHead(head);

        out.add(msgPacket);
        log.info("------------- msg decode end, seq:{}", head.getSeq());
    }
}
