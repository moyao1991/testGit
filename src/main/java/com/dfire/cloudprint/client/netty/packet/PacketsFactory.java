package com.dfire.cloudprint.client.netty.packet;

import org.apache.commons.lang3.tuple.Pair;
import com.dfire.cloudprint.client.config.BoxInfo;
import com.dfire.cloudprint.client.config.PrinterInfo;
import com.dfire.cloudprint.client.protocol.OutputByte;
import com.dfire.cloudprint.client.protocol.notify.HeartBodyPacket;
import com.dfire.cloudprint.client.protocol.notify.PrintResultPacket;
import com.dfire.cloudprint.client.protocol.notify.PrinterInfoPacket;
import com.dfire.cloudprint.client.protocol.req.BoxConfUpdateReqPacket;
import com.dfire.cloudprint.client.protocol.req.BoxUpgradeReqPacket;
import com.dfire.cloudprint.client.protocol.req.PrintReqPacket;
import com.dfire.cloudprint.client.protocol.req.PrintResPacket;
import com.dfire.cloudprint.client.protocol.req.ShakeReqbodyPacket;
import com.dfire.cloudprint.client.protocol.req.ShakeResbodyPacket;

public class PacketsFactory {

    /**
     * 握手请求
     * @param boxInfo
     * @return
     */
    public static ResultPacket newShakeReq(BoxInfo boxInfo) {
        OutputByte out = new ByteOutputAdapter(ShakeReqbodyPacket.BODY_LENGTH);
        ShakeReqbodyPacket body = new ShakeReqbodyPacket.Builder(out).box(boxInfo).build();
        return ResultPacket.buildResult(body);
    }

    /**
     * 握手响应
     * @param reuslt
     * @return
     */
    public static ResultPacket newShakeRes(boolean reuslt) {
        OutputByte out = new ByteOutputAdapter(ShakeResbodyPacket.BODY_LENGTH);
        ShakeResbodyPacket body = new ShakeResbodyPacket.Builder(out).success(reuslt).build();
        return ResultPacket.buildResult(body);
    }

    /**
     * 更新盒子配置请求
     * @param heartInterval
     * @param printerInterval
     * @return
     */
    public static ResultPacket newConfReq(short heartInterval, short printerInterval) {
        OutputByte outByte = new ByteOutputAdapter(BoxConfUpdateReqPacket.BODY_LENGTH);
        BoxConfUpdateReqPacket body = new BoxConfUpdateReqPacket.Builder(outByte).heartBeat(heartInterval)
                .printerInfo(printerInterval).build();
        return ResultPacket.buildResult(body);
    }

    /**
     * 盒子升级请求
     * @param lastVer
     * @param url
     * @return
     */
    public static ResultPacket newBoxUpgradeReq(String lastVer, String url) {
        OutputByte outByte = new ByteOutputAdapter();
        BoxUpgradeReqPacket body = new BoxUpgradeReqPacket.Builder(outByte).version(lastVer).url(url).build();
        return ResultPacket.buildResult(body);
    }

    /**
     * 上报盒子信息
     * @param lastVer
     * @param url
     * @return
     */
    public static ResultPacket newPrinterInfoNotify(Pair<PrinterInfo, PrinterInfo> pair) {

        PrinterInfo[] printers = pair == null || (pair.getLeft() == null && pair.getRight() == null) ? null
                : pair.getLeft() != null && pair.getRight() != null
                        ? new PrinterInfo[] { pair.getLeft(), pair.getRight() }
                        : pair.getLeft() != null ? new PrinterInfo[] { pair.getLeft() }
                                : new PrinterInfo[] { pair.getRight() };
        OutputByte outByte = new ByteOutputAdapter();
        PrinterInfoPacket body = new PrinterInfoPacket.Builder(outByte).printers(printers).build();
        return ResultPacket.buildResult(body);
    }

    /**
     * 发送心跳
     * @return
     */
    public static ResultPacket newHeartNotify() {
        OutputByte outByte = new ByteOutputAdapter(HeartBodyPacket.BODY_LENGTH);
        HeartBodyPacket body = new HeartBodyPacket.Builder(outByte).build();
        return ResultPacket.buildResult(body);
    }

    /**
     * 发送打印请求
     * @param printerMacOrSn
     * @param recordId
     * @param content
     * @return
     * @throws Exception
     */
    public static ResultPacket newPrintReq(String printerMacOrSn, String recordId, byte[] content) {
            int len = content.length;
            OutputByte outByte = new ByteOutputAdapter(PrintReqPacket.BODY_LENGTH + len);
            PrintReqPacket body = new PrintReqPacket.Builder(outByte).printerMacOrSn(printerMacOrSn).recordId(recordId)
                    .content(content).build();
            return ResultPacket.buildResult(body);
        
    }

    /**
     * 打印接收响应
     * @return
     * @throws Exception
     */
    public static ResultPacket newPrintRes(){
        OutputByte outByte = new ByteOutputAdapter(PrintResPacket.BODY_LENGTH);
        PrintResPacket body = new PrintResPacket.Builder(outByte).build();
        return ResultPacket.buildResult(body);
    }
    
    /**
     * 打印结果
     * @param recordId
     * @param success
     * @return
     */
    public static ResultPacket newPrintResult(String recordId, boolean success){
        OutputByte outByte = new ByteOutputAdapter(PrintResultPacket.BODY_LENGTH);
        PrintResultPacket body = new PrintResultPacket.Builder(outByte)
                .success(recordId, success)
                .build();
        return ResultPacket.buildResult(body);
    }
}
