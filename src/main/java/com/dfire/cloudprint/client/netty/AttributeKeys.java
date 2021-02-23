package com.dfire.cloudprint.client.netty;

import com.dfire.cloudprint.client.config.BoxInfo;
import com.dfire.cloudprint.client.netty.service.ChannelsContext;
import com.dfire.cloudprint.client.netty.service.PacketListenService;
import com.dfire.cloudprint.client.netty.service.PrintConfService;

import io.netty.util.AttributeKey;

public class AttributeKeys {

    public static AttributeKey<BoxInfo> BOX_INFO =  AttributeKey.valueOf("box_info");
    
    public static AttributeKey<PacketListenService> PACKET_LISTENER =  AttributeKey.valueOf("packet_listen");

    public static AttributeKey<PrintConfService> PRINT_CONF =  AttributeKey.valueOf("print_conf");

    public static AttributeKey<ChannelsContext> CHANNEL_CONTEXT =  AttributeKey.valueOf("channel_context");

}
