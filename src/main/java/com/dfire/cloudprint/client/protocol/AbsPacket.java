package com.dfire.cloudprint.client.protocol;

import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.lang3.Validate;

public abstract class AbsPacket {

    private static final byte SUCCESS = 0b01;
    
    private static final byte FAILED = 0b00;

    private InputByte input;
    
    public AbsPacket(InputByte input){
       this(input, false);
    }
    
    private AbsPacket(InputByte input, boolean flag){
        this.input = input;
        Validate.isTrue(flag || input.capacity() == getLength() , 
                "input capacity[%d] not equel %s[%d]", 
                input.capacity(), getClass().getSimpleName(), getLength());
    }
    
    public abstract int getLength();
    
    protected boolean getBoolean(int index){
        return input.getByte(index) == SUCCESS ? true : false;
    }
    
    protected  short getShort(int index){
        return input.getShort(index);
    }
    
    protected  int getInt(int index){
        return input.getInt(index);
    }

    protected String getUtf8String(int index, int len){
         byte[] dst = input.getBytes(index, len);
         return StringUtils.newStringUtf8(dst).trim();
    }

    public String hexDump(){
        return input.hexDump();
    }
    
    public byte[] getBytes(){
        return input.getBytes();
    }
    
    public byte[] getBytes(int index, int len){
        return input.getBytes(index, len);
    }
    
    public static abstract class Buidler{
        
        protected OutputByte output;

        public Buidler(OutputByte output) {
            this.output = output;
        }
        
        protected void putBoolean(int index, boolean value){
            output.putByte(index, value ? SUCCESS : FAILED);
        }
        
        protected void putBytes(int index, byte[] value){
            output.putBytes(index, value);
        }
        
        protected  short getShort(int index){
            return output.getShort(index);
        }
        
        protected  int getInt(int index){
            return output.getInt(index);
        }
        
        protected  void putShort(int index, short value){
             output.putShort(index, value);
        }
        
        protected  void putInt(int index, int value){
             output.putInt(index, value);
        }
        
        protected boolean putUtf8String(int index, String value, int len) {
            byte[] src = StringUtils.getBytesUtf8(value);
            if(src.length > len) return false;
            
            if(src.length == len){
                output.putBytes(index, src);
                return true;
            }
            
            byte[] dest = new byte[len];
            System.arraycopy(src, 0, dest, 0, src.length);
            output.putBytes(index, dest);
            return true;
        }
        
        protected int putUtf8String(int index, String value) {
            byte[] src = StringUtils.getBytesUtf8(value.trim());
            output.putBytes(index, src);
            return src.length;
        }
        
        public String hexDump(){
            return output.hexDump();
        }
                
        public abstract AbsPacket build();

    }
}
