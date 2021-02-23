package com.dfire.cloudprint.client.netty.service;

import com.dfire.cloudprint.client.config.BoxInfo;

public interface PacketListenService {

    /**
     * 握手时
     * @param boxInfo
     */
    public void shakeBoxInfo(BoxInfo boxInfo);
    
    /**
     * 上报打印机消息时
     * @param boxInfo
     */
    public void modifyPrinter(BoxInfo boxInfo);
    
    /**
     * 下线时
     * @param sn
     */
    public void downBoxInfo(String sn);
    
    
    /**
     * 心跳时
     * @param sn
     */
    public void heartBox(String sn);
    
    
    /**
     * 打印发送
     * @param taskId
     * @param throwable
     */
    public void printSendResult(boolean result, String taskId,String fileUrl, Throwable throwable);
    
    
    /**
     * 打印盒子接收成功
     * 暂时没用
     * @param taskId
     */
    public void printBoxAccept(String taskId);
    
    /**
     * 打印结果
     * @param result
     * @param taskId
     * @param throwable
     */
    public void printResult(boolean result, String taskId);

  

    
}
