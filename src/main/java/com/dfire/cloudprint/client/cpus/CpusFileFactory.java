package com.dfire.cloudprint.client.cpus;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.dfire.cloudprint.client.Constants;
import com.dfire.cloudprint.client.utils.PrintThreadFactory;

public class CpusFileFactory {

    private static Logger logger = LoggerFactory.getLogger(CpusFileFactory.class);

    private static ExecutorService executor= Executors.newCachedThreadPool(PrintThreadFactory.cupsThreadFactory());

    public static File pdfToCpus(String ppdName, File pdf) throws FileNotFoundException{
        Validate.notNull(pdf);
        Validate.notNull(ppdName);

        if(!pdf.exists()){
            throw new FileNotFoundException(pdf.getAbsolutePath());
        }
        Optional<PpdEnum>  optional = PpdEnum.findByName(ppdName);

        if(!optional.isPresent()){
            Pair<TypeEnum, File> typeAndPpd = TypeEnum.findTypePpdByPddName(ppdName);
            if(typeAndPpd != null) 
                return pdfToCpus(typeAndPpd.getLeft(), typeAndPpd.getRight(), pdf);
            return pdfToCupsByShell(pdf, ppdName);
        }
        PpdEnum ppdEnum = optional.get();
        TypeEnum type =  ppdEnum.getTypeEnum();
        File pddFile = ppdEnum.getFile();
        
        return pdfToCpus(type, pddFile, pdf);
    }
    
    private static File pdfToCpus(TypeEnum type, File ppd, File pdf) throws FileNotFoundException {
        Validate.notNull(type);
        Validate.notNull(pdf);
        Validate.notNull(type);

        if(!pdf.exists()){
            throw new FileNotFoundException(pdf.getAbsolutePath());
        }

        PpdTypeCmd preCmd = type.getPreCmd();
        PpdTypeCmd cmd = type.getCmd();

        File tmpFile = processFile(ppd, preCmd, pdf);
        if(tmpFile == null) return null;
        
        File cups = processFile(ppd, cmd, tmpFile);
        if(cups != null ){
            tmpFile.delete();
        }
        return cups;
    }
    
    private static File pdfToCupsByShell(File pdf, String ppdName)  {
        // 定义传入shell脚本的参数
        String[] cmds = new String[4];
        cmds[0] = PrintConstant.SH_PATH; // 脚本全路径
        
        String sourcePath = pdf.getAbsolutePath();
        String tmpName =  FilenameUtils.getBaseName(sourcePath);
        String targetPath = new StringBuffer(PrintConstant.TMP_PATH)
                .append(File.separator)
                .append(tmpName)
                .append(Constants.TMP_SUFFIX)
                .toString();
        cmds[1] = sourcePath;
        cmds[2] = ppdName; // ppd文件添加路径
        cmds[3] = tmpName;  //tmp文件无后缀，不添加路径
        
        logger.info("pdfToCupsByShell{}", Arrays.toString(cmds));
        
        File tmpFile = new File(targetPath);
        if(tmpFile.exists()){
            tmpFile.delete();
        }
        
        ProcessBuilder pb = new ProcessBuilder(cmds);
        Process process=null;
        try {
            process = pb.start();    
            Future<Void> errorFuture = executor.submit(new PrintErrorTask(process));
            int end = process.waitFor();
            if (0 != end) {
                throw new Exception(String.format("脚本waitFor[%s]异常", end));
            }
            errorFuture.get();
        } catch(Exception e){
            logger.error("执行出错:"+cmds,e);
            return null;
        }finally {
            if(process!= null)
                process.destroy();
        }
        return tmpFile.exists() ? tmpFile : null;
    }

    
    private static File processFile(File ppd , PpdTypeCmd cmd, File sourceFile) throws FileNotFoundException {

        String shPath = cmd.getShFile().getAbsolutePath();
        String sizeParam = cmd.getSizeParam();
        String ppdPath = ppd.getAbsolutePath();
        String sourcePath = sourceFile.getAbsolutePath();
        String sourceParent = FilenameUtils.getFullPathNoEndSeparator(sourcePath);
        String sourceName =  FilenameUtils.getBaseName(sourcePath);
        String targetPath = new StringBuffer(sourceParent)
                                .append(File.separator)
                                .append(sourceName)
                                .append(cmd.getSuffix())
                                .toString();
        
        List<String> command = commandLine(shPath, sizeParam, ppdPath, sourcePath, targetPath);        
        logger.info("processFile{}", JSON.toJSON(command));
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        Process process = null;
        Integer size = 0;
        File tmpFile = new File(targetPath);
        try{
           process = processBuilder.start();

           Future<Integer> printFuture = executor.submit(new PrintTask(process, targetPath));
           Future<Void> errorFuture = executor.submit(new PrintErrorTask(process));

           int end = process.waitFor();
           if (0 != end) {
                throw new Exception(String.format("脚本waitFor[%s]异常", end));
           }
           
           size = printFuture.get();
           errorFuture.get();
           
           if(size <= 0){
               throw new Exception(String.format("文件生成大小[%s]异常", size));
           }
        }catch(Exception e){
            logger.error("执行出错:"+command,e);
            return null;
        }finally {
            if(process!= null)
                process.destroy();
        }
        return tmpFile.exists() ? tmpFile : null;
    }
 
    private static List<String> commandLine(String shPath, String sizeParam, String ppdPath, String sourcePath, String targetPath){
        return Arrays.asList(shPath, "1", PrintConstant.DRIVER_USER, PrintConstant.DRIVER_PWD, "1", sizeParam, ppdPath, sourcePath);
    }
    
    
    private static class PrintErrorTask implements Callable<Void>{
        
        private Process process;

        public PrintErrorTask(Process process) {
            this.process = process;
        }

        @Override
        public Void call() throws Exception {
            try (InputStream is = process.getErrorStream();
                    InputStreamReader isr = new InputStreamReader(is);
                    BufferedReader stdInput = new BufferedReader(isr)) {
                String info = null;
                while ((info = stdInput.readLine()) != null) {
                    logger.info("脚本输出流信息：" + info);
                }
                return null;
            }
        }
    }
    private static class PrintTask implements Callable<Integer>{
        
        private Process process;
        
        private String path;
        
        public PrintTask(Process process, String path) {
            this.process = process;
            this.path = path;
        }

        @Override
        public Integer call() throws Exception {
            try (FileOutputStream output = new FileOutputStream(path);
                    InputStream is = process.getInputStream();) {
                int size = IOUtils.copy(process.getInputStream(), output);
                logger.info("脚本输出流信息：文件 {},大小{}", path, size);
                return size;
            }
        }
    }
}
