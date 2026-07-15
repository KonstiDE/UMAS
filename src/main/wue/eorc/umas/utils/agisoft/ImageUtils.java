package wue.eorc.umas.utils.agisoft;

import java.io.File;

public class ImageUtils {

    public static boolean isJPG(String name){
        return name.endsWith(".jpg") || name.endsWith(".JPG");
    }

    public static boolean isTIF(String name){
        return name.endsWith(".tif") || name.endsWith(".TIF");
    }

    public static boolean isAux(String name){
        return name.endsWith(".mrk") || name.endsWith(".MRK") || name.endsWith(".nav") || name.endsWith(".NAV") ||
                name.endsWith(".obs") || name.endsWith(".OBS") || name.endsWith(".rtk") || name.endsWith(".RTK");
    }

    public static boolean isJPGorAux(File name){
        return isJPG(name.getName()) || isAux(name.getName());
    }
    public static boolean isPNGorAux(File name){
        return isJPG(name.getName()) || isAux(name.getName());
    }

    public static boolean isTIForAux(File name){
        return isTIF(name.getName()) || isAux(name.getName());
    }

    public static boolean isThermalJPG(File name){
        return isJPG(name.getName()) && name.getName().endsWith("_T.JPG");
    }

    public static boolean isWJPG(File name){
        return isJPG(name.getName()) && name.getName().endsWith("_W.JPG");
    }

}
