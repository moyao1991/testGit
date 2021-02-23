package com.dfire.cloudprint.client.netty.components.server;

import com.dfire.cloudprint.client.netty.components.AbsPacketHandler;
import com.dfire.cloudprint.client.netty.packet.AcceptPacket;
import com.dfire.cloudprint.client.netty.service.PacketListenService;
import com.dfire.cloudprint.client.protocol.enums.CommandTypeEnum;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelHandler.Sharable;

/**
 * 发送已接收打印请求响应报文
 * 没告诉我哪个任务成功了 
 * 待改造
 * @author moyao
 * @date 2018年10月16日
 */
@Sharable
public class PrintResPacketHandler extends AbsPacketHandler{

    @Override
    public boolean notity(AcceptPacket packet, ChannelHandlerContext ctx) {
      
       // BoxInfo boxInfo = getBoxInfo(ctx);
        PacketListenService listener = getListener(ctx);
        if(listener != null){
            //listener.printBoxAcceptSuccess(taskId);
        }
        return true;
    }

    @Override
    public boolean filter(CommandTypeEnum comand) {
        return CommandTypeEnum.DO_PRINT_RES != comand;
    }
}
