package com.dfire.cloudprint.client.netty.components;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dfire.cloudprint.client.config.BoxInfo;
import com.dfire.cloudprint.client.netty.AttributeKeys;
import com.dfire.cloudprint.client.netty.components.codec.MessageDecoder;
import com.dfire.cloudprint.client.netty.packet.AcceptPacket;
import com.dfire.cloudprint.client.netty.packet.ResultPacket;
import com.dfire.cloudprint.client.netty.service.ChannelsContext;
import com.dfire.cloudprint.client.netty.service.PacketListenService;
import com.dfire.cloudprint.client.netty.service.PrintConfService;
import com.dfire.cloudprint.client.protocol.enums.CommandTypeEnum;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.Attribute;
import io.netty.util.ReferenceCountUtil;

public abstract class AbsPacketHandler extends SimpleChannelInboundHandler<AcceptPacket> implements PacketHandler{

    protected static Logger logger = LoggerFactory.getLogger(MessageDecoder.class);
    
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        boolean release = true;
        try{
            if (acceptInboundMessage(msg, ctx)) {
                AcceptPacket packet = (AcceptPacket) msg;
                channelRead0(ctx, packet);
            } else {
                release = false;
                ctx.fireChannelRead(msg);
            }
        }catch(Exception e){
            if(inExceptionCaught(e)) return;
            if (msg instanceof AcceptPacket) {
                logger.info("accept error:"+msg, e);
                return;
            }
            throw e;
        }finally {
            if (release) {
                ReferenceCountUtil.release(msg);
            }
        }
    }
    

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AcceptPacket packet) throws Exception {
        read(ctx, packet);
    }

    @Override
    public void writeAndFlush(ResultPacket result, ChannelHandlerContext ctx){
        writeAndFlush(result, ctx, null);
    }
    @Override
    public void writeAndFlush(ResultPacket result, ChannelHandlerContext ctx, ChannelFutureListener listener){
        ChannelFuture future = ctx.writeAndFlush(result);
        if(listener == null) return;
        future.addListener(listener);
    }
    

    public boolean acceptInboundMessage(Object packet, ChannelHandlerContext ctx) throws Exception {
        return super.acceptInboundMessage(packet) && 
                !filter(packet) &&
                hasBoxInfo((AcceptPacket)packet, ctx) &&
                notityPacket(packet, ctx);
    }
    
    @Override
    public void read(ChannelHandlerContext ctx, AcceptPacket packet){
    }
    
    @Override
    public  boolean filter(CommandTypeEnum comand){
        return false;
    }
     
    @Override
    public  boolean notity(AcceptPacket packet, ChannelHandlerContext ctx){
        return true;
    }

    private boolean filter(Object msg){
        AcceptPacket packet = (AcceptPacket)msg;
        return filter(packet.getHeader().getCommand());
    }
    
    private boolean notityPacket(Object msg, ChannelHandlerContext ctx){
        return notity((AcceptPacket)msg, ctx);
    }
    
    private static boolean inExceptionCaught(Throwable cause) {
        do {
            StackTraceElement[] trace = cause.getStackTrace();
            if (trace != null) {
                for (StackTraceElement t : trace) {
                    if (t == null) {
                        break;
                    }
                    if ("exceptionCaught".equals(t.getMethodName())) {
                        return true;
                    }
                }
            }

            cause = cause.getCause();
        } while (cause != null);

        return false;
    }

    private boolean hasBoxInfo(AcceptPacket packet, ChannelHandlerContext ctx){
        CommandTypeEnum type = packet.getHeader().getCommand();
        if(type == CommandTypeEnum.PRINT_BOX_SHAKE_REQ)
            return true;
        if(type == CommandTypeEnum.HEART_REQ)
            return true;
        if(getBoxInfo(ctx) != null) return true;
        logger.error("packect[{}] boxinfo is null close channel", packet);
        ctx.channel().close();
        return false;
    }
    

    public void setBoxInfo(ChannelHandlerContext ctx, BoxInfo boxInfo) {
        Attribute<BoxInfo>  attribute = ctx.channel().attr(AttributeKeys.BOX_INFO);
        BoxInfo old = attribute.getAndSet(boxInfo);
        if(old != null){
            logger.warn("old[{}] to new[{}]", old, boxInfo);
        }
        Channel oldChannel = getContext(ctx).set(boxInfo.getSn(), ctx.channel());
        if(oldChannel != null){
            Attribute<BoxInfo>  attr = oldChannel.attr(AttributeKeys.BOX_INFO);
            BoxInfo removeBox = attr.getAndSet(null);
            logger.warn("sn[{}] channel set error  boxinfo[{} {}]", boxInfo.getSn(), removeBox.getSn(), removeBox.getModel());
            oldChannel.close();
        }        
    }
    
    public PacketListenService getListener(ChannelHandlerContext ctx) {
        return ctx.channel().attr(AttributeKeys.PACKET_LISTENER).get();
    }
    
    public PrintConfService getPrintConf(ChannelHandlerContext ctx) {
        return ctx.channel().attr(AttributeKeys.PRINT_CONF).get();
    }
    
    @Override
    public BoxInfo getBoxInfo(ChannelHandlerContext ctx) {
        return ctx.channel().attr(AttributeKeys.BOX_INFO).get();
    }
    
    private ChannelsContext getContext(ChannelHandlerContext ctx) {
        return ctx.channel().attr(AttributeKeys.CHANNEL_CONTEXT).get();
    }
    
}
