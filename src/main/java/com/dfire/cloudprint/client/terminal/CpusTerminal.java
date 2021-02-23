package com.dfire.cloudprint.client.terminal;

import java.io.File;
import java.util.List;
import org.apache.commons.io.FileUtils;
import com.dfire.cloudprint.client.Constants;
import com.dfire.cloudprint.client.cpus.CpusFileFactory;
import com.dfire.cloudprint.client.cpus.PpdEnum;

public class CpusTerminal {
    
    public static void suportPpdBuild(String pdfpath, String ppdName){

        File pdf = new File(pdfpath);
        
        if(!pdf.exists()){
            System.out.println("pdf not exist");
            return;
        }        
        try{
            File file = CpusFileFactory.pdfToCpus(ppdName, pdf);
            if (null == file){
                System.out.println(ppdName+ " shell生成失败");
                return;
            }
            System.out.println(ppdName+ " 生成"+ file.getAbsolutePath());

        }catch(Exception e){
            e.printStackTrace();
        }
      
    }
    

    public static void suportPpdBuild(String pdfpath) throws Exception {

        File pdf = new File(pdfpath);
        
        if(!pdf.exists()){
            System.out.println("pdf not exist");
            return;
        }
        
        int fnum = 0;
        int snum = 0;
        List<PpdEnum> es = PpdEnum.listSuportEnums();

        for (PpdEnum ppdEnum : es) {
            
            try{
                String path = pdf.getAbsolutePath().replace(pdf.getName(), ppdEnum.name()+Constants.PDF_SUFFIX);

                File tempPdf = new File(path);

                FileUtils.copyFile(pdf, tempPdf);

                File file = CpusFileFactory.pdfToCpus(ppdEnum.name(), tempPdf);
                
                if(file !=null ){
                    System.out.println(ppdEnum.name()+ " 生成"+ file.getAbsolutePath());
                    snum ++;
                    continue;
                }
                
                System.out.println(ppdEnum.name()+ " 生成失败");
                fnum++;
            }catch(Exception e){
                System.out.println(ppdEnum.name()+ " 生成失败");
                fnum++;
            }
        }
        
        System.out.println("总数:"+es.size()+"成功:"+snum+",失败:"+fnum);
    }

    public static void printSuportPpd() {
        List<PpdEnum> es = PpdEnum.listSuportEnums();

        for (int i = 0; i < es.size(); i += 4) {
            StringBuffer sb = new StringBuffer(es.get(i).toString());
            if (i + 1 < es.size()) {
                sb.append("   " + es.get(i+1).toString());
            }

            if (i + 2 < es.size()) {
                sb.append("   " + es.get(i+2).toString());
            }

            if (i + 3 < es.size()) {
                sb.append("   " + es.get(i+3).toString());
            }
            System.out.println(sb.toString());
        }
    }
    
    public static void main(String[] args) {
        printSuportPpd();
    }
}
