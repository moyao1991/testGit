package com.dfire.cloudprint.client.protocol.notify;

import org.apache.commons.lang3.Validate;
import com.dfire.cloudprint.client.config.PrinterInfo;
import com.dfire.cloudprint.client.protocol.AbsPacket;
import com.dfire.cloudprint.client.protocol.InputByte;
import com.dfire.cloudprint.client.protocol.OutputByte;


public class PrinterInfoPacket extends AbsPacket{

    private final static int BODY_LENGTH = 4;
    
    private final static int PRINT_SIZE = 116;

    private final static int PRINT_COUNT_INDEX = 0;

    private final static int BRAND_CODE_INDEX = 4;

    private final static int MODEL_CODE_INDEX = 36;

    private final static int SN_INDEX = 68;

    private final static int MAC_INDEX = 100;

    
    public PrinterInfoPacket(InputByte input) {
        super(input);
    }

    /**
     * 打印机个数 
     * @return
     */
    public int getPrintCount(){
        return getInt(PRINT_COUNT_INDEX);
    }
    
    /**
     * 打印机num品牌，举例：EPSON
     * @param num
     * @return
     */
    public String getBrandCode(int num){
        Validate.isTrue(num < getPrintCount() , " num between 0,%d" , getPrintCount()-1);
        return getUtf8String(BRAND_CODE_INDEX + num * PRINT_SIZE, 32);
    }
    
    /**
     * 打印机num型号，举例：LQ-630K
     * @param num
     * @return
     */
    public String getModelCode(int num){
        Validate.isTrue(num < getPrintCount() , " num between 0,%d" , getPrintCount()-1);
        return getUtf8String(MODEL_CODE_INDEX + num * PRINT_SIZE, 32);
    }
    
    /**
     * 打印机num序列号
     * @param num
     * @return
     */
    public String getSn(int num){
        Validate.isTrue(num < getPrintCount() , " num between 0,%d" , getPrintCount()-1);
        return getUtf8String(SN_INDEX + num * PRINT_SIZE, 32);
    }
    
    /**
     * 打印机num MAC地址，例：005056C00011
     * @return
     */
    public String getMac(int num) {
        Validate.isTrue(num < getPrintCount() , " num between 0,%d" , getPrintCount()-1);
        return getUtf8String(MAC_INDEX + num * PRINT_SIZE, 20);
    }
    
    @Override
    public int getLength() {
        return BODY_LENGTH + PRINT_SIZE * getPrintCount();
    }
    
    public static class Builder extends AbsPacket.Buidler{

        public Builder(OutputByte output) {
            super(output);
        }

        public Builder printers(PrinterInfo[] printers){
            int size = printers==null? 0 : printers.length;
            putInt(PRINT_COUNT_INDEX, size);
            if(size == 0) return this;

            for (int i = 0; i < printers.length; i++) {
                PrinterInfo p = printers[i];
                putUtf8String(BRAND_CODE_INDEX + i * PRINT_SIZE, p.getBrandCode(), 32);
                putUtf8String(MODEL_CODE_INDEX + i * PRINT_SIZE, p.getModelCode(), 32);
                putUtf8String(SN_INDEX + i * PRINT_SIZE, p.getSn(), 32);
                putUtf8String(MAC_INDEX + i * PRINT_SIZE, p.getMac(), 20);
            }

            return this;
        }
        
        @Override
        public PrinterInfoPacket build() {
            int count = getInt(PRINT_COUNT_INDEX);
            return new PrinterInfoPacket(output.toInputByte(BODY_LENGTH + PRINT_SIZE * count));
        }
    }

}
