package com.dfire.cloudprint.client.netty.service;

import com.dfire.cloudprint.client.netty.enums.PrintBoxConfEnum;

public class PrintConfServiceImpl implements PrintConfService {

    @Override
    public short getHeartbeatInterval() {
        return PrintBoxConfEnum.HEARTBEAT_INTERVAL.getShortValue();
    }

    @Override
    public short getPrinterInfoInterval() {
        return PrintBoxConfEnum.PRINTER_INFO_INTERVAL.getShortValue();
    }

    @Override
    public String getlastAppVersion() {
        return PrintBoxConfEnum.LAST_APP_VERSION.getValue();
    }

    @Override
    public String getDownUrl() {
        return PrintBoxConfEnum.LAST_APP_VERSION.getField();
    }

}
