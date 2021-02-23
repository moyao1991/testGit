package com.dfire.cloudprint.client.cpus;

import java.io.File;
import java.io.FileNotFoundException;
import com.dfire.cloudprint.client.Constants;
import com.dfire.cloudprint.client.utils.FileResouceUtils;

class PpdTypeCmd {
    
    static PpdTypeCmd GSTORASTER = new PpdTypeCmd("gstoraster");
    
    static PpdTypeCmd RASTERTOEPSON = new PpdTypeCmd("rastertoepson");
    
    static PpdTypeCmd RASTERTOSPRT = new PpdTypeCmd("rastertosprt");

    static PpdTypeCmd RASTERTOEPSON_SIZE = new PpdTypeCmd("rastertoepson", "PageSize=2dfire");


    private String sh;
    
    private String sizeParam;

    public PpdTypeCmd(String sh) {
       this(sh, "1");
    }
    
    public PpdTypeCmd(String sh, String sizeParam) {
        this.sh = sh;
        this.sizeParam = sizeParam;
    }

    public File getShFile() throws FileNotFoundException {
        String path = getPath();

        File sh = FileResouceUtils.getResouceFile(path);

        if (sh == null) {
            throw new FileNotFoundException(path);
        }
        return sh;
    }

    public String getSizeParam() {
        return sizeParam;
    }

    public String getSuffix(){
        return  "pdftops".equals(sh) ? Constants.PS_SUFFIX :
                "gstoraster".equals(sh) ? Constants.RAS_SUFFIX : Constants.TMP_SUFFIX;
    }
   
     
    private String getPath(){
        return new StringBuffer(PrintConstant.EXE_PATH).
                append(File.separator).
                append(sh).
                toString();
    }
}
