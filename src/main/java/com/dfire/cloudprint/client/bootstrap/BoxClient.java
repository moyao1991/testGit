package com.dfire.cloudprint.client.bootstrap;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.dfire.cloudprint.client.config.BoxInfo;
import com.dfire.cloudprint.client.config.PrinterInfo;
import com.dfire.cloudprint.client.netty.NettyClient;
import com.dfire.cloudprint.client.utils.PrintThreadFactory;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

public class BoxClient implements BootStrap{
    
    private static Logger logger = LoggerFactory.getLogger(BoxClient.class);

    private static ThreadFactory threadFactory = PrintThreadFactory.nettyThreadFactory();

    private BoxInfo[] boxs = null; 
    
    private NettyClient[] clients = null;
    
    private EventLoopGroup group = null;
    
    private int inetPort = 9190;
    
    private String inetHost = "127.0.0.1";
    
    @Override
    public void load(Properties context) throws Exception {
        List<BoxInfo> boxList = new ArrayList<>();
        while(true){
            String preSuffix = "netty.box"+boxList.size();
            String sn = context.getProperty(preSuffix+".sn");
            if(StringUtils.isEmpty(sn)) break;
            String version = context.getProperty(preSuffix+".version", BoxInfo.DEFUALT_VERSION);
            String sysVersion = context.getProperty(preSuffix+".sys.version", BoxInfo.DEFUALT_SYS_VERSION);
            String model = context.getProperty(preSuffix+".model", BoxInfo.DEFUALT_MODEL);
            BoxInfo boxInfo = new BoxInfo(sn, sysVersion, model, version);
            boxInfo.addPrinter(newPrinterInfo(sn, true));
            boxInfo.addPrinter(newPrinterInfo(sn, false));
            boxList.add(boxInfo); 
        }
        boxs = boxList.toArray(new BoxInfo[boxList.size()]);
        inetPort = Integer.parseInt(context.getProperty("netty.port", "9190"));
        inetHost = context.getProperty("netty.server", "127.0.0.1");
    }

    
    @Override
    public void start() throws Exception {
        int boxSize = boxs.length;
        if(boxSize == 0)
            logger.info(" zero box bind client");
        
        group = new NioEventLoopGroup(boxSize + 3, threadFactory);
        
        clients = new NettyClient[boxSize];
        for (int i = 0; i < boxSize; i++) {
            clients[i] = new NettyClient(boxs[i], group);
            clients[i].connect(inetHost, inetPort);
            logger.info(boxs[i]+"");
            logger.info(boxs[i].getSn()+" is bind");
        }
        
        group.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
    }


    @Override
    public void close() {
        if(clients != null){
            try{
                for (NettyClient client : clients) {
                    if(client !=null )
                        client.close();
                }
                
                if(group != null){
                    group.shutdownGracefully();
                }
            }catch(Exception e){
                logger.error("close",e);
                System.exit(-2);
            }
        }
    }

   private static PrinterInfo  newPrinterInfo(String boxsn, boolean pdf){
       String modelCode = pdf? "pdf-001L" : "cpus-002L";
       String brandCode="Model_Java";
       String mac = "";
       String sn = (pdf? "UPDF":"CPUS") + boxsn ;
       return new PrinterInfo(brandCode, modelCode, sn, mac);
   }
    
}
