package wue.eorc.umas.agisoft;

import javafx.application.Platform;
import javafx.scene.layout.StackPane;
import wue.eorc.umas.controller.listeners.AgisoftCallbackListener;
import wue.eorc.umas.controller.listeners.AgisoftQueueListener;
import wue.eorc.umas.enums.AgisoftTask;
import wue.eorc.umas.enums.Setting;
import wue.eorc.umas.exception.UMASException;
import wue.eorc.umas.loader.Settings;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;

public class AgisoftCaller {

    public static Queue<Runnable> queue = new LinkedList<>();
    public static boolean isRunning = false;

    private final String snippetsPath = Objects.requireNonNull(getClass().getClassLoader().getResource("python")).getPath();

    public AgisoftQueueListener agisoftQueueListener;
    public AgisoftCallbackListener agisoftCallbackListener;

    public AgisoftCaller(AgisoftQueueListener agisoftQueueListener, AgisoftCallbackListener agisoftCallbackListener) {
        this.agisoftQueueListener = agisoftQueueListener;
        this.agisoftCallbackListener = agisoftCallbackListener;
    }

    public boolean createProject(String psxFilePath) throws IOException, InterruptedException {
        Path pythonPath = Paths.get(Settings.getSetting(Setting.AGISOFTEXECPATH));
        Path filePath = Paths.get(snippetsPath, "create_project.py");

        ProcessBuilder pb = new ProcessBuilder(pythonPath.toFile().getAbsolutePath(), "-r",
                filePath.toFile().getAbsolutePath(), "-psxFile", psxFilePath);
        pb.redirectErrorStream(true);
        Process p = pb.start();

        boolean success = Boolean.parseBoolean(watchForSignal("vn: ", p.getInputStream(), null));

        int exitCode = p.waitFor();

        return exitCode == 0 && success;
    }

    public String checkAgisoftVersion(String path) throws InterruptedException {
        Path pythonPath = Paths.get(path);
        Path filePath = Paths.get(snippetsPath, "version_number.py");

        try{
            ProcessBuilder pb = new ProcessBuilder(pythonPath.toFile().getAbsolutePath(), "-r", filePath.toFile().getAbsolutePath());
            pb.redirectErrorStream(true);
            Process p = pb.start();

            String versionNumber = watchForSignal("vn: ", p.getInputStream(), null);

            int exitCode = p.waitFor();

            return exitCode == 0 ? versionNumber : null;
        }catch (IOException e){
            return null;
        }
    }

    public void addPhotosCheck(StackPane stackPane, String psxFile){
        Path pythonPath = Paths.get(Settings.getSetting(Setting.AGISOFTEXECPATH));
        Path filePath = Paths.get(snippetsPath, "add_photos_check.py");

        ProcessBuilder pb = new ProcessBuilder(pythonPath.toFile().getAbsolutePath(), "-r",
                filePath.toFile().getAbsolutePath(), "-psxFile", psxFile);

        enqueue(AgisoftTask.ADD_PHOTOS_CHECK, stackPane, pb, true);
    }

    public void addPhotos(StackPane stackPane, String psxFile, List<String> folders){
        Path pythonPath = Paths.get(Settings.getSetting(Setting.AGISOFTEXECPATH));
        Path filePath = Paths.get(snippetsPath, "add_photos.py");

        ProcessBuilder pb = new ProcessBuilder(pythonPath.toFile().getAbsolutePath(), "-r", filePath.toFile().getAbsolutePath(),
                "-psxFile", psxFile, "-photo_folder", folders.size() > 1 ? String.join(",", folders) : folders.get(0));

        enqueue(AgisoftTask.ADD_PHOTOS, stackPane, pb, false);
        addPhotosCheck(stackPane, psxFile);
    }

    public void setBrightnessCheck(StackPane stackPane, String psxFile){
        Path pythonPath = Paths.get(Settings.getSetting(Setting.AGISOFTEXECPATH));
        Path filePath = Paths.get(snippetsPath, "set_brightness_check.py");

        ProcessBuilder pb = new ProcessBuilder(pythonPath.toFile().getAbsolutePath(), "-r",
                filePath.toFile().getAbsolutePath(), "-psxFile", psxFile);

        enqueue(AgisoftTask.SET_BRIGHTNESS_CHECK, stackPane, pb, true);
    }

    public void setBrightness(StackPane stackPane, String psxFile, int brightness, int contrast){
        Path pythonPath = Paths.get(Settings.getSetting(Setting.AGISOFTEXECPATH));
        Path filePath = Paths.get(snippetsPath, "set_brightness.py");

        ProcessBuilder pb = new ProcessBuilder(pythonPath.toFile().getAbsolutePath(), "-r",
                filePath.toFile().getAbsolutePath(), "-psxFile", psxFile, "-brightness", String.valueOf(brightness), "-contrast", String.valueOf(contrast));

        enqueue(AgisoftTask.SET_BRIGHTNESS, stackPane, pb, false);
        setBrightnessCheck(stackPane, psxFile);
    }

    public void alignPhotosCheck(StackPane stackPane, String psxFile){
        Path pythonPath = Paths.get(Settings.getSetting(Setting.AGISOFTEXECPATH));
        Path filePath = Paths.get(snippetsPath, "align_photos_check.py");

        ProcessBuilder pb = new ProcessBuilder(pythonPath.toFile().getAbsolutePath(), "-r",
                filePath.toFile().getAbsolutePath(), "-psxFile", psxFile);

        enqueue(AgisoftTask.ALIGN_IMAGES_CHECK, stackPane, pb, true);
    }

    public void alignPhotos(StackPane stackPane, String psxFile){
        Path pythonPath = Paths.get(Settings.getSetting(Setting.AGISOFTEXECPATH));
        Path filePath = Paths.get(snippetsPath, "align_photos.py");

        ProcessBuilder pb = new ProcessBuilder(pythonPath.toFile().getAbsolutePath(), "-r",
                filePath.toFile().getAbsolutePath(), "-psxFile", psxFile);

        enqueue(AgisoftTask.ALIGN_IMAGES, stackPane, pb, false);
        alignPhotosCheck(stackPane, psxFile);
    }

    public void optimizeCamerasCheck(StackPane stackPane, String psxFile){
        Path pythonPath = Paths.get(Settings.getSetting(Setting.AGISOFTEXECPATH));
        Path filePath = Paths.get(snippetsPath, "optimize_cameras_check.py");

        ProcessBuilder pb = new ProcessBuilder(pythonPath.toFile().getAbsolutePath(), "-r",
                filePath.toFile().getAbsolutePath(), "-psxFile", psxFile);

        enqueue(AgisoftTask.OPTIMIZE_CAMERAS_CHECK, stackPane, pb, true);
    }

    public void optimizeCameras(StackPane stackPane, String psxFile){
        Path pythonPath = Paths.get(Settings.getSetting(Setting.AGISOFTEXECPATH));
        Path filePath = Paths.get(snippetsPath, "optimize_cameras.py");

        ProcessBuilder pb = new ProcessBuilder(pythonPath.toFile().getAbsolutePath(), "-r",
                filePath.toFile().getAbsolutePath(), "-psxFile", psxFile);

        enqueue(AgisoftTask.OPTIMIZE_CAMERAS, stackPane, pb, false);
        optimizeCamerasCheck(stackPane, psxFile);
    }

    public void buildPointCloudCheck(StackPane stackPane, String psxFile){
        Path pythonPath = Paths.get(Settings.getSetting(Setting.AGISOFTEXECPATH));
        Path filePath = Paths.get(snippetsPath, "build_point_cloud_check.py");

        ProcessBuilder pb = new ProcessBuilder(pythonPath.toFile().getAbsolutePath(), "-r",
                filePath.toFile().getAbsolutePath(), "-psxFile", psxFile);

        enqueue(AgisoftTask.BUILD_POINT_CLOUD_CHECK, stackPane, pb, true);
    }

    public void buildPointCloud(StackPane stackPane, String psxFile){
        Path pythonPath = Paths.get(Settings.getSetting(Setting.AGISOFTEXECPATH));
        Path filePath = Paths.get(snippetsPath, "build_point_cloud.py");

        ProcessBuilder pb = new ProcessBuilder(pythonPath.toFile().getAbsolutePath(), "-r",
                filePath.toFile().getAbsolutePath(), "-psxFile", psxFile);

        enqueue(AgisoftTask.BUILD_POINT_CLOUD, stackPane, pb, false);
        buildPointCloudCheck(stackPane, psxFile);
    }

    public void buildDemCheck(StackPane stackPane, String psxFile){
        Path pythonPath = Paths.get(Settings.getSetting(Setting.AGISOFTEXECPATH));
        Path filePath = Paths.get(snippetsPath, "build_dem_check.py");

        ProcessBuilder pb = new ProcessBuilder(pythonPath.toFile().getAbsolutePath(), "-r",
                filePath.toFile().getAbsolutePath(), "-psxFile", psxFile);

        enqueue(AgisoftTask.BUILD_DEM_CHECK, stackPane, pb, true);
    }

    public void buildDem(StackPane stackPane, String psxFile){
        Path pythonPath = Paths.get(Settings.getSetting(Setting.AGISOFTEXECPATH));
        Path filePath = Paths.get(snippetsPath, "build_dem.py");

        ProcessBuilder pb = new ProcessBuilder(pythonPath.toFile().getAbsolutePath(), "-r",
                filePath.toFile().getAbsolutePath(), "-psxFile", psxFile);

        enqueue(AgisoftTask.BUILD_DEM, stackPane, pb, false);
        buildDemCheck(stackPane, psxFile);
    }

    public void buildOrthomosaicCheck(StackPane stackPane, String psxFile){
        Path pythonPath = Paths.get(Settings.getSetting(Setting.AGISOFTEXECPATH));
        Path filePath = Paths.get(snippetsPath, "build_orthomosaic_check.py");

        ProcessBuilder pb = new ProcessBuilder(pythonPath.toFile().getAbsolutePath(), "-r",
                filePath.toFile().getAbsolutePath(), "-psxFile", psxFile);

        enqueue(AgisoftTask.BUILD_ORTHOMOSAIC_CHECK, stackPane, pb, true);
    }

    public void buildOrthomosaic(StackPane stackPane, String psxFile){
        Path pythonPath = Paths.get(Settings.getSetting(Setting.AGISOFTEXECPATH));
        Path filePath = Paths.get(snippetsPath, "build_orthomosaic.py");

        ProcessBuilder pb = new ProcessBuilder(pythonPath.toFile().getAbsolutePath(), "-r",
                filePath.toFile().getAbsolutePath(), "-psxFile", psxFile);

        enqueue(AgisoftTask.BUILD_ORTHOMOSAIC, stackPane, pb, false);
        buildOrthomosaicCheck(stackPane, psxFile);
    }

    public void exportDemCheck(StackPane stackPane, String psxFile, String targetFile){
        Path pythonPath = Paths.get(Settings.getSetting(Setting.AGISOFTEXECPATH));
        Path filePath = Paths.get(snippetsPath, "export_dem_check.py");

        ProcessBuilder pb = new ProcessBuilder(pythonPath.toFile().getAbsolutePath(), "-r",
                filePath.toFile().getAbsolutePath(), "-psxFile", psxFile, "-demFile", targetFile);

        enqueue(AgisoftTask.EXPORT_DEM_CHECK, stackPane, pb, true);

    }
    public void exportDem(StackPane stackPane, String psxFile, String targetFile){
        Path pythonPath = Paths.get(Settings.getSetting(Setting.AGISOFTEXECPATH));
        Path filePath = Paths.get(snippetsPath, "export_dem.py");

        ProcessBuilder pb = new ProcessBuilder(pythonPath.toFile().getAbsolutePath(), "-r",
                filePath.toFile().getAbsolutePath(), "-psxFile", psxFile, "-demFile", targetFile);

        enqueue(AgisoftTask.EXPORT_DEM, stackPane, pb, false);
        exportDemCheck(stackPane, psxFile, targetFile);
    }

    public void exportOrthoCheck(StackPane stackPane, String psxFile, String targetFile){
        Path pythonPath = Paths.get(Settings.getSetting(Setting.AGISOFTEXECPATH));
        Path filePath = Paths.get(snippetsPath, "export_orthomosaic_check.py");

        ProcessBuilder pb = new ProcessBuilder(pythonPath.toFile().getAbsolutePath(), "-r",
                filePath.toFile().getAbsolutePath(), "-psxFile", psxFile, "-orthoFile", targetFile);

        enqueue(AgisoftTask.EXPORT_ORTHOMOSAIC_CHECK, stackPane, pb, true);

    }
    public void exportOrtho(StackPane stackPane, String psxFile, String targetFile){
        Path pythonPath = Paths.get(Settings.getSetting(Setting.AGISOFTEXECPATH));
        Path filePath = Paths.get(snippetsPath, "export_orthomosaic.py");

        ProcessBuilder pb = new ProcessBuilder(pythonPath.toFile().getAbsolutePath(), "-r",
                filePath.toFile().getAbsolutePath(), "-psxFile", psxFile, "-orthoFile", targetFile);

        enqueue(AgisoftTask.EXPORT_ORTHOMOSAIC, stackPane, pb, false);
        exportOrthoCheck(stackPane, psxFile, targetFile);
    }

    public void generateReportCheck(StackPane stackPane, String psxFile, String targetFile){
        Path pythonPath = Paths.get(Settings.getSetting(Setting.AGISOFTEXECPATH));
        Path filePath = Paths.get(snippetsPath, "generate_report_check.py");

        ProcessBuilder pb = new ProcessBuilder(pythonPath.toFile().getAbsolutePath(), "-r",
                filePath.toFile().getAbsolutePath(), "-psxFile", psxFile, "-reportFile", targetFile);

        enqueue(AgisoftTask.GENERATE_REPORT_CHECK, stackPane, pb, true);

    }
    public void generateReport(StackPane stackPane, String psxFile, String targetFile, String flightName, String description){
        Path pythonPath = Paths.get(Settings.getSetting(Setting.AGISOFTEXECPATH));
        Path filePath = Paths.get(snippetsPath, "generate_report.py");

        ProcessBuilder pb = new ProcessBuilder(pythonPath.toFile().getAbsolutePath(), "-r",
                filePath.toFile().getAbsolutePath(), "-psxFile", psxFile, "-reportFile", targetFile, "-flightName", flightName, "-description", description);

        enqueue(AgisoftTask.GENERATE_REPORT, stackPane, pb, false);
        generateReportCheck(stackPane, psxFile, targetFile);

    }

    public void completeBuildRGB(List<StackPane> stackPanes, List<String> folders, int brightness, int contrast,
                                 String psxFile, String demFile, String orthoFile, String reportFile,
                                 String flightName, String reportDescription){

        addPhotos(stackPanes.get(0), psxFile, folders);
        setBrightness(stackPanes.get(1), psxFile, brightness, contrast);
        alignPhotos(stackPanes.get(2), psxFile);
        optimizeCameras(stackPanes.get(3), psxFile);
        buildPointCloudCheck(stackPanes.get(4), psxFile);
        buildDemCheck(stackPanes.get(5), psxFile);
        buildOrthomosaic(stackPanes.get(6), psxFile);
        exportDem(stackPanes.get(7), psxFile, demFile);
        exportOrtho(stackPanes.get(8), psxFile, orthoFile);
        generateReport(stackPanes.get(9), psxFile, reportFile, flightName, reportDescription);

    }


    private void enqueue(AgisoftTask task, StackPane stackPane, ProcessBuilder pb, boolean nextIfFailed){
        this.agisoftQueueListener.enqueue(task);

        queue.add(() -> CompletableFuture.supplyAsync(() -> {
            this.agisoftQueueListener.started(task);
            try{
                pb.redirectErrorStream(true);
                Process p = pb.start();

                boolean success = Boolean.parseBoolean(watchForSignal("vn: ", p.getInputStream(), agisoftCallbackListener));

                int exitCode = p.waitFor();

                return exitCode == 0 && success;
            }catch (IOException | InterruptedException e){
                return false;
            }
        }) .thenAcceptAsync(result -> {
            try {
                agisoftCallbackListener.callback(stackPane, task, result);
            } catch (UMASException e) {
                throw new RuntimeException(e);
            }

            agisoftQueueListener.finish();

            if(result) {
                isRunning = true;
                processNext();
            }else{
                if(nextIfFailed) {
                    isRunning = true;
                    processNext();
                }else{
                    isRunning = false;
                }
            }

        }));

        if (!isRunning) {
            isRunning = true;
            processNext();
        }
    }

    // !!!Signal key must be 4 chars long!!!
    public static String watchForSignal(String signalKey, InputStream inputStream, AgisoftCallbackListener listener) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                if(listener != null && line.contains("vp: ")) {
                    String finalLine = line;
                    Platform.runLater(() -> {
                        try{
                            listener.progress(Float.parseFloat(finalLine.substring(4)));
                        } catch (NumberFormatException ignored) {  }
                    });
                }
                if(line.startsWith(signalKey)){
                    if(listener != null) Platform.runLater(() -> listener.progress(0));
                    return line.substring(4);
                }
            }
        }
        return null;
    }

    private static synchronized void processNext() {
        Runnable nextTask = queue.poll();
        if (nextTask != null) {
            nextTask.run();
        } else {
            isRunning = false;
        }
    }

}
