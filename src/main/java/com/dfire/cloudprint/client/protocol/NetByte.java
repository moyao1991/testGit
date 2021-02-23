package com.dfire.cloudprint.client.protocol;

public interface NetByte {
   
    public int capacity();
    
    public byte  getByte(int index);
    
    public short getShort(int index);
  
    public int getInt(int index);
    
    public String hexDump();
    
    public byte[] getBytes(int index, int len);

    public byte[] getBytes();
}
