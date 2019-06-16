package com.hoonzhang.netty.server.codec.handler;

import com.hoonzhang.netty.server.codec.header.Head;
import com.hoonzhang.netty.server.codec.header.HeadConstant;
import com.hoonzhang.netty.server.codec.packet.MsgPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MsgEncoder extends MessageToByteEncoder<MsgPacket> {
    private static final Logger log = LoggerFactory.getLogger(MsgEncoder.class);

    @Override
    protected void encode(ChannelHandlerContext ctx, MsgPacket msg, ByteBuf out) throws Exception {

        Head head = msg.getHead();
        int packetLen = HeadConstant.kEmptyPacketLen + (msg.getBody() != null ? msg.getBody().length : 0);
        if (packetLen != head.getLength()) {
            log.error("invalid length:{}", head.getLength());
            return;
        }

        //开始字符
        out.writeByte(head.getSoh());

        out.writeShort(head.getVersion());

        out.writeInt(head.getLength());

        out.writeInt(head.getCmd());
        out.writeInt(head.getSeq());
        out.writeInt(head.getErrorCode());

        if (msg.getBody() != null) {
            out.writeBytes(msg.getBody());
        }

        out.writeByte(head.getEot());
    }
}
