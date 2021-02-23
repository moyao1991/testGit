package com.dfire.cloudprint.client.terminal;

import org.apache.commons.codec.binary.Hex;
import com.alibaba.fastjson.JSON;
import com.dfire.cloudprint.client.netty.packet.AcceptPacket;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

public class AcceptPacketExmaple {
     
    public static void printPacket(String hex){
        try{
            char[] cs = hex.toCharArray();
            ByteBuf in = ByteBufAllocator.DEFAULT.buffer();
            byte[] aa = Hex.decodeHex(cs);
            in.writeBytes(aa);
            AcceptPacket packet = AcceptPacket.accept(in);
            System.out.println(JSON.toJSONString(packet));
        }catch(Exception e){
            e.printStackTrace();
        }
      
    } 
    
    
    public static void main(String[] args) {
        
    }
}
