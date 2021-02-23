package com.dfire.cloudprint.client.netty.packet;

import com.dfire.cloudprint.client.Constants;
import com.dfire.cloudprint.client.protocol.AbsPacket;
import com.dfire.cloudprint.client.protocol.HeadPacket;
import com.dfire.cloudprint.client.protocol.req.PrintReqPacket;

public abstract class Packets {
    
    public static final int HEAD_LENGTH =  HeadPacket.BODY_LENGTH;
    
    public static final short PACKET_LENGTH_INDEX =  HeadPacket.PACKET_LENGTH_INDEX;

    public static final short FLAG_INDEX = HeadPacket.FLAG_INDEX;
    
    public static final int COMMAND_INDEX =  HeadPacket.COMMAND_INDEX;

    public static final int PRINT_REQ_CONTENT_LENGTH_INDEX =  HEAD_LENGTH + PrintReqPacket.CONTENT_LENGTH_INDEX;

    protected static final int CRT_INDEX =  HeadPacket.CRT_INDEX;

            
    protected HeadPacket head;
    
    protected AbsPacket body;
    
    
    protected Packets(AbsPacket body){
        this.body = body;
    }
    
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer(head.hexDump());
        sb.append(Constants.MID_SPILE);
        sb.append(body.hexDump());
        return sb.toString();
    }
   
    public HeadPacket getHeader() {
        return head;
    }

    public AbsPacket getBody(){
        return body;
    }
    
}
