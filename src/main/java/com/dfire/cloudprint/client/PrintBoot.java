package com.dfire.cloudprint.client;

import java.io.InputStream;
import java.util.Properties;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.dfire.cloudprint.client.bootstrap.BootStrap;
import com.dfire.cloudprint.client.config.ModeEnum;
import com.dfire.cloudprint.client.utils.FileResouceUtils;

public class PrintBoot {

    private static Logger logger = LoggerFactory.getLogger(PrintBoot.class);

    private static BootStrap getBootStrap( Properties context){
        String serverMode = context.getProperty("server.mode", "client");
        ModeEnum  mode = ModeEnum.getModeEnumByName(serverMode);
        Validate.notNull(mode,"server.mode [server,client]");
        return mode.getStrap();
    }
    
    public static void main(String[] args) {
        BootStrap strap = null;
        try{
            try(
                 InputStream is = FileResouceUtils.getResouceAsStream(Constants.COMFIG_PROPERTIES);
             ){
                  if(is == null){
                      logger.error("配置文件{}找不到",Constants.COMFIG_PROPERTIES);
                      return;
                  }
                  Properties context = new Properties();
                  context.load(is);
                  strap = getBootStrap(context);
                  strap.load(context);
              } 
            strap.start();
        }catch (Exception e) {
            logger.error("启动异常",e);
        }finally{
            if(strap != null){
                strap.close();
            }
        }
    }
}
