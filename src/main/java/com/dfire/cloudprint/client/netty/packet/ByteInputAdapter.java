package com.dfire.cloudprint.client.netty.packet;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import com.dfire.cloudprint.client.protocol.InputByte;

import io.netty.buffer.ByteBuf;

public class ByteInputAdapter implements InputByte{

    private int base = 0;
    
    private int capacity = 0;

    private ByteBuf byteBuf;

    public ByteInputAdapter(ByteBuf byteBuf) {
       this(byteBuf, 0, byteBuf.writerIndex());
    }
    

    private ByteInputAdapter(ByteBuf byteBuf , int base , int capacity) {
        this.byteBuf = byteBuf;
        this.base = base;
        this.capacity = capacity;
    }

    @Override
    public int capacity() {
        return capacity;
    }

    @Override
    public Pair<InputByte, InputByte> split(int pos) {
        Validate.isTrue(pos <= capacity() && pos >= 0, "pos between 0,%d ", capacity());
        InputByte left = new ByteInputAdapter(byteBuf, base + 0, pos);
        InputByte right = new ByteInputAdapter(byteBuf,base + pos, capacity() - pos);
        return new ImmutablePair<InputByte, InputByte>(left, right);
    }

    @Override
    public byte getByte(int index) {
        Validate.isTrue(index < capacity(), "pos must less than %d", capacity());
        return byteBuf.getByte(base + index);
    }

    @Override
    public short getShort(int index) {
        Validate.isTrue(index < capacity(), "pos must less than %d", capacity());
        return byteBuf.getShort(base + index);
    }


    @Override
    public int getInt(int index) {
        Validate.isTrue(index < capacity(), "pos must less than %d", capacity());
        return byteBuf.getInt(base + index);
    }
    
    @Override
    public byte[] getBytes(int index, int len) {
        byte[] dst = new byte[len];
        byteBuf.getBytes(base+ index, dst);
        return dst;
    }

    @Override
    public String hexDump() {
        byte[] dst = new byte[capacity];
        byteBuf.getBytes(base, dst);        
        return Hex.encodeHexString(dst);      
    }


    @Override
    public byte[] getBytes() {
        byte[] dst = new byte[capacity];
        byteBuf.getBytes(base, dst);        
        return dst;
    }


  

   


   
}
