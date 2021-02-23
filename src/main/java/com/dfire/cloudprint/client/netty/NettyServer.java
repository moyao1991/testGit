package com.dfire.cloudprint.client.netty;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.concurrent.ThreadFactory;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.dfire.cloudprint.client.config.BoxInfo;
import com.dfire.cloudprint.client.netty.components.codec.MessageDecoder;
import com.dfire.cloudprint.client.netty.components.codec.MessageEncoder;
import com.dfire.cloudprint.client.netty.components.server.HeartPacketHadler;
import com.dfire.cloudprint.client.netty.components.server.PrintResultPacketHandler;
import com.dfire.cloudprint.client.netty.components.server.PrinterInfoPacketHadler;
import com.dfire.cloudprint.client.netty.components.server.ShakePacketsHadler;
import com.dfire.cloudprint.client.netty.packet.PacketsFactory;
import com.dfire.cloudprint.client.netty.packet.ResultPacket;
import com.dfire.cloudprint.client.netty.service.ChannelsContext;
import com.dfire.cloudprint.client.netty.service.DefaultChannelsContext;
import com.dfire.cloudprint.client.netty.service.PacketListenService;
import com.dfire.cloudprint.client.netty.service.PrintConfService;
import com.dfire.cloudprint.client.netty.service.PrintConfServiceImpl;
import com.dfire.cloudprint.client.utils.PrintThreadFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;

public class NettyServer implements Closeable{

    private static Logger logger = LoggerFactory.getLogger(NettyServer.class);

    
    private static ThreadFactory threadFactory = PrintThreadFactory.nettyThreadFactory();
    
    /**
     * 客户端连接的接收
     */
    private EventLoopGroup            bossGroup;
    
    /**
     * 客户端数据的发送接收
     */
    private EventLoopGroup            workGroup;
    
    /**
     * 业务数据处理
     */
    private EventExecutorGroup        threadPool;

    
    /**
     * netty server
     */
    private ServerBootstrap           bootstrap;
    
    /**
     * 服务通道
     */
    private Channel                serverChannel;
    
    private ChannelsContext        context;
    
    private PacketListenService    listener;
    
    
    public NettyServer() {
        this(0, 0, 10, 90);
    }
    
    public NettyServer(int bossNum , int workNum, int nThreads, int timeoutSeconds) {
        context = new DefaultChannelsContext();
        bossGroup = new NioEventLoopGroup(bossNum, threadFactory);
        workGroup = new NioEventLoopGroup(workNum, threadFactory);
        threadPool = new  DefaultEventExecutorGroup(nThreads, threadFactory);
        bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workGroup)
            .channel(NioServerSocketChannel.class)
            .option(ChannelOption.TCP_NODELAY, true)//设置为非延迟发送，为true则不组装成大包发送，收到东西马上发出
            .option(ChannelOption.SO_KEEPALIVE, false)
            .childAttr(AttributeKeys.PRINT_CONF, new PrintConfServiceImpl())
            .childAttr(AttributeKeys.CHANNEL_CONTEXT, context)
            .childHandler(new ServerInitializer(this, timeoutSeconds));
        
    }
    
    
    public NettyServer printConf(PrintConfService printConf){
        Validate.notNull(printConf);
        bootstrap.childAttr(AttributeKeys.PRINT_CONF, printConf);
        return this;
    }
    
    public NettyServer context(ChannelsContext context){
        Validate.notNull(context);
        this.context = context;
        bootstrap.childAttr(AttributeKeys.CHANNEL_CONTEXT, context);
        return this;
    }
    
    public NettyServer packetListen(PacketListenService listener){
        Validate.notNull(listener);
        this.listener = listener;
        bootstrap.childAttr(AttributeKeys.PACKET_LISTENER, listener);
        return this;
    }

    public void bind(int inetPort) throws InterruptedException {
        serverChannel = bootstrap.bind(inetPort).sync().channel();
    }
    
    public boolean sendUpgradeReq(String boxSn, String url){
        Channel channel = context.get(boxSn);
        if(channel == null) return false;
        if(!channel.isOpen()){
            channel.close();
            return false;
        }
        BoxInfo boxInfo = channel.attr(AttributeKeys.BOX_INFO).get();
        if(boxInfo == null){
            logger.error("boxInfo is null");
            return false;
        }
        
        String lastVer = boxInfo.getVersion()+"1";
        ResultPacket packet =  PacketsFactory.newBoxUpgradeReq(lastVer, url);
        channel.writeAndFlush(packet);
        return true;
    }
    
    
    public boolean sendPrintCommand(String boxSn, String printerMacOrSn, String fileUrl, String taskId){
        return sendPrintCommand(boxSn, printerMacOrSn, fileUrl.getBytes(), taskId, new ChannelFutureListener(){
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                listener.printSendResult(future.isSuccess(), taskId, fileUrl , future.cause());
            }
        });
    }
    
    public boolean sendPrintCommand(String boxSn, String printerMacOrSn, File file, String taskId){
        try (InputStream is = new FileInputStream(file);) {
            byte[] content = IOUtils.toByteArray(is);
            return sendPrintCommand(boxSn, printerMacOrSn, content, taskId, new ChannelFutureListener(){
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    listener.printSendResult(future.isSuccess(), taskId, file.getPath() , future.cause());
                }
            });
        }  catch (Exception e) {
            logger.error("请求命令创建出错", e);
            return false;
        }
    }
    
    private boolean sendPrintCommand(String boxSn, String printerMacOrSn, byte[] content, String taskId, ChannelFutureListener channelFutureListener){
        Channel channel = context.get(boxSn);
        if(channel == null) return false;
        if(!channel.isOpen()){
            channel.close();
            return false;
        }
        BoxInfo boxInfo = channel.attr(AttributeKeys.BOX_INFO).get();
        if(boxInfo == null){
            logger.error("boxInfo is null");
            return false;
        }
        
        long count = Arrays.asList(boxInfo.getPrinterInfo()).stream()
                .filter(x->StringUtils.equals(x.getSn(), printerMacOrSn) || StringUtils.equals(x.getMac(), printerMacOrSn))
                .count();
        
        if(count == 0)
            return false;

        ResultPacket packet = PacketsFactory.newPrintReq(printerMacOrSn, taskId, content);
        
        ChannelFuture f = channel.writeAndFlush(packet);
        if(this.listener == null) return true;
        f.addListener(channelFutureListener);        
        return true;      
    }
    
    
    
    
    @Override
    public void close() throws IOException {
        context.close();
        if( serverChannel != null && serverChannel.isOpen()){
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
            threadPool.shutdownGracefully();
            serverChannel.close();
        }        
    }
    
    private static class ServerInitializer extends  ChannelInitializer<SocketChannel>{
        
        private NettyServer server;
        private int timeoutSeconds;
        private MessageEncoder messageEncoder;
        private ShakePacketsHadler shakePacketsHadler;
        private PrinterInfoPacketHadler printerInfoPacketHadler;
        private HeartPacketHadler heartPacketHadler;
        private PrintResultPacketHandler printResultPacketHandler;
        
        public ServerInitializer(NettyServer server, int timeoutSeconds) {
            this.server = server;
            this.timeoutSeconds = timeoutSeconds;
            this.messageEncoder = new MessageEncoder();
            this.shakePacketsHadler = new ShakePacketsHadler();
            this.printerInfoPacketHadler = new PrinterInfoPacketHadler();
            this.heartPacketHadler = new HeartPacketHadler();
            this.printResultPacketHandler = new PrintResultPacketHandler();
        }

        @Override
        protected void initChannel(SocketChannel ch) throws Exception {
            ChannelPipeline pipeline = ch.pipeline();
            pipeline.addLast(new ReadTimeoutHandler(timeoutSeconds));
            pipeline.addLast(new MessageDecoder());
            pipeline.addLast(messageEncoder);
            pipeline.addLast(server.threadPool, shakePacketsHadler);
            pipeline.addLast(server.threadPool, printerInfoPacketHadler);
            pipeline.addLast(server.threadPool, heartPacketHadler);
            pipeline.addLast(server.threadPool, printResultPacketHandler);
        }
        
    }

}
