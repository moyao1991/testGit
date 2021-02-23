package com.dfire.cloudprint.client.config;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class BoxInfo {
    
    public static final String DEFUALT_MODEL = "Model_Java001";
    
    public static final String DEFUALT_SYS_VERSION = "4.4.2";

    public static final String DEFUALT_VERSION = "1.1.7";

    private String sn;
    
    private String sysVersion;
    
    private String model;
    
    private String version;
    
    private List<PrinterInfo> printers;

    public BoxInfo(String sn, String sysVersion, String model, String version) {
        this.sn = sn;
        this.sysVersion = sysVersion;
        this.model = model;
        this.version = version;
        this.printers = new ArrayList<>();;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getSysVersion() {
        return sysVersion;
    }

    public void setSysVersion(String sysVersion) {
        this.sysVersion = sysVersion;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    
    public void addPrinter(PrinterInfo printer){
        printers.add(printer);
    }
    
    public void resetPrinter(List<PrinterInfo> ps){
        printers.clear();
        printers.addAll(ps);
    }
    
    public PrinterInfo[] getPrinterInfo(){
        PrinterInfo[] a = new PrinterInfo[printers.size()];
        return printers.toArray(a);
    }

    
    public Pair<PrinterInfo, PrinterInfo> getPairPrinter(){
        if(printers.isEmpty()) return null;
        if(printers.size() == 1) return new MutablePair<>(printers.get(0), null);
        return new MutablePair<>(printers.get(0), printers.get(1));
        
    }
    
    @Override
    public String toString() {
        return "BoxInfo [sn=" + sn +"]";
    }
}
