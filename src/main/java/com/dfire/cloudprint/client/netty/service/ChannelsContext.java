package com.dfire.cloudprint.client.netty.service;

import java.io.Closeable;
import java.util.Collection;

import io.netty.channel.Channel;

public interface ChannelsContext extends Closeable{

    void remove(Channel channel);
    
    Channel get(String key);
    
    /**
     * 
     * @param key
     * @param channel
     * @return null 为正常， channel为老值
     */
    Channel set(String key, Channel channel);
    
    
    Collection<Channel> getChannels();
}
