package com.dfire.cloudprint.client.netty.packet;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;
import com.dfire.cloudprint.client.protocol.HeadPacket;
import com.dfire.cloudprint.client.protocol.InputByte;
import com.dfire.cloudprint.client.protocol.enums.CommandTypeEnum;
import io.netty.buffer.ByteBuf;

public class AcceptPacket extends Packets{
        
    private AcceptPacket(){
        super(null);
    }
    
    private void parseBuffer(InputByte input) throws Exception {
        
        Validate.isTrue(input.capacity() >= HEAD_LENGTH, "capacity[%d],head[%d]",
                input.capacity() , HEAD_LENGTH);
        
        Pair<InputByte, InputByte> pair = input.split(HEAD_LENGTH);
        
        InputByte headByte = pair.getLeft();
        
        InputByte bodyByte = pair.getRight();

        head = new HeadPacket(headByte);
        
        CommandTypeEnum comand = head.getCommand();
        
        Validate.notNull(comand, "command");

        body = comand.bodyClz(bodyByte);
        
    }
           
    private static void checkByteBuf(ByteBuf dup, int len) {
        short crc = 0;
        for (int i = 0; i < len; i++) {
            crc ^= dup.readUnsignedByte();
        }
        Validate.isTrue(crc == 0, "crt");
    }
    
    private static void checkByteBuf(ByteBuf buf) {
        ByteBuf dup = buf.duplicate();
        dup.resetReaderIndex();
        int len = buf.readableBytes();
        if(buf.getUnsignedShort(COMMAND_INDEX) == 104){
            len = len - buf.getInt(PRINT_REQ_CONTENT_LENGTH_INDEX);
            return;//老版本104 packetlen乱搞加此一行
        }
        checkByteBuf(dup , len);
    }
    
    
    public static AcceptPacket accept(ByteBuf in) throws Exception{

        checkByteBuf(in);
        InputByte input = new ByteInputAdapter(in);
        AcceptPacket packet = new AcceptPacket();
        packet.parseBuffer(input);
        
        return packet;
    }
    
}
