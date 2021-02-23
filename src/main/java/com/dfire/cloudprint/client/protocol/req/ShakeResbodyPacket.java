package com.dfire.cloudprint.client.protocol.req;

import com.dfire.cloudprint.client.protocol.AbsPacket;
import com.dfire.cloudprint.client.protocol.InputByte;
import com.dfire.cloudprint.client.protocol.OutputByte;

public class ShakeResbodyPacket extends AbsPacket{

    public static final int BODY_LENGTH = 1;
    
    private final static int RESULT_INDEX = 0;
 

    public ShakeResbodyPacket(InputByte input) {
        super(input);
    }

    /**
     * 认证结果，1： 认证成功   0： 认证失败
     */
    public boolean getResult(){
       return getBoolean(RESULT_INDEX);
    }
    
    @Override
    public int getLength() {
        return BODY_LENGTH;
    }
    
    public static class Builder extends AbsPacket.Buidler{

        public Builder(OutputByte output) {
            super(output);
            putBoolean(RESULT_INDEX, true);
        }

        public Builder success(boolean reuslt){
              putBoolean(RESULT_INDEX, reuslt);
              return this;
        }
        
        @Override
        public ShakeResbodyPacket build() {
            return new ShakeResbodyPacket(output.toInputByte(BODY_LENGTH));
        }
   
    }

}
