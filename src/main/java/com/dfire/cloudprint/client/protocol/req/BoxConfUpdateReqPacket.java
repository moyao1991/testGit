package com.dfire.cloudprint.client.protocol.req;

import com.dfire.cloudprint.client.protocol.AbsPacket;
import com.dfire.cloudprint.client.protocol.InputByte;
import com.dfire.cloudprint.client.protocol.OutputByte;

public class BoxConfUpdateReqPacket extends AbsPacket{

    public  final static int BODY_LENGTH = 4;

    private final static int HEARTBEAT_INTERVAL_INDEX = 0;
    private final static int PRINTERINFO_INTERVAL_INDEX = 2;
    
    
    public BoxConfUpdateReqPacket(InputByte input) {
        super(input);
    }

    /**
     * 心跳时间间隔
     */
    public short getHeartbeatInterval() {
        return getShort(HEARTBEAT_INTERVAL_INDEX);
    }

    /**
     * 上报打印机信息时间间隔
     */
    public short getPrinterInfoInterval() {
        return getShort(PRINTERINFO_INTERVAL_INDEX);
    }
    
    @Override
    public int getLength() {
        return BODY_LENGTH;
    }
    
    public static class Builder extends AbsPacket.Buidler{

        public Builder(OutputByte output) {
            super(output);
        }

        public Builder heartBeat(short interval){
            putShort(HEARTBEAT_INTERVAL_INDEX, interval);
            return this;
        }
        
        public Builder printerInfo(short interval){
            putShort(PRINTERINFO_INTERVAL_INDEX, interval);
            return this;
        }
        
        @Override
        public BoxConfUpdateReqPacket build() {
            return new BoxConfUpdateReqPacket(output.toInputByte(BODY_LENGTH));
        }
    }

}
