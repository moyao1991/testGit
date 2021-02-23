package com.dfire.cloudprint.client.protocol.notify;

import com.dfire.cloudprint.client.protocol.AbsPacket;
import com.dfire.cloudprint.client.protocol.InputByte;
import com.dfire.cloudprint.client.protocol.OutputByte;

public class PrintResultPacket extends AbsPacket{

    public final static int BODY_LENGTH = 33;

    private final static int PRINT_RECORD_ID_INDEX = 0;
    private final static int RESULT_INDEX = 32;


    public PrintResultPacket(InputByte input) {
        super(input);
    }
    
    public String getPrintRecordId() {
        return getUtf8String(PRINT_RECORD_ID_INDEX, 32);
    }

    public boolean getResult() {
        return getBoolean(RESULT_INDEX);
    }

    @Override
    public int getLength() {
        return BODY_LENGTH;
    }
    
    public static class Builder extends AbsPacket.Buidler{

        public Builder(OutputByte output) {
            super(output);
        }
        
        public Builder success(String recordId, boolean success){
            putUtf8String(PRINT_RECORD_ID_INDEX, recordId, 32);
            putBoolean(RESULT_INDEX, success);
            return this;
        }
        
        @Override
        public PrintResultPacket build() {
            return new PrintResultPacket(output.toInputByte(BODY_LENGTH));
        }
    }

}
