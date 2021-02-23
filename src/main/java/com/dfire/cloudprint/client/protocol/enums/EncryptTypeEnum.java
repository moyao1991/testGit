package com.dfire.cloudprint.client.protocol.enums;

public enum EncryptTypeEnum {

    UN_ENCRYPT((short)0, "不加密");
    
    private short type;
    private String name;

    private  EncryptTypeEnum(short type, String name){
        this.type = (short) type;
        this.name = name;
    }
    
    public short getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public static EncryptTypeEnum valueOf(short type) {
        for (EncryptTypeEnum dm : EncryptTypeEnum.values()) {
            if (type == dm.getType()) {
                return dm;
            }
        }
        return null;
    }
}
