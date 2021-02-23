package com.dfire.cloudprint.client.protocol.enums;

public enum BoxTypeEnum {
    /**
     */
    PRINT_BOX(1, "打印盒子");

    private short type;
    private String name;

    private BoxTypeEnum(int type, String name){
        this.type = (short) type;
        this.name = name;
    }

   public short getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public static BoxTypeEnum valueOf(short type) {
        for (BoxTypeEnum dm : BoxTypeEnum.values()) {
            if (type == dm.getType()) {
                return dm;
            }
        }
        return null;
    }
}