package com.dfire.cloudprint.client.netty.enums;
public enum PrintBoxConfEnum {
    
    /**
     * 【打印服务器】心跳时间间隔(秒)
     */
    HEARTBEAT_INTERVAL("HEARTBEAT_INTERVAL","30"),
   
    /**
     * 【打印服务器】上报【打印机】信息时间间隔(秒)
     */
    PRINTER_INFO_INTERVAL("PRINTER_INFO_INTERVAL","20"),
    
    /**
     * 【打印服务器】最新应用版本号
     */
    LAST_APP_VERSION("LAST_APP_VERSION", "1.1.7", "http://10.1.5.112/nginx/static-cloudprint/box/app/print-server-1.1.7");

    private String code;
    private String value;
    private String field;

    PrintBoxConfEnum(String code, String value) {
        this(code, value, "");
    }

    
    PrintBoxConfEnum(String code,String value, String field) {
        this.code = code;
        this.value = value;
        this.field = field;
    }

    public String getCode() {
        return code;
    }
    
    public String getField() {
        return field;
    }
    
    public short getShortValue(){
        return Short.parseShort(value);
    }

    public String getValue(){
        return value;
    }
}
