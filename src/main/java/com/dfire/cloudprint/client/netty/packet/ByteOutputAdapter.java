package com.dfire.cloudprint.client.netty.packet;

import org.apache.commons.codec.binary.Hex;
import com.dfire.cloudprint.client.protocol.InputByte;
import com.dfire.cloudprint.client.protocol.OutputByte;
import io.netty.buffer.ByteBuf;

public class ByteOutputAdapter implements OutputByte {

    private ByteBuf byteBuf;

    public ByteOutputAdapter() {
        this(ByteAlloc.heapAlloc());
     }

    public ByteOutputAdapter(int capacity) {
       this(ByteAlloc.heapAlloc(capacity));
    }
    
    public ByteOutputAdapter(ByteBuf byteBuf) {
        this.byteBuf = byteBuf;
        this.byteBuf.writerIndex(byteBuf.capacity());
    }

    @Override
    public int capacity() {
        return byteBuf.capacity();
    }

    @Override
    public byte getByte(int index) {
        return byteBuf.getByte(index);
    }

    @Override
    public short getShort(int index) {
        return byteBuf.getShort(index);
    }

    @Override
    public int getInt(int index) {
        return byteBuf.getInt(index);
    }

    @Override
    public String hexDump() {
        byte[] dst = new byte[capacity()];
        byteBuf.getBytes(0, dst);        
        return Hex.encodeHexString(dst);      
    }
    
    @Override
    public byte[] getBytes(int index, int len) {
        byte[] dst = new byte[len];
        byteBuf.getBytes(index, dst);
        return dst;
    }

    @Override
    public void putByte(int index, byte value) {
        byteBuf.setByte(index, value);
    }

    @Override
    public void putShort(int index, short value) {
        byteBuf.setShort(index, value);
    }

    @Override
    public void putInt(int index, int value) {
        byteBuf.setInt(index, value);
    }

    @Override
    public InputByte toInputByte() {
        return toInputByte(byteBuf.capacity());
    }
    
    @Override
    public InputByte toInputByte(int len) {
        int capacity = len;
        ByteBuf dst = ByteAlloc.heapAlloc(capacity);
        byteBuf.getBytes(0, dst, capacity);
        dst.writerIndex(capacity);
        return new ByteInputAdapter(dst);
    }

    @Override
    public byte[] getBytes() {
        byte[] dst = new byte[capacity()];
        byteBuf.getBytes(0, dst);        
        return dst;
    }

    @Override
    public void putBytes(int index, byte[] value) {
        byteBuf.setBytes(index, value);

    }

}
