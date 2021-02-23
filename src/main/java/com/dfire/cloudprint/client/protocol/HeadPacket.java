package com.dfire.cloudprint.client.protocol;

import org.apache.commons.lang3.Validate;

import com.dfire.cloudprint.client.protocol.enums.BoxTypeEnum;
import com.dfire.cloudprint.client.protocol.enums.CommandTypeEnum;
import com.dfire.cloudprint.client.protocol.enums.EncryptTypeEnum;

public class HeadPacket extends AbsPacket {
    
    public final static int BODY_LENGTH = 20;
    
    public final static int FLAG_INDEX = 0;
    public  final static int PACKET_LENGTH_INDEX = 2;
    private final static int BOX_TYPE_INDEX = 4;
    private final static int VERSION_INDEX = 6;
    public final static int COMMAND_INDEX = 8;
    private final static int ENCRYPT_INDEX = 10;
    private final static int PAD_INDEX = 12;
    public final static int CRT_INDEX = 16;


    private boolean fromBuidler = false;
    
    public HeadPacket(InputByte input) {
        this(input, false);
    }
    
    private HeadPacket(InputByte input , boolean fromBuidler) {
        super(input);
        this.fromBuidler = fromBuidler;
    }

    /**
     * 为true crt没设值
     * @return
     */
    public boolean isFromBuidler(){
        return fromBuidler;
    }
    
    /**
     * 协议标志字段，固定值：0x5aa5
     */
    public short getFlag() {
        return getShort(FLAG_INDEX);
    }
    
    /**
     * 数据包的长度
     */
    public short getPacketLength() {
        return getShort(PACKET_LENGTH_INDEX);
    }
    
    /**
     * 设备类型
     */
    public BoxTypeEnum getBoxType(){
        short boxType = getShort(BOX_TYPE_INDEX);
        return  BoxTypeEnum.valueOf(boxType);
    }
    
    /**
     * 协议版本号,1
     */
    public short getVersion(){
        return getShort(VERSION_INDEX);
    }
    
    /**
     * 命令
     */
    public CommandTypeEnum getCommand(){
        short command = getShort(COMMAND_INDEX);
        return CommandTypeEnum.valueOf(command);
    }
    
    /**
     * 数据加密方式，0: 不加密；其它可扩展
     */
    public EncryptTypeEnum getEncryptType(){
        short encrypt = getShort(ENCRYPT_INDEX);
        return EncryptTypeEnum.valueOf(encrypt);
    }
    
    /**
     * 保留字段
     */
    public int getPad(){
        return getInt(PAD_INDEX);
    }
    
    /**
     * 较验位
     */
    public int getCrt(){
        return getInt(CRT_INDEX);
    }

    @Override
    public int getLength() {
        return BODY_LENGTH;
    }
    
    public static class Builder extends AbsPacket.Buidler{

        private static short DEFAULT_FLAG = 0x5aa5;

        public Builder(OutputByte output) {
            super(output);
            putShort(FLAG_INDEX, DEFAULT_FLAG);
            putShort(BOX_TYPE_INDEX,  BoxTypeEnum.PRINT_BOX.getType());
            putShort(VERSION_INDEX, (short)1);
            putShort(ENCRYPT_INDEX, EncryptTypeEnum.UN_ENCRYPT.getType());
            putInt(PAD_INDEX, 0);
        }

        public Builder command(CommandTypeEnum command){
              Validate.notNull(command, "command");
              putShort(COMMAND_INDEX, command.getType());
              return this;
        }
        
        public Builder packetLength(short packetLength){
            putShort(PACKET_LENGTH_INDEX, packetLength);
            return this;
        }
        @Override
        public HeadPacket build() {
            return new HeadPacket(output.toInputByte(BODY_LENGTH), true);
        }    
    }
        
}
