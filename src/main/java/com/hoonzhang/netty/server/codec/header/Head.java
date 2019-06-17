package com.hoonzhang.netty.server.codec.header;

/**
 * <pre>
 *
 * HEAD PROTOCOL (20 bytes) and Body
 * +--------+-----------------------------------------------------------+---------+--------+
 * | Soh(1) | Version(2) |  Length(4) | Cmd(4) | Seq(4) | Error Code(4) | Body(x) | Eot(1) |
 * +--------+-----------------------------------------------------------+---------+--------+
 * </pre>
 */
public class Head implements Cloneable {
    //开始字符，用于检验
    private byte soh;
    //结束字符，用于检验
    private byte eot;
    //协议头版本
    private short version;
    //包总长度，包括body部分
    private int length;
    //命令字
    private int cmd;
    //请求序列号
    private int seq;
    //返回码
    private int errorCode;

    public Head() {
        this.soh = HeadConstant.kHeadSoh;
        this.eot = HeadConstant.kHeadEot;
        this.length = HeadConstant.kEmptyPacketLen;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public String toString() {
        return "Head{" +
                "soh=" + soh +
                ", eot=" + eot +
                ", version=" + version +
                ", length=" + length +
                ", cmd=" + cmd +
                ", seq=" + seq +
                ", errorCode=" + errorCode +
                '}';
    }

    public byte getSoh() {
        return soh;
    }

    public void setSoh(byte soh) {
        this.soh = soh;
    }

    public byte getEot() {
        return eot;
    }

    public void setEot(byte eot) {
        this.eot = eot;
    }

    public short getVersion() {
        return version;
    }

    public void setVersion(short version) {
        this.version = version;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getCmd() {
        return cmd;
    }

    public void setCmd(int cmd) {
        this.cmd = cmd;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }
}
