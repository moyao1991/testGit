package com.dfire.cloudprint.client.netty.components.client;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.RandomUtils;

import com.dfire.cloudprint.client.config.BoxInfo;
import com.dfire.cloudprint.client.netty.components.AbsPacketHandler;
import com.dfire.cloudprint.client.netty.components.PacketHandler;
import com.dfire.cloudprint.client.netty.packet.AcceptPacket;
import com.dfire.cloudprint.client.netty.packet.PacketsFactory;
import com.dfire.cloudprint.client.netty.packet.ResultPacket;
import com.dfire.cloudprint.client.protocol.enums.CommandTypeEnum;
import com.dfire.cloudprint.client.protocol.req.BoxConfUpdateReqPacket;
import io.netty.channel.ChannelHandlerContext;

public class HeartChannelHandler extends AbsPacketHandler implements PacketHandler {

    private short heartInterval = 30;
    
    private short printerInterval = 20;

    private volatile ScheduledFuture<?> heartTask;

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        destroy();
        super.channelInactive(ctx);
    }
    
    private void destroy() {
        if (heartTask != null) {
            heartTask.cancel(false);
            heartTask = null;
        }
    }
    
    @Override
    public void read(ChannelHandlerContext ctx, AcceptPacket packet) {
        BoxConfUpdateReqPacket body = (BoxConfUpdateReqPacket)packet.getBody();
        heartInterval = body.getHeartbeatInterval();
        printerInterval = body.getPrinterInfoInterval();
        
        if(heartTask == null){
            heartTask = ctx.executor().schedule(new HeartTask(ctx), heartInterval, TimeUnit.SECONDS);
        }
    }

    @Override
    public boolean filter(CommandTypeEnum comand) {
        return CommandTypeEnum.UPDATE_BOX_CONF_REQ != comand;
    }

    
    private class HeartTask implements Runnable{

        private ChannelHandlerContext ctx = null;
        
        public HeartTask(ChannelHandlerContext ctx){
            this.ctx = ctx;
        }
        
        @Override
        public void run() {
            if (!ctx.channel().isOpen()) {
                return;
            }
            
            ResultPacket  heartNotify = PacketsFactory.newHeartNotify();
            writeAndFlush(heartNotify, ctx);
            
            int rand = RandomUtils.nextInt(0,  printerInterval/5 < 5 ? 5 : printerInterval/5);
            if(rand == 3){
                BoxInfo boxInfo = getBoxInfo(ctx);
                ResultPacket printerInfoNotify = PacketsFactory.newPrinterInfoNotify(boxInfo.getPairPrinter());
                writeAndFlush(printerInfoNotify, ctx);
            }
            
            heartTask = ctx.executor().schedule(this, heartInterval, TimeUnit.SECONDS);
        }
        
    }
    

    
}
