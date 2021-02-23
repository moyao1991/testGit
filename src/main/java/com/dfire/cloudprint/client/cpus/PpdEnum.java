package com.dfire.cloudprint.client.cpus;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.lang3.Validate;
import com.dfire.cloudprint.client.Constants;
import com.dfire.cloudprint.client.utils.FileResouceUtils;

/**
 * 
 * @author moyao
 * @date 2018年9月19日
 */
public enum PpdEnum {

    CANON_IP4800_SERIES("Canon_IP4800_series",TypeEnum.CANON),
    CANON_MG5100_SERIES("Canon_MG5100_series",TypeEnum.CANON),
    CANON_MG5200_SERIES("Canon_MG5200_series",TypeEnum.CANON),
    CANON_MG6100_SERIES("Canon_MG6100_series",TypeEnum.CANON),
    CANON_MG8100_SERIES("Canon_MG8100_series",TypeEnum.CANON),
    CANON_MP250_SERIES("Canon_MP250_series",TypeEnum.CANON),
    CANON_MP280_SERIES("Canon_MP280_series",TypeEnum.CANON , true),
    CANON_MP495_SERIES("Canon_MP495_series",TypeEnum.CANON),
    
    DASCOM_DS_660("DASCOM_DS-660",TypeEnum.DASCOM),
    
    DELI_DL_620K("DELI_DL-620K",TypeEnum.DELI),
    DELI_DL_630K("DELI_DL-630K",TypeEnum.DELI),
    DELI_DL_625K("DELI_DL-625K",TypeEnum.DELI, true),

    EPSON_LQ_610K("EPSON_LQ-610K",TypeEnum.EPSON , true),
    EPSON_LQ_610KII("EPSON_LQ-610KII",TypeEnum.EPSON, true),
    EPSON_LQ_615K("EPSON_LQ-615K",TypeEnum.EPSON),
    EPSON_LQ_615KII("EPSON_LQ-615KII",TypeEnum.EPSON, true),
    EPSON_LQ_630K("EPSON_LQ-630K",TypeEnum.EPSON, true),
    EPSON_LQ_630KII("EPSON_LQ-630KII",TypeEnum.EPSON, true),
    EPSON_LQ_635K("EPSON_LQ-635K",TypeEnum.EPSON, true),
    EPSON_LQ_635KII("EPSON_LQ-635KII",TypeEnum.EPSON, true),
    EPSON_LQ_690K("EPSON_LQ-690K",TypeEnum.EPSON),
    EPSON_LQ_730K("EPSON_LQ-730K",TypeEnum.EPSON, true),
    EPSON_LQ_730KII("EPSON_LQ-730KII",TypeEnum.EPSON, true),
    EPSON_LQ_735K("EPSON_LQ-735K",TypeEnum.EPSON, true),
    EPSON_LQ_735KII("EPSON_LQ-735KII",TypeEnum.EPSON, true),
    EPSON_UB_E03("EPSON_UB-E03",TypeEnum.EPSON),
    
    NFCP_DPK710("NFCP_DPK710",TypeEnum.NFCP , true),
    
    HP_LASERJET_M1005_MFP("HP_Laserjet_M1005_Mfp",TypeEnum.HP),
    HP_LASERJET_PRO_MFP_M128FN("HP_LaserJet_Pro_MFP_M128fn",TypeEnum.HP, true),
    HP_LASERJET_PROFESSIONAL_P1108("HP_LaserJet_Professional_P1108",TypeEnum.HP, true),
    
    OKI_MICROLANE_MF5100("OKI_MICROLANE-MF5100+",TypeEnum.OKI),
    OKI_ML5200F("OKI_ML5200F+",TypeEnum.OKI),

    SPRT_ZM_POS58("SPRT_ZM-POS58",TypeEnum.SPRT, PageEnum.PAGE_58, true),
    
    ZHONGYING_NX_635KII("ZhongYing_NX-635KII",TypeEnum.ZHONGYING, true);

    private String name;

    private TypeEnum type;
    
    private PageEnum page;
    
    private boolean support;
    
    private PpdEnum(String name , TypeEnum type) {
        this(name, type, false);
    }
    
    private PpdEnum(String name , TypeEnum type, boolean support) {
        this(name, type, PageEnum.PAGE_241, support);
    }
    
    private PpdEnum(String name , TypeEnum type, PageEnum page, boolean support) {
        this.name = name;
        this.type = type;
        this.page = page;
        this.support = support;
    }

    public boolean isSupport() {
        return support;
    }
    
    public boolean is58Page() {
        return PageEnum.PAGE_58 == this.page;
    }
    
    public boolean is241Page() {
        return PageEnum.PAGE_241 == this.page;
    }

    public File getFile() throws FileNotFoundException {
        String path = getPath();
        
        File ppd = FileResouceUtils.getResouceFile(path);

        if (ppd == null) {
            throw new FileNotFoundException(path);
        }
        return ppd;
    }

    private String getPath(){
        return new StringBuffer(PrintConstant.DRIVER_PATH).
                append(File.separator).
                append(type.getName()).
                append(File.separator).
                append(name).
                append(Constants.PPD_SUFFIX).
                toString();
    }
    
   
    TypeEnum getTypeEnum(){
        return this.type;
    }
    
    @Override
    public String toString() {
        return name+Constants.PPD_SUFFIX;
    }
    
    
    public static  Optional<PpdEnum> findByName(String name){
        Validate.notBlank(name, "name is empty");
        return Arrays.asList(values()).stream()
                .filter(e->{return compare(name, e);})
                .findAny();
    }
    
    public static List<PpdEnum> listEnums(){
        return Arrays.asList(values());
    }
    
    public static List<PpdEnum> listSuportEnums(){
        return listEnums().stream()
                .filter(PpdEnum::isSupport)
                .collect(Collectors.toList());
    }
    
    public static List<PpdEnum> listSuportAnd241Enums(){
        return listEnums().stream()
                .filter(PpdEnum::isSupport)
                .filter(PpdEnum::is241Page)
                .collect(Collectors.toList());
    }
    
    public static List<PpdEnum> listSuportAnd58Enums(){
        return listEnums().stream()
                .filter(PpdEnum::isSupport)
                .filter(PpdEnum::is58Page)
                .collect(Collectors.toList());
    }
    
    private static boolean compare(String name , PpdEnum e){
        return name.endsWith(Constants.PPD_SUFFIX) ?
                name.equalsIgnoreCase(e.toString()) :
                name.equalsIgnoreCase(e.name);
    }
    

}
