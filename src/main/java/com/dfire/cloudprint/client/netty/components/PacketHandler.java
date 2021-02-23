package com.dfire.cloudprint.client.netty.components;

import com.dfire.cloudprint.client.config.BoxInfo;
import com.dfire.cloudprint.client.netty.packet.AcceptPacket;
import com.dfire.cloudprint.client.netty.packet.ResultPacket;
import com.dfire.cloudprint.client.protocol.enums.CommandTypeEnum;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;

public interface PacketHandler extends ChannelInboundHandler {
    
    /**
     * 消费包体
     * @param packet
     */
    void read(ChannelHandlerContext ctx, AcceptPacket packet);
    
    /**
     * 过滤不是要的包体
     * @param comand
     * @return
     */
    boolean filter(CommandTypeEnum comand);
    
    /**
     * 仅通知不进行消费
     * @param packet
     */
    boolean notity(AcceptPacket packet, ChannelHandlerContext ctx);
    
    void writeAndFlush(ResultPacket result, ChannelHandlerContext ctx);
    
    void writeAndFlush(ResultPacket result, ChannelHandlerContext ctx, ChannelFutureListener listener);
    
    
    BoxInfo getBoxInfo(ChannelHandlerContext ctx);


}
