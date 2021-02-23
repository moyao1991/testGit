package com.dfire.cloudprint.client.netty.components.codec ;

import java.util.List;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.dfire.cloudprint.client.config.BoxInfo;
import com.dfire.cloudprint.client.netty.AttributeKeys;
import com.dfire.cloudprint.client.netty.packet.AcceptPacket;
import com.dfire.cloudprint.client.netty.packet.ByteAlloc;
import com.dfire.cloudprint.client.netty.service.ChannelsContext;
import com.dfire.cloudprint.client.netty.service.PacketListenService;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.Attribute;

/**
 * 解码器
 * @author moyao
 * @date 2018年9月30日
 */
public class MessageDecoder extends ByteToMessageDecoder{

    private static Logger logger = LoggerFactory.getLogger(MessageDecoder.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if(ctx.channel().remoteAddress().toString().contains("100.")){
            // ignore
            // 阿理云端口健康检查会造成大量该错误
            ctx.channel().close();
            return;
        }
        super.channelActive(ctx);
    }

    @Override
    public void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        
        int base = in.readerIndex();
        int remaining = in.writerIndex() - base;

        while( remaining >0 ) {
            
            if(remaining < AcceptPacket.HEAD_LENGTH){
                //logger.info("accept error dump:" + new String(Hex.encodeHex(getBytes(in))));
                return;
            }
            
            short  flag = in.getShort(base + AcceptPacket.FLAG_INDEX);
            
            if (flag != 0x5aa5){
                return;
            }
            
            int packectLen = in.getShort(base + AcceptPacket.PACKET_LENGTH_INDEX);

            if (in.getUnsignedShort(base + AcceptPacket.COMMAND_INDEX) == 104){
                //请求命令特殊  头长20+ 固定体长68 + 文件大小
                ////老版本104 packetlen乱搞加直接写死
                packectLen = 88 + in.getInt(base + AcceptPacket.PRINT_REQ_CONTENT_LENGTH_INDEX);
                //packectLen += in.getInt(base + AcceptPacket.PRINT_REQ_CONTENT_LENGTH_INDEX);
            }
            
            if (packectLen < AcceptPacket.HEAD_LENGTH){
                  return;
            }
            
            if (remaining < packectLen){
                //logger.info("accept error dump:" + new String(Hex.encodeHex(getBytes(in))));
                return;
            }

            ByteBuf dst = ByteAlloc.heapAlloc(packectLen);
            in.readBytes(dst);
            try{
                AcceptPacket  packet = AcceptPacket.accept(dst);  
                out.add(packet);
                String packetName = packet.getBody().getClass().getSimpleName();
                logger.info("accept {} :{}", packetName, packet);
            }catch(Exception e){
                logger.error(new String(Hex.encodeHex(getBytes(in))), e);
            }

            base = in.readerIndex();
            remaining = in.writerIndex() - base;
        }
        
    }
    
    /**
     * 客户端连接断开
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
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
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception { 
        ByteBuf byteBuf = internalBuffer();
        byte[] dst = getBytes(byteBuf);
        logger.info("channel[{}]:", ctx.channel().remoteAddress(), new String(Hex.encodeHex(dst)), cause);
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
