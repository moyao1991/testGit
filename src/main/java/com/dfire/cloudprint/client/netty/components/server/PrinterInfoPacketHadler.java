package com.dfire.cloudprint.client.netty.components.server;

import java.util.ArrayList;
import java.util.List;

import com.dfire.cloudprint.client.config.BoxInfo;
import com.dfire.cloudprint.client.config.PrinterInfo;
import com.dfire.cloudprint.client.netty.components.AbsPacketHandler;
import com.dfire.cloudprint.client.netty.packet.AcceptPacket;
import com.dfire.cloudprint.client.netty.service.PacketListenService;
import com.dfire.cloudprint.client.protocol.enums.CommandTypeEnum;
import com.dfire.cloudprint.client.protocol.notify.PrinterInfoPacket;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;

/**
 * 上报打印机信息
 * @author moyao
 * @date 2018年10月15日
 */
@Sharable
public class PrinterInfoPacketHadler extends AbsPacketHandler {

    @Override
    public boolean notity(AcceptPacket packet, ChannelHandlerContext ctx) {
        PrinterInfoPacket body = (PrinterInfoPacket)packet.getBody();
        int count = body.getPrintCount();
        List<PrinterInfo> priners = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String modelCode = body.getModelCode(i);
            String brandCode= body.getBrandCode(i);
            String mac = body.getMac(i);
            String sn = body.getSn(i);
            PrinterInfo priner = new PrinterInfo(brandCode, modelCode, sn, mac);
            priners.add(priner);
        }
        BoxInfo boxInfo = getBoxInfo(ctx);
        boxInfo.resetPrinter(priners);
        
        PacketListenService listener = getListener(ctx);
        if(listener != null){
            listener.modifyPrinter(boxInfo);
        }
        return true;
    }

    @Override
    public boolean filter(CommandTypeEnum comand) {
        return CommandTypeEnum.PRINTER_INFO_REQ != comand;
    }
    
}
