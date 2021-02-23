package com.dfire.cloudprint.client.netty.service;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.Channel;

public class DefaultChannelsContext implements ChannelsContext {

    private static Logger logger = LoggerFactory.getLogger(DefaultChannelsContext.class);

    private Map<String, Channel> channelMap = new ConcurrentHashMap<>();

    @Override
    public synchronized void close() throws IOException {
        channelMap.values().stream().forEach(x -> x.close());
        int timeout = 30000;
        int tt = 0;
        try{
            while (!channelMap.isEmpty() && tt < timeout) {
                    wait(100);
                    tt += 100;
            }
        } catch (InterruptedException e) {
            logger.info("close error", e);
        }
       
    }

    @Override
    public Channel get(String key) {
        Channel channel = channelMap.get(key);

        if (channel == null)
            return null;

        if (channel.isOpen())
            return channel;

        channelMap.remove(key);

        return null;
    }

    @Override
    public synchronized Channel set(String key, Channel channel) {
        
        if (key == null || channel == null) return null;
        try{
            Channel channel_ =  get(key);
            if (channel_ != null && !channel_.equals(channel)) {
                return channel_;
            }

        }finally{
            channelMap.put(key, channel);
        }
        
        return null;
    }

    @Override
    public synchronized void remove(Channel channel) {
        String key = key(channel);
        if (key != null)
            channelMap.remove(key);

    }

    private String key(Channel channel) {
        return channelMap.entrySet().stream().filter(e -> e.getValue().equals(channel)).map(e -> e.getKey()).findFirst()
                .orElse(null);
    }

    @Override
    public Collection<Channel> getChannels() {
        return channelMap.values();
    }

}
