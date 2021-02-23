package com.dfire.cloudprint.client.config;

import org.apache.commons.lang3.StringUtils;

public class PrinterInfo {

    private String brandCode;
    
    private String modelCode;
    
    private String sn;
    
    private String mac;

    

    public PrinterInfo(String brandCode, String modelCode, String sn, String mac) {
        super();
        this.brandCode = brandCode;
        this.modelCode = modelCode;
        this.sn = sn;
        this.mac = mac;
    }



    public String getBrandCode() {
        return brandCode;
    }

    public String getModelCode() {
        return modelCode;
    }

    public String getSn() {
        return sn;
    }

    public String getMac() {
        return mac;
    }
    
    public String getMacOrSn() {
        return StringUtils.isEmpty(mac) ? sn : mac;
    }


    @Override
    public String toString() {
        return "PrinterInfo [sn=" + sn + "]";
    }
}
