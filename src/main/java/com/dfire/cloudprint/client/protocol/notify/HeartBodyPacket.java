package com.dfire.cloudprint.client.protocol.notify;

import com.dfire.cloudprint.client.protocol.AbsPacket;
import com.dfire.cloudprint.client.protocol.InputByte;
import com.dfire.cloudprint.client.protocol.OutputByte;

public class HeartBodyPacket extends AbsPacket {

    public final static int BODY_LENGTH = 0;

    public HeartBodyPacket(InputByte input) {
        super(input);
    }

    @Override
    public int getLength() {
        return BODY_LENGTH;
    }
    
    public static class Builder extends AbsPacket.Buidler{

        public Builder(OutputByte output) {
            super(output);
        }

        @Override
        public HeartBodyPacket build() {
            return new HeartBodyPacket(output.toInputByte(BODY_LENGTH));
        }
   
    }
    
}
