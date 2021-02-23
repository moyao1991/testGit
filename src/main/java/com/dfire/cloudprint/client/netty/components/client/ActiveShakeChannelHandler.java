package com.dfire.cloudprint.client.netty.components.client;

import com.dfire.cloudprint.client.config.BoxInfo;
import com.dfire.cloudprint.client.netty.components.AbsPacketHandler;
import com.dfire.cloudprint.client.netty.components.PacketHandler;
import com.dfire.cloudprint.client.netty.packet.AcceptPacket;
import com.dfire.cloudprint.client.netty.packet.PacketsFactory;
import com.dfire.cloudprint.client.netty.packet.ResultPacket;
import com.dfire.cloudprint.client.protocol.enums.CommandTypeEnum;
import com.dfire.cloudprint.client.protocol.req.ShakeResbodyPacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelHandler.Sharable;

@Sharable
public class ActiveShakeChannelHandler extends AbsPacketHandler implements PacketHandler{

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        BoxInfo boxInfo = getBoxInfo(ctx);
        ResultPacket shakeReq = PacketsFactory.newShakeReq(boxInfo);
        writeAndFlush(shakeReq, ctx);
    }
    
    @Override
    public void read(ChannelHandlerContext ctx, AcceptPacket packet) {
        BoxInfo boxInfo = getBoxInfo(ctx);
        ShakeResbodyPacket body = (ShakeResbodyPacket)packet.getBody();
        if(!body.getResult()){
            logger.info("box[{}] shake failed", boxInfo.getSn());
            ctx.channel().close();
            return;
        }
        
        ResultPacket printerInfoNotify = PacketsFactory.newPrinterInfoNotify(boxInfo.getPairPrinter());
        writeAndFlush(printerInfoNotify, ctx);
    }

    @Override
    public boolean filter(CommandTypeEnum comand) {
        return CommandTypeEnum.PRINT_BOX_SHAKE_RESP != comand;
    }
   
}
