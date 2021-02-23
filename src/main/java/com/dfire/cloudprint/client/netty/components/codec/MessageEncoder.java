package com.dfire.cloudprint.client.netty.components.codec;

import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.dfire.cloudprint.client.config.BoxInfo;
import com.dfire.cloudprint.client.netty.AttributeKeys;
import com.dfire.cloudprint.client.netty.packet.ResultPacket;
import com.dfire.cloudprint.client.netty.service.ChannelsContext;
import com.dfire.cloudprint.client.netty.service.PacketListenService;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.Attribute;

@Sharable
public class MessageEncoder extends MessageToByteEncoder<ResultPacket> {

    private static Logger logger = LoggerFactory.getLogger(MessageEncoder.class);

    @Override
    protected void encode(ChannelHandlerContext ctx, ResultPacket result, ByteBuf out) throws Exception {
        try{
            String packetName = result.getBody().getClass().getSimpleName();
            result.toByteBuffer(out);
            logger.info("send {}:{}", packetName, new String(Hex.encodeHex(getBytes(out))));
        }catch(Exception e){
            logger.warn("send error:"+result, e);
            throw e;
        }
    }

    /**
     * 服务端连接断开
     */
    @Override
    public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        ChannelsContext   context =  getContext(ctx);
        String sn = "";
        if(context != null)
            context.remove(ctx.channel());
        Attribute<BoxInfo> boxAttr = ctx.channel().attr(AttributeKeys.BOX_INFO);
        if(boxAttr.get() != null){
            PacketListenService listener = getListener(ctx);
            sn = boxAttr.get().getSn();
            if(listener != null){
                listener.downBoxInfo(sn);
            }
            boxAttr.remove();
        }
        
        //logger.info("channel[{}] box[{}] is down", ctx.channel().remoteAddress(), sn);
        super.close(ctx, promise);
    }
    
    private byte[] getBytes(ByteBuf buf){
        byte[] dst = new byte[buf.writerIndex()]; 
        buf.getBytes(0, dst);
        return dst;
    }

    private PacketListenService getListener(ChannelHandlerContext ctx) {
        return ctx.channel().attr(AttributeKeys.PACKET_LISTENER).get();
    }
       
    private ChannelsContext getContext(ChannelHandlerContext ctx) {
        return ctx.channel().attr(AttributeKeys.CHANNEL_CONTEXT).get();
    }
}
