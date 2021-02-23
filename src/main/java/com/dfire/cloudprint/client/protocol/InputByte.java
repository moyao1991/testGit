package com.dfire.cloudprint.client.protocol;

import org.apache.commons.lang3.tuple.Pair;

public interface InputByte extends NetByte {
      
    public Pair<InputByte, InputByte> split(int pos); 
}
    
  
