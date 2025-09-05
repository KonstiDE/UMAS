package wue.eorc.umas.futures;

import wue.eorc.umas.controller.listeners.CopyProgressListener;
import wue.eorc.umas.enums.ImageType;
import wue.eorc.umas.enums.UAV;
import wue.eorc.umas.models.Flight;
import wue.eorc.umas.utils.ImageUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

public class FileCopier {

    CopyProgressListener copyProgressListener;

    private final CompletableFuture<Void> copyTask;

    private FileTime[] times;

    public FileCopier(CopyProgressListener copyProgressListener, Flight flight) {
        this.copyProgressListener = copyProgressListener;

        copyTask = CompletableFuture.runAsync(() -> {
            try {
                copyDroneImages(flight.getUav(), flight.getImageTypes().keySet(), flight.getOriginFlightDirs(), flight.getOriginCalibDirs(), flight.getFlightDirectory());
            } catch (InterruptedException | IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public CompletableFuture<Void> getCopyTask() {
        return copyTask;
    }


    private void copyDroneImages(UAV uav, Set<ImageType> imageTypes, List<String> originFlightDirs, List<String> originCalibDirs, String flightDirectory) throws IOException, InterruptedException {
        switch(uav) {
            case MAVICM3M -> copyM3M(imageTypes, originFlightDirs, originCalibDirs, flightDirectory);
        }
    }

    public void copyM3M(Set<ImageType> imageTypes, List<String> originFlightDirs, List<String> originCalibDirs, String flightDirectory) throws IOException {
        if(imageTypes.contains(ImageType.RGB)) {
            copy(originFlightDirs, flightDirectory, ImageUtils::isJPGorAux, "0_Images", "0_RGB");
        }
        if(imageTypes.contains(ImageType.MULTISPECTRAL)) {
            copy(originFlightDirs, flightDirectory, ImageUtils::isTIForAux,"0_Images", "1_MS");
            copy(originCalibDirs, flightDirectory, _ignored -> true,"0_Images", "2_CALIB");
        }
    }

    public void copyM3T(Set<ImageType> imageTypes, List<String> originFlightDirs, List<String> originCalibDirs, String flightDirectory) throws IOException {
        if(imageTypes.contains(ImageType.RGB)) {
            copy(originFlightDirs, flightDirectory, ImageUtils::isJPGorAux, "0_Images", "0_RGB");
        }
        if(imageTypes.contains(ImageType.IR)) {
            copy(originFlightDirs, flightDirectory, ImageUtils::isTIForAux,"0_Images", "1_T");
        }
    }

    public void copyM4T(Set<ImageType> imageTypes, List<String> originFlightDirs, List<String> originCalibDirs, String flightDirectory) throws IOException {
        if(imageTypes.contains(ImageType.RGB)) {
            copy(originFlightDirs, flightDirectory, ImageUtils::isJPGorAux, "0_Images", "0_RGB");
        }
        if(imageTypes.contains(ImageType.IR)) {
            copy(originFlightDirs, flightDirectory, ImageUtils::isTIForAux,"0_Images", "1_T");
        }
    }

    private void copy(List<String> origins, String flightDirectory, Predicate<File> filter, String... baseDest) throws IOException {
        ArrayList<File> filesToCopy = new ArrayList<>();
        for(String absPathString : origins){
            File[] files = Paths.get(absPathString).toFile().listFiles(filter::test);
            if(files != null){
                filesToCopy.addAll(Arrays.asList(files));
            }
        }

        if(filesToCopy.isEmpty()){
            return;
        }

        String innerPath = String.join(File.separator, baseDest);

        List<FileTime> fileTimes = new ArrayList<>();

        int c = 0;
        int max = filesToCopy.size();
        for(File file : filesToCopy){
            Path source = Path.of(file.getAbsolutePath());
            Path target = Paths.get(flightDirectory, innerPath, file.getName());

            FileTime fileTime = Files.readAttributes(source, BasicFileAttributes.class).creationTime();
            fileTimes.add(fileTime);

            Files.copy(source, target, StandardCopyOption.COPY_ATTRIBUTES);
            c++;
            copyProgressListener.receivedProgress((double) c / max);
        }

        Collections.sort(fileTimes);

        this.times = new FileTime[]{fileTimes.get(0), fileTimes.get(fileTimes.size() - 1)};

    }


    public void testCopyJob() throws InterruptedException {
        double max = 100;
        for (int i = 0; i < max; i++) {
            Thread.sleep(10);
            this.copyProgressListener.receivedProgress((double) i / max);
        }
    }

    public FileTime[] getTimes() {
        return times;
    }

}
