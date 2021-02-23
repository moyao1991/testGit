package com.dfire.cloudprint.client.cpus;

public enum PageEnum {

    PAGE_58("58mm小票"),PAGE_241("三联纸");
    
    private String name;

    private PageEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    
    
}
