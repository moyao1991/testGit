package com.dfire.cloudprint.client.cpus;

import java.io.File;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import com.dfire.cloudprint.client.Constants;
import com.dfire.cloudprint.client.utils.FileResouceUtils;

enum TypeEnum {
        
        EPSON(PpdTypeCmd.RASTERTOEPSON, "epson"), 
        HP(new PpdTypeCmd("hpcups"),"hp"), 
        NFCP("fujitsu"), 
        ZHONGYING("zhongying"), 
        CANON(new PpdTypeCmd("pdftops"), new PpdTypeCmd("pstocanonij") , "canon"), 
        SPRT(PpdTypeCmd.RASTERTOSPRT, "sprt"), 
        DASCOM("dascom"), 
        OKI("oki"), 
        DELI("deli");

        private PpdTypeCmd preCmd;

        private PpdTypeCmd cmd;
        
        private String name;
        
        private TypeEnum(String name) {
            this(PpdTypeCmd.RASTERTOEPSON_SIZE, name);
        }
        
        private TypeEnum(PpdTypeCmd cmd, String name) {
            this(PpdTypeCmd.GSTORASTER, cmd, name);
        }
        
        private TypeEnum(PpdTypeCmd preCmd, PpdTypeCmd cmd, String name) {
            this.preCmd = preCmd;
            this.cmd = cmd;
            this.name = name;
        }
  
        public PpdTypeCmd getPreCmd() {
            return preCmd;
        }

        public PpdTypeCmd getCmd() {
            return cmd;
        }

        public String getName() {
            return name;
        }
        
        public static Pair<TypeEnum, File>  findTypePpdByPddName(String pddName){
            if(StringUtils.isEmpty(pddName)) return null;
            pddName = pddName.endsWith(Constants.PPD_SUFFIX) ? pddName : pddName +Constants.PPD_SUFFIX;
            TypeEnum[] types = TypeEnum.values();
            for (TypeEnum type : types) {
                String path = new StringBuffer(PrintConstant.DRIVER_PATH).
                        append(File.separator).
                        append(type.getName()).
                        append(File.separator).
                        append(pddName).toString();
                
                File ppd = FileResouceUtils.getResouceFile(path);
                
                if (ppd !=null) {
                    return new MutablePair<>(type, ppd);
                }
            }
            
            return null;
        }

    }