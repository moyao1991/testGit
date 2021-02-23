package com.dfire.cloudprint.client.netty.components.server;

import com.dfire.cloudprint.client.netty.components.AbsPacketHandler;
import com.dfire.cloudprint.client.netty.packet.AcceptPacket;
import com.dfire.cloudprint.client.netty.service.PacketListenService;
import com.dfire.cloudprint.client.protocol.enums.CommandTypeEnum;
import com.dfire.cloudprint.client.protocol.notify.PrintResultPacket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelHandler.Sharable;

@Sharable
public class PrintResultPacketHandler extends AbsPacketHandler{


    @Override
    public boolean notity(AcceptPacket packet, ChannelHandlerContext ctx) {
        PrintResultPacket body = (PrintResultPacket)packet.getBody();
        PacketListenService listener = getListener(ctx);
        if(listener != null){
            listener.printResult(body.getResult(), body.getPrintRecordId());
        }
        return true;
    }

    @Override
    public boolean filter(CommandTypeEnum comand) {
        return CommandTypeEnum.PRINT_RESULT_REQ != comand;
    }
}
