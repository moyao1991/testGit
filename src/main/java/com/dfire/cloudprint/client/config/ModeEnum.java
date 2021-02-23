package com.dfire.cloudprint.client.config;

import org.apache.commons.lang3.EnumUtils;

import com.dfire.cloudprint.client.bootstrap.BootServer;
import com.dfire.cloudprint.client.bootstrap.BootStrap;
import com.dfire.cloudprint.client.bootstrap.BoxClient;

public enum ModeEnum {
    
    SERVER(new BootServer()),CLIENT(new BoxClient());
    
    private BootStrap strap;

    private ModeEnum(BootStrap strap) {
        this.strap = strap;
    }

    public BootStrap getStrap(){
        return strap;
    }

    public static ModeEnum getModeEnumByName(String name){
        String bigName = name.toUpperCase();
        return EnumUtils.getEnum(ModeEnum.class, bigName);
    }
}
