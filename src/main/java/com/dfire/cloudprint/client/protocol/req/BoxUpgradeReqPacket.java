package com.dfire.cloudprint.client.protocol.req;

import org.apache.commons.lang3.Validate;

import com.dfire.cloudprint.client.protocol.AbsPacket;
import com.dfire.cloudprint.client.protocol.InputByte;
import com.dfire.cloudprint.client.protocol.OutputByte;

public class BoxUpgradeReqPacket extends AbsPacket{

    private  final static int BODY_LENGTH = 12;

    private final static int APPVERSION_INDEX = 0;
    private final static int URLLENGTH_INDEX = 10;
    private final static int URL_INDEX = 12;

    public BoxUpgradeReqPacket(InputByte input) {
        super(input);
    }
    
    public String getAppVersion() {
        return getUtf8String(APPVERSION_INDEX, 10);
    }

    public short getUrlLength() {
        return getShort(URLLENGTH_INDEX);
    }

    public String getUrl() {
        short len = getUrlLength();
        return getUtf8String(URL_INDEX, len);
    }

    @Override
    public int getLength() {
        return BODY_LENGTH+getUrlLength();
    }
    
    public static class Builder extends AbsPacket.Buidler{

        public Builder(OutputByte output) {
            super(output);
        }

        public Builder url(String url){
            int  len = putUtf8String(URL_INDEX, url);
            putShort(URLLENGTH_INDEX, (short)len);
            return this;
        }
        
        public Builder version(String version){
            Validate.isTrue(putUtf8String(APPVERSION_INDEX, version, 10), "version[s%] more than 10");
            return this;
        }
        
        @Override
        public BoxUpgradeReqPacket build() {
            short urlLen = getShort(URLLENGTH_INDEX);
            return new BoxUpgradeReqPacket(output.toInputByte(BODY_LENGTH + urlLen));
        }
    }

}
