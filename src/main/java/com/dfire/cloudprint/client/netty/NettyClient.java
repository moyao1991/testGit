package com.dfire.cloudprint.client.netty;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dfire.cloudprint.client.config.BoxInfo;
import com.dfire.cloudprint.client.netty.components.client.ActiveShakeChannelHandler;
import com.dfire.cloudprint.client.netty.components.client.HeartChannelHandler;
import com.dfire.cloudprint.client.netty.components.client.PrintReqPacketHandler;
import com.dfire.cloudprint.client.netty.components.codec.MessageDecoder;
import com.dfire.cloudprint.client.netty.components.codec.MessageEncoder;
import com.dfire.cloudprint.client.utils.PrintThreadFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class NettyClient implements Closeable {

    private static Logger logger = LoggerFactory.getLogger(NettyServer.class);
    
    private static ThreadFactory threadFactory = PrintThreadFactory.nettyThreadFactory();
    
    /**
     * 客户端连接的接收
     */
    private EventLoopGroup            bossGroup;
    
    private boolean                  interGroup;

    /**
     * netty server
     */
    private Bootstrap                 bootstrap;
    
    private Channel                  channel;
    
    private String                   inetHost;
    
    private int                      inetPort;
    
    public NettyClient(BoxInfo boxInfo) {
        this(boxInfo, new NioEventLoopGroup(0, threadFactory));
        this.interGroup = true;
    }
    
    public NettyClient(BoxInfo boxInfo , EventLoopGroup bossGroup) {
        this.bossGroup = bossGroup;
        this.interGroup = false;
        bootstrap = new Bootstrap();
        bootstrap.group(bossGroup)
            .channel(NioSocketChannel.class)
            .attr(AttributeKeys.BOX_INFO, boxInfo)
            .option(ChannelOption.TCP_NODELAY, true)//设置为非延迟发送，为true则不组装成大包发送，收到东西马上发出
            .option(ChannelOption.SO_KEEPALIVE, false)
            .option(ChannelOption.MAX_MESSAGES_PER_READ, Integer.MAX_VALUE)
            .handler(new ClientInitializer());
        
    }
    
    public  void connect(String inetHost, int inetPort) {
                this.inetHost = inetHost;
                this.inetPort = inetPort;
                retry();
    }
    
    private  void retry() {
        ChannelFuture f =  bootstrap.connect(inetHost, inetPort);
        channel = f.channel();
        f.addListener(new ChannelFutureListener(){
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                 
                if(future.isSuccess()){
                    logger.info("connect {}:{} success", inetHost, inetPort);
                }else{
                    logger.info("connect {}:{} failed", inetHost, inetPort, future.cause());
                    future.channel().eventLoop().schedule(()->{
                        retry();
                    }, 5, TimeUnit.SECONDS);
                }
            }
       }
    );
}
    
    @Override
    public void close() throws IOException {
        if(interGroup && bossGroup != null)  bossGroup.shutdownGracefully();
        if(channel != null && channel.isOpen()) channel.close();
    }
    
    private  class ClientInitializer extends  ChannelInitializer<SocketChannel> {

        private MessageEncoder messageEncoder;
        
        private ActiveShakeChannelHandler activeShakeChannelHandler;
        
        private PrintReqPacketHandler printReqPacketHandler;

        
        public ClientInitializer() {
            this.messageEncoder = new MessageEncoder();
            this.activeShakeChannelHandler = new ActiveShakeChannelHandler(){
                @Override
                public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                    logger.info("channel {}:{} inactive", inetHost, inetPort);
                    retry();
                }
            };
            printReqPacketHandler = new PrintReqPacketHandler();
        }

        @Override
        protected void initChannel(SocketChannel ch) throws Exception {
            ChannelPipeline pipeline = ch.pipeline();
            pipeline.addLast(new MessageDecoder());
            pipeline.addLast(messageEncoder);
            pipeline.addLast(activeShakeChannelHandler);
            pipeline.addLast(printReqPacketHandler);
            pipeline.addLast(new HeartChannelHandler());
            
        }
     
    }

}
