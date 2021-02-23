package com.dfire.cloudprint.client.protocol.req;

import com.dfire.cloudprint.client.protocol.AbsPacket;
import com.dfire.cloudprint.client.protocol.InputByte;
import com.dfire.cloudprint.client.protocol.OutputByte;

public class PrintReqPacket extends AbsPacket{

    public  final static int BODY_LENGTH = 68;

    private final static int SN_OR_MAC_INDEX = 0;

    private final static int RECORD_ID_INDEX = 32;

    public final static int CONTENT_LENGTH_INDEX = 64;

    private final static int CONTENT_INDEX = 68;

    
    public PrintReqPacket(InputByte input) {
        super(input);
    }
    
    /**
     * 打印机序列号或MAC
     * @return
     */
    public String getPrinterSnOrMac() {
        return getUtf8String(SN_OR_MAC_INDEX, 32);
    }

    /**
     * 待打印的PDF标志ID
     * @return
     */
    public String getPrintRecordId() {
        return getUtf8String(RECORD_ID_INDEX, 32);
    }

    /**
     * 文件大小
     * @return
     */
    public int getContentLength() {
        return getInt(CONTENT_LENGTH_INDEX);
    }

    /**
     * 待打印的PDF文件的二进制流，N对应上面的文件大小（不参与校验和）
     * @return
     */
    public byte[] getContent() {
        return getBytes(CONTENT_INDEX, getContentLength());
    }

    @Override
    public int getLength() {
        return BODY_LENGTH + getContentLength();
    }
    
    public static class Builder extends AbsPacket.Buidler{

        public Builder(OutputByte output) {
            super(output);
        }

        public Builder printerMacOrSn(String printerMacOrSn){
            putUtf8String(SN_OR_MAC_INDEX, printerMacOrSn, 32);
            return this;
        }
        
        public Builder recordId(String recordId){
            putUtf8String(RECORD_ID_INDEX, recordId, 32);
            return this;
        }
        
        public Builder content(byte[] content){
            int len = content.length;
            putInt(CONTENT_LENGTH_INDEX, len);
            putBytes(CONTENT_INDEX, content);
            return this;
        }
        
        @Override
        public PrintReqPacket build() {
            int len = getInt(CONTENT_LENGTH_INDEX);
            return new PrintReqPacket(output.toInputByte(BODY_LENGTH + len));
        }
    }

}
