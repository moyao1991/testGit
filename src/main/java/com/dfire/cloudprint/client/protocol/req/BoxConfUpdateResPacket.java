package com.dfire.cloudprint.client.protocol.req;

import com.dfire.cloudprint.client.protocol.AbsPacket;
import com.dfire.cloudprint.client.protocol.InputByte;

public class BoxConfUpdateResPacket extends AbsPacket{

    public final static int BODY_LENGTH = 0;
    
    public BoxConfUpdateResPacket(InputByte input) {
        super(input);
    }

    @Override
    public int getLength() {
        return BODY_LENGTH;
    }

}
