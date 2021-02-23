package com.dfire.cloudprint.client.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.io.FileUtils;


public class FileResouceUtils {
    
    
    public static File getResouceFile(String relaPath){
        File file = new File(relaPath);

        if(file.exists()){
           if(file.isDirectory()) return null;
           return file;
        }
        URL url = FileResouceUtils.class.getResource("/" + relaPath);
        return FileUtils.toFile(url);                
    }
    
    public static InputStream getResouceAsStream(String relaPath){
        File file = new File(relaPath);

        if(file.exists()){
            InputStream is = null;
            try {
                is = FileUtils.openInputStream(file);
            } catch (IOException e) {
                
            }
            return is;
        }
        
        return FileResouceUtils.class.getResourceAsStream("/" + relaPath);
    }
    
}
