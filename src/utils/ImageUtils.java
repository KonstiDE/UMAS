package utils;

public class ImageUtils {

    public static boolean isJPG(String name){
        return name.endsWith(".jpg") || name.endsWith(".JPG");
    }

    public static boolean isTIF(String name){
        return name.endsWith(".tif") || name.endsWith(".TIF");
    }

    public static boolean isMRK(String name){
        return name.endsWith(".mrk") || name.endsWith(".MRK");
    }

}
