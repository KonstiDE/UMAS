package wue.eorc.umas.utils;

import javafx.scene.image.Image;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;

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

    public static boolean isJPGorMRK(String name){
        return isJPG(name) && isMRK(name);
    }
    public static boolean isPNGorMRK(String name){
        return isJPG(name) && isMRK(name);
    }

    public static boolean isTIForMRK(String name){
        return isTIF(name) && isMRK(name);
    }

}
