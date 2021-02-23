package com.dfire.cloudprint.client.netty.packet;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

public class ByteAlloc {
    
   private final static short DEFAULT_CAPACITY = 256;
    
   private final static ByteBufAllocator heapAlloc = ByteBufAllocator.DEFAULT;
   
   public static ByteBuf heapAlloc(int capacity){
       return heapAlloc.buffer(capacity);
   }
   
   public static ByteBuf heapAlloc(){
       return heapAlloc(DEFAULT_CAPACITY);
   }
   
   public static void main(String[] args) {
      ByteBuf byteBuf = heapAlloc();
      System.out.println(byteBuf.writableBytes());
      System.out.println(byteBuf.readableBytes());
      System.out.println(byteBuf.capacity());

   }
}
