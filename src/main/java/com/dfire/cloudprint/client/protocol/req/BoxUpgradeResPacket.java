package com.dfire.cloudprint.client.protocol.req;

import com.dfire.cloudprint.client.protocol.AbsPacket;
import com.dfire.cloudprint.client.protocol.InputByte;

public class BoxUpgradeResPacket extends AbsPacket{
    
    public final static int BODY_LENGTH = 0;

    public BoxUpgradeResPacket(InputByte input) {
        super(input);
    }

    @Override
    public int getLength() {
        return BODY_LENGTH;
    }

}
