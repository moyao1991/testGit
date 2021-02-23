package com.dfire.cloudprint.client.protocol.notify;

import com.dfire.cloudprint.client.protocol.AbsPacket;
import com.dfire.cloudprint.client.protocol.InputByte;

public class DisconnectPacket extends AbsPacket{

    public final static int BODY_LENGTH = 0;

    public DisconnectPacket(InputByte input) {
        super(input);
    }
    
    @Override
    public int getLength() {
        return BODY_LENGTH;
    }

}
