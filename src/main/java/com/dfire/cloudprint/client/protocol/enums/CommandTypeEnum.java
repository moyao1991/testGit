package com.dfire.cloudprint.client.protocol.enums;
import com.dfire.cloudprint.client.protocol.AbsPacket;
import com.dfire.cloudprint.client.protocol.InputByte;
import com.dfire.cloudprint.client.protocol.notify.DisconnectPacket;
import com.dfire.cloudprint.client.protocol.notify.HeartBodyPacket;
import com.dfire.cloudprint.client.protocol.notify.PrintResultPacket;
import com.dfire.cloudprint.client.protocol.notify.PrinterInfoPacket;
import com.dfire.cloudprint.client.protocol.req.BoxConfUpdateReqPacket;
import com.dfire.cloudprint.client.protocol.req.BoxConfUpdateResPacket;
import com.dfire.cloudprint.client.protocol.req.BoxUpgradeReqPacket;
import com.dfire.cloudprint.client.protocol.req.BoxUpgradeResPacket;
import com.dfire.cloudprint.client.protocol.req.PrintReqPacket;
import com.dfire.cloudprint.client.protocol.req.PrintResPacket;
import com.dfire.cloudprint.client.protocol.req.ShakeReqbodyPacket;
import com.dfire.cloudprint.client.protocol.req.ShakeResbodyPacket;

public enum CommandTypeEnum {

    PRINT_BOX_SHAKE_REQ((short)100, "握手", ShakeReqbodyPacket.class),
    PRINT_BOX_SHAKE_RESP((short)101, "响应握手", ShakeResbodyPacket.class),
    HEART_REQ((short)102, "心跳", HeartBodyPacket.class),
    PRINTER_INFO_REQ((short)103, "上报打印机信息", PrinterInfoPacket.class),
    DO_PRINT_REQ((short)104, "发起打印请求", PrintReqPacket.class),
    DO_PRINT_RES((short)105, "响应打印请求", PrintResPacket.class),
    PRINT_RESULT_REQ((short)106, "打印结果", PrintResultPacket.class),
    UPDATE_BOX_APP_REQ((short)107, "升级盒子应用版本", BoxUpgradeReqPacket.class),
    UPDATE_BOX_APP_RES((short)108, "响应盒子升级", BoxUpgradeResPacket.class),
    DO_DISCONNECT_REQ((short)109, "断开连接请求", DisconnectPacket.class),
    UPDATE_BOX_CONF_REQ((short)110, "更新盒子配置请求", BoxConfUpdateReqPacket.class),
    UPDATE_BOX_CONF_RES((short)111, "响应更新盒子配置", BoxConfUpdateResPacket.class);

    private short type;
    
    private String name;
    
    private Class<? extends AbsPacket> bodyClz;

    private CommandTypeEnum(short type, String name){
        this(type, name, HeartBodyPacket.class);
    }
    
    private CommandTypeEnum(short type, String name, Class<? extends AbsPacket> clz){
        this.type = type;
        this.name = name;
        this.bodyClz = clz;
    }
    
    public short getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public AbsPacket bodyClz(InputByte input) throws Exception{
        return this.bodyClz.getConstructor(InputByte.class).newInstance(input);

    }
    
    public static CommandTypeEnum valueOf(Class<? extends AbsPacket> clz) {
        for (CommandTypeEnum dm : CommandTypeEnum.values()) {
            if (clz == dm.bodyClz) {
                return dm;
            }
        }
        return null;
    }
    
    public static CommandTypeEnum valueOf(short type) {
        for (CommandTypeEnum dm : CommandTypeEnum.values()) {
            if (type == dm.getType()) {
                return dm;
            }
        }
        return null;
    }
}
