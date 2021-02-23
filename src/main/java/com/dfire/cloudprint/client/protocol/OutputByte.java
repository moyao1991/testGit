package com.dfire.cloudprint.client.protocol;

public interface OutputByte extends NetByte{

    public void  putByte(int index, byte value);
    
    public void  putBytes(int index, byte[] value);
    
    public void putShort(int index, short value);
  
    public void putInt(int index, int value);
        
    public InputByte toInputByte();
    
    public InputByte toInputByte(int len);
    
}
