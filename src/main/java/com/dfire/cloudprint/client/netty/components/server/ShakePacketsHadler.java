package com.dfire.cloudprint.client.netty.components.server;

import com.dfire.cloudprint.client.config.BoxInfo;
import com.dfire.cloudprint.client.netty.components.AbsPacketHandler;
import com.dfire.cloudprint.client.netty.packet.AcceptPacket;
import com.dfire.cloudprint.client.netty.packet.PacketsFactory;
import com.dfire.cloudprint.client.netty.packet.ResultPacket;
import com.dfire.cloudprint.client.netty.service.PacketListenService;
import com.dfire.cloudprint.client.netty.service.PrintConfService;
import com.dfire.cloudprint.client.protocol.enums.CommandTypeEnum;
import com.dfire.cloudprint.client.protocol.req.ShakeReqbodyPacket;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;

/**
 * 握手
 * @author moyao
 * @date 2018年10月9日
 */
@Sharable
public class ShakePacketsHadler extends AbsPacketHandler{
    
    @Override
    public void read(ChannelHandlerContext ctx, AcceptPacket packet) {
        
        ShakeReqbodyPacket body = (ShakeReqbodyPacket)packet.getBody();
        boolean result = body.isSecret();
        ResultPacket shakeRes = PacketsFactory.newShakeRes(result); 
        writeAndFlush(shakeRes, ctx, result ? null : ChannelFutureListener.CLOSE);
        if(!result) return ;
        
        PrintConfService printConfService = getPrintConf(ctx);
        ResultPacket confReq = PacketsFactory.newConfReq(printConfService.getHeartbeatInterval(), printConfService.getPrinterInfoInterval());
        writeAndFlush(confReq, ctx);
        
        String boxVer = body.getVersion(); 
        String lastVer = printConfService.getlastAppVersion();
        
        if(needUpdate(boxVer, lastVer, body)){
            ResultPacket boxUpgradeReq = PacketsFactory.newBoxUpgradeReq(lastVer, printConfService.getDownUrl());
            writeAndFlush(boxUpgradeReq, ctx);
        }
    }
    
  

    private boolean needUpdate(String boxVer, String lastVer, ShakeReqbodyPacket body) {

        String regex = "[0-9]+\\.[0-9]+\\.[0-9]+";
        if (!boxVer.matches(regex)) {
            return true;
        }

        String[] boxVerList = boxVer.split("\\.");
        String[] lastVerList = lastVer.split("\\.");
        // 长度不同，升级
        if (boxVerList.length != lastVerList.length) {
            return true;
        }
        
        // 比较版本号
        for (int i = 0; i < lastVerList.length; i++) {
            int boxInt = Integer.parseInt(boxVerList[i]);
            int lastVerInt = Integer.parseInt(lastVerList[i]);
            if (boxInt < lastVerInt) {
                return true;
            } else if (boxInt > lastVerInt) {
                logger.error("The box sn={} has a bigger appVersion {}! And the lastAppVersion is {}", body.getSn(), boxVer, lastVer);
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean notity(AcceptPacket packet, ChannelHandlerContext ctx) {
        ShakeReqbodyPacket body = (ShakeReqbodyPacket)packet.getBody();
        BoxInfo boxInfo =  new BoxInfo(body.getSn(), body.getSysVersion(), body.getBoxModel(), body.getVersion());
        setBoxInfo(ctx, boxInfo);
        PacketListenService listener = getListener(ctx);
        if(listener != null){
            listener.shakeBoxInfo(boxInfo);
        }
        return true;
    }

    @Override
    public boolean filter(CommandTypeEnum comand) {
        return CommandTypeEnum.PRINT_BOX_SHAKE_REQ != comand;
    }

}
