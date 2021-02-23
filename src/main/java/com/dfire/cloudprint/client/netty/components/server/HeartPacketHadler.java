package com.dfire.cloudprint.client.netty.components.server;

import com.dfire.cloudprint.client.config.BoxInfo;
import com.dfire.cloudprint.client.netty.components.AbsPacketHandler;
import com.dfire.cloudprint.client.netty.packet.AcceptPacket;
import com.dfire.cloudprint.client.netty.service.PacketListenService;
import com.dfire.cloudprint.client.protocol.enums.CommandTypeEnum;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelHandler.Sharable;

@Sharable
public class HeartPacketHadler extends AbsPacketHandler{

    @Override
    public boolean notity(AcceptPacket packet, ChannelHandlerContext ctx) {
      
        BoxInfo boxInfo = getBoxInfo(ctx);
        
        if(boxInfo == null) return true;        
        PacketListenService listener = getListener(ctx);
        if(listener != null){
            listener.heartBox(boxInfo.getSn());
        }
        return true;
    }

    @Override
    public boolean filter(CommandTypeEnum comand) {
        return CommandTypeEnum.HEART_REQ != comand;
    }
}
