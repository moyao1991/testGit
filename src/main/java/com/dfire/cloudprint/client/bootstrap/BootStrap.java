package com.dfire.cloudprint.client.bootstrap;

import java.util.Properties;

public interface BootStrap {

    void load(Properties context) throws Exception;
    
    void start() throws Exception;
    
    void close();
}
