package wue.eorc.umas.futures;

import wue.eorc.umas.controller.listeners.CopyProgressListener;
import wue.eorc.umas.enums.ImageType;
import wue.eorc.umas.enums.UAV;
import wue.eorc.umas.models.Flight;
import wue.eorc.umas.utils.ImageUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

public class FileCopier {

    CopyProgressListener copyProgressListener;

    private final CompletableFuture<Void> copyTask;

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

    public void copyM3M(Set<ImageType> imageTypes, List<String> originFlightDirs, List<String> originCalibDirs, String flightDirectory) throws IOException, InterruptedException {
        if(imageTypes.contains(ImageType.RGB)) {
            copy(originFlightDirs, flightDirectory, ImageUtils::isJPG, "0_Images", "0_RGB");
        }
        if(imageTypes.contains(ImageType.MULTISPECTRAL)) {
            copy(originFlightDirs, flightDirectory, ImageUtils::isTIF,"0_Images", "1_MS");
            copy(originCalibDirs, flightDirectory, _ignored -> true,"0_Images", "2_CALIB");
        }
    }

    private void copy(List<String> origins, String flightDirectory, Predicate<String> filter, String... baseDest) throws IOException, InterruptedException {
        ArrayList<File> filesToCopy = new ArrayList<>();
        for(String absPathString : origins){
            File[] files = Paths.get(absPathString).toFile().listFiles((_ignored, name) -> filter.test(name));
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


    public void testCopyJob() throws InterruptedException {
        double max = 1000;
        for (int i = 0; i < max; i++) {
            Thread.sleep(10);
            this.copyProgressListener.receivedProgress((double) i / max);
        }
    }

}
