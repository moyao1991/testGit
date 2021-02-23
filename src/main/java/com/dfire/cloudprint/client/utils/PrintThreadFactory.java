package com.dfire.cloudprint.client.utils;

import java.util.concurrent.ThreadFactory;
import org.apache.commons.lang3.concurrent.BasicThreadFactory.Builder;

public class PrintThreadFactory {

   public static ThreadFactory cupsThreadFactory(){
       return new Builder().namingPattern("pool[cups]-thread-%d").build();
   }
      
   public static ThreadFactory nettyThreadFactory(){
       return new Builder().namingPattern("pool[netty]-thread-%d").build();
   }
}
