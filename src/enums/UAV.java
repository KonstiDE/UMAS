package enums;

import controller.listeners.CopyProgressListener;
import utils.ImageUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public enum UAV {

    MAVICM2("MavicM2", List.of(Sensor.FIXEDM2)),
    MAVICM3M("MavicM3M", List.of(Sensor.FIXEDM3M)),
    MAVICM3T("MavicM3T", List.of(Sensor.FIXEDM3T)),
    MAVICM4T("MavicM4T", List.of(Sensor.FIXEDM4T)),
    PHAMTOM("Phantom", List.of(Sensor.FIXEDMPHANTOM)),
    M300("M300", List.of(Sensor.ALTUM, Sensor.L1, Sensor.H20T, Sensor.MXDUAL)),
    M600("M600", List.of(Sensor.ALTUM, Sensor.MXDUAL, Sensor.NANOHP, Sensor.LIAIRV)),
    WINGTRA("Wingtra", List.of(Sensor.NIKONRGB, Sensor.ALTUM)),
    TRINITY("Trinity", List.of(Sensor.D2M, Sensor.ALTUMPT, Sensor.Q2));

    private final String name;
    private final List<Sensor> sensors;

    UAV(String name, List<Sensor> sensors){
        this.name = name;
        this.sensors = sensors;
    }

    public List<Sensor> getSensors(){
        return sensors;
    }

    public String getName() {
        return name;
    }

    public static UAV fromName(String name){
        for(UAV uav : UAV.values()){
            if(uav.name.equals(name)){
                return uav;
            }
        }
        throw new IllegalArgumentException("Invalid UAV name: " + name);
    }

    public static void copyM3M(List<ImageType> imageTypes, String flightDirectory, List<String> originFlightDirs, CopyProgressListener copyProgressListener, List<String> originCalibDirs) throws IOException {
        if(imageTypes.contains(ImageType.RGB)) {
            copy(originFlightDirs, flightDirectory, ImageUtils::isJPG, copyProgressListener, "0_Images", "0_RGB");
        }
        if(imageTypes.contains(ImageType.MULTISPECTRAL)) {
            copy(originFlightDirs, flightDirectory, ImageUtils::isTIF, copyProgressListener , "0_Images", "1_MS");
            copy(originCalibDirs, flightDirectory, _ -> true, copyProgressListener , "0_Images", "2_CALIB");
        }
    }

    private static void copy(List<String> origins, String flightDirectory, Predicate<String> filter, CopyProgressListener copyProgressListener, String... baseDest) throws IOException {
        ArrayList<File> filesToCopy = new ArrayList<>();
        for(String absPathString : origins){
            File[] files = Paths.get(absPathString).toFile().listFiles((_, name) -> filter.test(name));
            if(files != null){
                filesToCopy.addAll(Arrays.asList(files));
            }
        }
        String innerPath = String.join(File.separator, baseDest);

        int c = 0;
        int max = filesToCopy.size();
        for(File file : filesToCopy){
            Files.copy(Path.of(file.getAbsolutePath()), Paths.get(flightDirectory, innerPath, file.getName()));
            c++;
            copyProgressListener.receivedProgress((double) c / max);
        }
    }

}
