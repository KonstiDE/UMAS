package wue.eorc.umas.utils;

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

    public static boolean isJPGorAux(String name){
        return isJPG(name) && isAux(name);
    }
    public static boolean isPNGorAux(String name){
        return isJPG(name) && isAux(name);
    }

    public static boolean isTIForAux(String name){
        return isTIF(name) && isAux(name);
    }

}
