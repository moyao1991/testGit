package com.dfire.cloudprint.client.netty.components.client;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import com.dfire.cloudprint.client.Constants;
import com.dfire.cloudprint.client.netty.components.AbsPacketHandler;
import com.dfire.cloudprint.client.netty.components.PacketHandler;
import com.dfire.cloudprint.client.netty.packet.AcceptPacket;
import com.dfire.cloudprint.client.netty.packet.PacketsFactory;
import com.dfire.cloudprint.client.netty.packet.ResultPacket;
import com.dfire.cloudprint.client.protocol.enums.CommandTypeEnum;
import com.dfire.cloudprint.client.protocol.req.PrintReqPacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelHandler.Sharable;

@Sharable
public class PrintReqPacketHandler extends AbsPacketHandler implements PacketHandler{

    @Override
    public void read(ChannelHandlerContext ctx, AcceptPacket packet) {

        ResultPacket printRes = PacketsFactory.newPrintRes();
        writeAndFlush(printRes, ctx);
        
        PrintReqPacket body = (PrintReqPacket)packet.getBody();        
        String sn = body.getPrinterSnOrMac();
        boolean isPdf = sn.startsWith("UPDF");
        byte[] bs = body.getContent();

        File destFile = new File(Constants.TMP_PATH 
                            + File.separator
                            + body.getPrintRecordId() 
                            + (isPdf ? Constants.PDF_SUFFIX : Constants.TMP_SUFFIX ));
        
        boolean success = false;
        try {
            FileUtils.  writeByteArrayToFile(destFile, bs);
            success = true;
        } catch (IOException e) {
           logger.error("打印{}异常", body.getPrintRecordId(), e);
        }

        ResultPacket printResult = PacketsFactory.newPrintResult(body.getPrintRecordId(), success);
        writeAndFlush(printResult, ctx);
       
    }

    @Override
    public boolean filter(CommandTypeEnum comand) {
        return CommandTypeEnum.DO_PRINT_REQ != comand;
    }
    
}
