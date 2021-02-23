package com.dfire.cloudprint.client.netty.service;

public interface PrintConfService {

    public short getHeartbeatInterval();
    
    public short getPrinterInfoInterval();
    
    public String getlastAppVersion();
    
    public String getDownUrl();

}
