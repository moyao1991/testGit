package com.dfire.cloudprint.client.protocol.req;

import org.apache.commons.codec.digest.DigestUtils;
import com.dfire.cloudprint.client.config.BoxInfo;
import com.dfire.cloudprint.client.protocol.AbsPacket;
import com.dfire.cloudprint.client.protocol.InputByte;
import com.dfire.cloudprint.client.protocol.OutputByte;

public class ShakeReqbodyPacket extends AbsPacket {

    private final static String SECRET = "06dbe5be4da6468eae8ac8a0f00cee31";
    public  final static int BODY_LENGTH = 142;
    
    private final static int SN_INDEX = 0;
    private final static int VERSION_INDEX = 32;
    private final static int TIMESTAMP_INDEX = 42;
    private final static int SIGN_INDEX = 46;
    private final static int BOX_MODEL_INDEX = 78;
    private final static int SYS_VERSION_INDEX = 110;

    
    public ShakeReqbodyPacket(InputByte input) {
        super(input);
    }

    /**
     * 打印服务器序列号
     */
    public String getSn() {
        return getUtf8String(SN_INDEX, 32);
    }

    /**
     * 主程序版本号，格式1.0.0
     */
    public String getVersion() {
        return getUtf8String(VERSION_INDEX, 10);
    }

    /**
     * 长整形时间戳
     */
    public int getTimestamp() {
        return getInt(TIMESTAMP_INDEX);
    }

    /**
     * 签名,生成方式： MD5(MD5(密钥+设备序列号) +时间戳)，这里+号表示字符串拼接
     */
    public String getSign() {
        return getUtf8String(SIGN_INDEX, 32);
    }

    /**
     * 设备硬件型号，类似：epson lq-630k ；ZhongYing NX-635KII 
     */
    public String getBoxModel() {
        return getUtf8String(BOX_MODEL_INDEX, 32);
    }

    /**
     * 安卓系统版本号,格式：4.xx.xx
     */
    public String getSysVersion() {
        return getUtf8String(SYS_VERSION_INDEX, 32);
    }
    
    /**
     * 是否合法
     * @return
     */
    public boolean isSecret(){
        String gSign = generateSign(getSn(), getTimestamp());
        return getSign().equalsIgnoreCase(gSign);
    }

    @Override
    public int getLength() {
        return BODY_LENGTH;
    }
    
    private static String generateSign(String sn, int time){
        return  DigestUtils.md5Hex(DigestUtils.md5Hex(SECRET + sn) + time);
    }
    
    public static class Builder extends AbsPacket.Buidler{

        public Builder(OutputByte output) {
            super(output);
        }

        public Builder box(BoxInfo boxInfo){
              int time = (int) System.currentTimeMillis()  / 1000;
              putUtf8String(SN_INDEX, boxInfo.getSn(), 32);
              putUtf8String(VERSION_INDEX, boxInfo.getVersion(), 10);
              putInt(TIMESTAMP_INDEX, time);
              String sign = generateSign(boxInfo.getSn(), time);
              putUtf8String(SIGN_INDEX, sign, 32);
              putUtf8String(SYS_VERSION_INDEX, boxInfo.getSysVersion(), 32);
              return this;
        }
        
        @Override
        public ShakeReqbodyPacket build() {
            return new ShakeReqbodyPacket(output.toInputByte(BODY_LENGTH));
        }
   
    }
}
