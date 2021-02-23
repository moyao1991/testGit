package com.dfire.cloudprint.client.netty.packet;

import com.dfire.cloudprint.client.protocol.AbsPacket;
import com.dfire.cloudprint.client.protocol.HeadPacket;
import com.dfire.cloudprint.client.protocol.enums.CommandTypeEnum;
import com.dfire.cloudprint.client.protocol.req.PrintReqPacket;

import io.netty.buffer.ByteBuf;

public class ResultPacket extends Packets{

    private ResultPacket(AbsPacket body) {
        super(body);
    }

    public void toByteBuffer(ByteBuf in){
        in.resetWriterIndex();
        byte[] headBuffer = head.getBytes();
        byte[] bodyBuffer = body.getBytes();
        
        int len = bodyBuffer.length;
        if(body instanceof PrintReqPacket){
            len -= ((PrintReqPacket)body).getContentLength();
        }
        
        int crc = generateCrc(headBuffer) ^ generateCrc(bodyBuffer, len);
        in.writeBytes(headBuffer);
        in.setInt(CRT_INDEX , crc);
        in.writeBytes(bodyBuffer);
    }
    
    private void parsePacket(){
        int packetLen = HEAD_LENGTH + body.getLength();
        
        if(body instanceof PrintReqPacket){
            packetLen -= ((PrintReqPacket)body).getContentLength();
        }
        
        CommandTypeEnum command = CommandTypeEnum.valueOf(body.getClass());
        head = new HeadPacket.Builder(new ByteOutputAdapter(HeadPacket.BODY_LENGTH)).command(command).packetLength((short)packetLen).build();
    }
    
    private int generateCrc(byte[] arr) {
        return generateCrc(arr, arr.length);
    }
    
    private int generateCrc(byte[] arr, int len) {
        int crc = 0;
        for (int i = 0; i < len; i++) {
            crc ^= arr[i] & 0xFF;
        }
        return crc;
    }

    public static ResultPacket buildResult(AbsPacket body) {
        ResultPacket result = new ResultPacket(body);
        result.parsePacket();
        return result;
    }
    
}
