package com.dfire.cloudprint.client.bootstrap;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.UnrecognizedOptionException;
import org.apache.commons.lang3.StringUtils;
import com.dfire.cloudprint.client.Constants;
import com.dfire.cloudprint.client.config.BoxInfo;
import com.dfire.cloudprint.client.config.PrinterInfo;
import com.dfire.cloudprint.client.cpus.CpusFileFactory;
import com.dfire.cloudprint.client.netty.AttributeKeys;
import com.dfire.cloudprint.client.netty.NettyServer;
import com.dfire.cloudprint.client.netty.service.ChannelsContext;
import com.dfire.cloudprint.client.netty.service.DefaultChannelsContext;
import com.dfire.cloudprint.client.terminal.CpusTerminal;


public class BootServer implements BootStrap , Runnable{

    private NettyServer server = null;
    private ChannelsContext  context = null;

    private int inetPort = 9190;

    @Override
    public void load(Properties context) throws Exception {
        inetPort = Integer.parseInt(context.getProperty("netty.port", "9190"));
    }
    
    @Override
    public void start() throws InterruptedException {
        this.context = new DefaultChannelsContext();
        this.server = new NettyServer();
        server.context(context);
        server.bind(inetPort);
        Thread t = new Thread(this);
        t.start();
        t.join();
    }

    @Override
    public void close() {
        try{
            server.close();
        }catch(Exception e){
            e.printStackTrace();
            System.exit(-2);
        }
    }
    
    public static void main(String[] args) throws InterruptedException {
        new BootServer().start();
    }
    
   
    @Override
    public void run() {
        Options options = new Options();
        options.addOption("help", false, "命令说明");
        options.addOption("box", false, "显示在线盒子");
        options.addOption("use", true, "-use snOrMac 打印文件");
        options.addOption("where", false, "正在使用的打印机");
        options.addOption("print", true, "print filePath");
        options.addOption("printTxt", true, "print content 直接发送内容");
        options.addOption("upgrade", true, "upgrade downurl 直接下载路径");
        options.addOption("tppd", true, "tppd pdfPath 测试所有支持驱动");
        options.addOption("ppd", false, "显示所有驱动名");
        options.addOption("outppd", true, "outppd ppdName 和print, tppd配合使用");
        options.addOption("exit", false, "退出");

        
        HelpFormatter hf = new HelpFormatter();
        CommandLineParser parser = new DefaultParser();
        hf.printHelp(Constants.USAGE, null, options, "box console please enter");
        
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            while(true){
                try{
                    String[] cmdStr = br.readLine().split("\\s+");
                    CommandLine cmd = parser.parse(options, cmdStr);
                    
                    if(cmd.hasOption("help")){
                        hf.printHelp(Constants.USAGE, null, options, null);
                    }else if(cmd.hasOption("box")){
                        printBoxs();
                    }else if(cmd.hasOption("use")){
                        String macOrSn = cmd.getOptionValue("use");
                        use(macOrSn);
                    }else if(cmd.hasOption("where")){
                        where();
                    }else if(cmd.hasOption("ppd")){
                        CpusTerminal.printSuportPpd();
                    }else if(cmd.hasOption("tppd")  && cmd.hasOption("outppd")){
                        String pdfPath = cmd.getOptionValue("tppd");
                        String ppdName = cmd.getOptionValue("outppd");
                        CpusTerminal.suportPpdBuild(pdfPath, ppdName);
                    }else if(cmd.hasOption("tppd")){
                        String pdfPath = cmd.getOptionValue("tppd");
                        CpusTerminal.suportPpdBuild(pdfPath);
                    }else if(cmd.hasOption("exit")){
                        System.out.println("stoping");
                        break;
                    }else if(cmd.hasOption("print") && cmd.hasOption("outppd")){
                        String filepath = cmd.getOptionValue("print");
                        String ppdName = cmd.getOptionValue("outppd");
                        print(filepath, ppdName);
                    }else if(cmd.hasOption("print")){
                        String filepath = cmd.getOptionValue("print");
                        print(filepath);
                    }else if(cmd.hasOption("printTxt")){
                        String content = cmd.getOptionValue("printTxt");
                        printTxt(content);
                    }else if(cmd.hasOption("upgrade")){
                        String url = cmd.getOptionValue("upgrade");
                        upgrade(url);
                    }else{
                        System.out.println("not support command:"+cmdStr[0]);
                    }
                }catch (UnrecognizedOptionException e) {
                    e.printStackTrace();
                }
               
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            close();
        }
    }
    
 

    Map<PrinterInfo, BoxInfo> table = new HashMap<>();
    List<BoxInfo> emptyBox = new ArrayList<>();

    PrinterInfo currPrinter = null;
    
    private void freshenTable(){
        table.clear();
        emptyBox.clear();
        List<BoxInfo> boxs = context.getChannels().stream().map(channel->{
            if(channel == null || !channel.isOpen())
                return null;
            return channel.attr(AttributeKeys.BOX_INFO).get();
        }).filter(x-> x != null).collect(Collectors.toList());
        
        boxs.forEach(b->{
            PrinterInfo[] ps = b.getPrinterInfo();
            if(ps.length == 0){
                emptyBox.add(b);
                return;
            }
            for (PrinterInfo p : ps) {
                table.put(p, b);
            }
        });
        
        if(currPrinter != null){
            currPrinter = table.keySet().stream()
                    .filter(x->StringUtils.equals(currPrinter.getMacOrSn(), x.getMacOrSn()))
                    .findAny().orElse(null);
        }
        
    }
    
    private void print(String filePath, String ppd){
        File file = new File(filePath);
        if(!file.exists()){
            System.out.println("文件不存在");
            return; 
        }
        File cpusFile = null;
        try {
            cpusFile = CpusFileFactory.pdfToCpus(ppd, file);
            if (null == cpusFile){
                System.out.println(ppd+ " 生成失败");
                return;
            }            
        } catch (Exception e) {
            System.out.println("文件转换出错");
            e.printStackTrace();
        }
        print(cpusFile);
    }
    
    private void print(String filePath){
        File file = new File(filePath);
        if(!file.exists()){
            System.out.println("文件不存在");
            return; 
        }
        print(file);
    }
   
    private void print(File file){
        freshenTable();
        if(currPrinter == null){
            System.out.println("请先选定一台打印机");
            return;
        }
        String taskId = System.currentTimeMillis()+"";
        String printerMacOrSn = currPrinter.getMacOrSn();
        BoxInfo box = table.get(currPrinter);
        if(server.sendPrintCommand(box.getSn(), printerMacOrSn, file, taskId)){
            System.out.println("打印成功taskId:"+taskId);
        }else{
            System.out.println("打印失败,请查看日志文件");
        }
    }
    
    private void upgrade(String url){
        freshenTable();
        if(currPrinter == null){
            System.out.println("请先选定一台打印机");
            return;
        }
        BoxInfo box = table.get(currPrinter);
        if(server.sendUpgradeReq(box.getSn(), url)){
            System.out.println("升级成功:" + box.getSn());
        }else{
            System.out.println("升级失败");
        }
    }
    
    private void printTxt(String context){
        freshenTable();
        if(currPrinter == null){
            System.out.println("请先选定一台打印机");
            return;
        }
        String taskId = System.currentTimeMillis()+"";
        String printerMacOrSn = currPrinter.getMacOrSn();
        BoxInfo box = table.get(currPrinter);
        if(server.sendPrintCommand(box.getSn(), printerMacOrSn, context, taskId)){
            System.out.println("打印成功taskId:"+taskId);
        }else{
            System.out.println("打印失败,请查看日志文件");
        }
    }
    
    private void printBoxs(){
       freshenTable();
       if(table.isEmpty()){
           System.out.println("[]");
           return;
       }
       System.out.println("     snOrmac               box[sn]");
       table.entrySet().forEach((e)->{
           PrinterInfo p = e.getKey();
           BoxInfo b = e.getValue();
           System.out.println(p.getMacOrSn()+"        " + b.getSn());
       });
       emptyBox.forEach((b)->{
           System.out.println("     ------             " + b.getSn());

       });
    }
    
    private void where(){
        if(currPrinter == null)
            System.out.println("not printer in");
        else
            System.out.println("printer["+currPrinter.getMacOrSn()+"]");
    }
    
    private void use(String macOrSn){
        freshenTable();
        Optional<PrinterInfo> op = table.keySet().stream()
                .filter(x->StringUtils.equals(macOrSn, x.getMacOrSn()))
                .findAny();
        
        if(op.isPresent()){
            currPrinter = op.get();
            System.out.println("use printer["+macOrSn+"]");
        }else{
            System.out.println("no printer find");
        }
    }
}
