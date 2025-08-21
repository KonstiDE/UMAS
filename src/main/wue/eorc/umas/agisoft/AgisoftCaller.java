package wue.eorc.umas.agisoft;

import javafx.application.Platform;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Pair;
import wue.eorc.umas.controller.listeners.AgisoftCallbackListener;
import wue.eorc.umas.controller.listeners.AgisoftQueueListener;
import wue.eorc.umas.enums.AgisoftTask;
import wue.eorc.umas.enums.Setting;
import wue.eorc.umas.enums.WorkflowType;
import wue.eorc.umas.exception.UMASException;
import wue.eorc.umas.loader.Settings;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class AgisoftCaller {

    public static Queue<Runnable> queue = new LinkedList<>();
    public static boolean isRunning = false;

    private final String snippetsPath = Path.of(Objects.requireNonNull(getClass().getClassLoader().getResource("python")).toURI()).toString();

    public AgisoftQueueListener agisoftQueueListener;
    public AgisoftCallbackListener agisoftCallbackListener;

    public AgisoftCaller(AgisoftQueueListener agisoftQueueListener, AgisoftCallbackListener agisoftCallbackListener) throws URISyntaxException {
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

        Pair<AgisoftTask, String> success = watchForSignal("vn: ", p.getInputStream(), null, AgisoftTask.CREATE_PROJECT, null);

        int exitCode = p.waitFor();

        return exitCode == 0 && Boolean.parseBoolean(success.getValue());
    }

    public String checkAgisoftVersion(String path) throws InterruptedException {
        Path pythonPath = Paths.get(path);
        Path filePath = Paths.get(snippetsPath, "version_number.py");

        try{
            ProcessBuilder pb = new ProcessBuilder(pythonPath.toFile().getAbsolutePath(), "-r", filePath.toFile().getAbsolutePath());
            pb.redirectErrorStream(true);
            Process p = pb.start();

            Pair<AgisoftTask, String> versionNumber = watchForSignal("vn:", p.getInputStream(), null, AgisoftTask.CHECK_VERSION, null);

            int exitCode = p.waitFor();

            return exitCode == 0 ? versionNumber.getValue() : null;
        }catch (IOException e){
            return null;
        }
    }

    public void checkChunk(AnchorPane anchorPane, String psxFile, String demFile, String orthoFile, String reportFile,
                           WorkflowType workflowType){

        Path pythonPath = Paths.get(Settings.getSetting(Setting.AGISOFTEXECPATH));
        Path filePath = Paths.get(snippetsPath, "check_chunk.py");

        ProcessBuilder pb = new ProcessBuilder(pythonPath.toFile().getAbsolutePath(), "-r",
                filePath.toFile().getAbsolutePath(), "-psxFile", psxFile, "-demFile", demFile, "-orthoFile", orthoFile,
                "-reportFile", reportFile, "-chunk_label", chunkLabel(workflowType));

        enqueue(AgisoftTask.CHECK_CHUNK, anchorPane, pb, true);
    }

    public void addPhotosCheck(StackPane stackPane, String psxFile, WorkflowType workflowType){
        Path pythonPath = Paths.get(Settings.getSetting(Setting.AGISOFTEXECPATH));
        Path filePath = Paths.get(snippetsPath, "add_photos_check.py");

        ProcessBuilder pb = new ProcessBuilder(pythonPath.toFile().getAbsolutePath(), "-r",
                filePath.toFile().getAbsolutePath(), "-psxFile", psxFile, "-chunk_label", chunkLabel(workflowType));

        enqueue(AgisoftTask.ADD_PHOTOS_CHECK, stackPane, pb, true);
    }

    public void addPhotos(StackPane stackPane, String psxFile, List<String> folders, WorkflowType workflowType){
        Path pythonPath = Paths.get(Settings.getSetting(Setting.AGISOFTEXECPATH));
        Path filePath = Paths.get(snippetsPath, "add_photos.py");

        ProcessBuilder pb = new ProcessBuilder(pythonPath.toFile().getAbsolutePath(), "-r", filePath.toFile().getAbsolutePath(),
                "-psxFile", psxFile, "-chunk_label", chunkLabel(workflowType), "-photo_folder", folders.size() > 1 ? String.join(",", folders) : folders.get(0));

        enqueue(AgisoftTask.ADD_PHOTOS, stackPane, pb, true);
        addPhotosCheck(stackPane, psxFile, workflowType);
    }

    public void setBrightnessCheck(StackPane stackPane, String psxFile, WorkflowType workflowType){
        Path pythonPath = Paths.get(Settings.getSetting(Setting.AGISOFTEXECPATH));
        Path filePath = Paths.get(snippetsPath, "set_brightness_check.py");

        ProcessBuilder pb = new ProcessBuilder(pythonPath.toFile().getAbsolutePath(), "-r",
                filePath.toFile().getAbsolutePath(), "-psxFile", psxFile, "-chunk_label", chunkLabel(workflowType));

        enqueue(AgisoftTask.SET_BRIGHTNESS_CHECK, stackPane, pb, true);
    }

    public void setBrightness(StackPane stackPane, String psxFile, int brightness, int contrast, WorkflowType workflowType){
        Path pythonPath = Paths.get(Settings.getSetting(Setting.AGISOFTEXECPATH));
        Path filePath = Paths.get(snippetsPath, "set_brightness.py");

        ProcessBuilder pb = new ProcessBuilder(pythonPath.toFile().getAbsolutePath(), "-r",
                filePath.toFile().getAbsolutePath(), "-psxFile", psxFile, "-brightness", String.valueOf(brightness), "-contrast", String.valueOf(contrast), "-chunk_label", chunkLabel(workflowType));

        enqueue(AgisoftTask.SET_BRIGHTNESS, stackPane, pb, false);
        setBrightnessCheck(stackPane, psxFile, workflowType);
    }

    public void alignPhotosCheck(StackPane stackPane, String psxFile, WorkflowType workflowType){
        Path pythonPath = Paths.get(Settings.getSetting(Setting.AGISOFTEXECPATH));
        Path filePath = Paths.get(snippetsPath, "align_images_check.py");

        ProcessBuilder pb = new ProcessBuilder(pythonPath.toFile().getAbsolutePath(), "-r",
                filePath.toFile().getAbsolutePath(), "-psxFile", psxFile, "-chunk_label", chunkLabel(workflowType));

        enqueue(AgisoftTask.ALIGN_IMAGES_CHECK, stackPane, pb, true);
    }

    public void alignPhotos(StackPane stackPane, String psxFile, WorkflowType workflowType, HashMap<String, String> agisoftParams){
        Path pythonPath = Paths.get(Settings.getSetting(Setting.AGISOFTEXECPATH));
        Path filePath = Paths.get(snippetsPath, "align_images.py");

        ProcessBuilder pb = new ProcessBuilder(pythonPath.toFile().getAbsolutePath(), "-r",
                filePath.toFile().getAbsolutePath(), "-psxFile", psxFile, "-chunk_label", chunkLabel(workflowType));

        enqueue(AgisoftTask.ALIGN_IMAGES, stackPane, pb, false);
        alignPhotosCheck(stackPane, psxFile, workflowType);
    }

    public void optimizeCamerasCheck(StackPane stackPane, String psxFile, WorkflowType workflowType){
        Path pythonPath = Paths.get(Settings.getSetting(Setting.AGISOFTEXECPATH));
        Path filePath = Paths.get(snippetsPath, "optimize_cameras_check.py");

        ProcessBuilder pb = new ProcessBuilder(pythonPath.toFile().getAbsolutePath(), "-r",
                filePath.toFile().getAbsolutePath(), "-psxFile", psxFile, "-chunk_label", chunkLabel(workflowType));

        enqueue(AgisoftTask.OPTIMIZE_CAMERAS_CHECK, stackPane, pb, true);
    }

    public void optimizeCameras(StackPane stackPane, String psxFile, WorkflowType workflowType){
        Path pythonPath = Paths.get(Settings.getSetting(Setting.AGISOFTEXECPATH));
        Path filePath = Paths.get(snippetsPath, "optimize_cameras.py");

        ProcessBuilder pb = new ProcessBuilder(pythonPath.toFile().getAbsolutePath(), "-r",
                filePath.toFile().getAbsolutePath(), "-psxFile", psxFile, "-chunk_label", chunkLabel(workflowType));

        enqueue(AgisoftTask.OPTIMIZE_CAMERAS, stackPane, pb, false);
        optimizeCamerasCheck(stackPane, psxFile, workflowType);
    }

    public void buildPointCloudCheck(StackPane stackPane, String psxFile, WorkflowType workflowType){
        Path pythonPath = Paths.get(Settings.getSetting(Setting.AGISOFTEXECPATH));
        Path filePath = Paths.get(snippetsPath, "build_point_cloud_check.py");

        ProcessBuilder pb = new ProcessBuilder(pythonPath.toFile().getAbsolutePath(), "-r",
                filePath.toFile().getAbsolutePath(), "-psxFile", psxFile, "-chunk_label", chunkLabel(workflowType));

        enqueue(AgisoftTask.BUILD_POINT_CLOUD_CHECK, stackPane, pb, true);
    }

    public void buildPointCloud(StackPane stackPane, String psxFile, WorkflowType workflowType){
        Path pythonPath = Paths.get(Settings.getSetting(Setting.AGISOFTEXECPATH));
        Path filePath = Paths.get(snippetsPath, "build_point_cloud.py");

        ProcessBuilder pb = new ProcessBuilder(pythonPath.toFile().getAbsolutePath(), "-r",
                filePath.toFile().getAbsolutePath(), "-psxFile", psxFile, "-chunk_label", chunkLabel(workflowType));

        enqueue(AgisoftTask.BUILD_POINT_CLOUD, stackPane, pb, false);
        buildPointCloudCheck(stackPane, psxFile, workflowType);
    }

    public void buildDemCheck(StackPane stackPane, String psxFile, WorkflowType workflowType){
        Path pythonPath = Paths.get(Settings.getSetting(Setting.AGISOFTEXECPATH));
        Path filePath = Paths.get(snippetsPath, "build_dem_check.py");

        ProcessBuilder pb = new ProcessBuilder(pythonPath.toFile().getAbsolutePath(), "-r",
                filePath.toFile().getAbsolutePath(), "-psxFile", psxFile, "-chunk_label", chunkLabel(workflowType));

        enqueue(AgisoftTask.BUILD_DEM_CHECK, stackPane, pb, true);
    }

    public void buildDem(StackPane stackPane, String psxFile, WorkflowType workflowType){
        Path pythonPath = Paths.get(Settings.getSetting(Setting.AGISOFTEXECPATH));
        Path filePath = Paths.get(snippetsPath, "build_dem.py");

        ProcessBuilder pb = new ProcessBuilder(pythonPath.toFile().getAbsolutePath(), "-r",
                filePath.toFile().getAbsolutePath(), "-psxFile", psxFile, "-chunk_label", chunkLabel(workflowType));

        enqueue(AgisoftTask.BUILD_DEM, stackPane, pb, false);
        buildDemCheck(stackPane, psxFile, workflowType);
    }

    public void buildOrthomosaicCheck(StackPane stackPane, String psxFile, WorkflowType workflowType){
        Path pythonPath = Paths.get(Settings.getSetting(Setting.AGISOFTEXECPATH));
        Path filePath = Paths.get(snippetsPath, "build_orthomosaic_check.py");

        ProcessBuilder pb = new ProcessBuilder(pythonPath.toFile().getAbsolutePath(), "-r",
                filePath.toFile().getAbsolutePath(), "-psxFile", psxFile, "-chunk_label", chunkLabel(workflowType));

        enqueue(AgisoftTask.BUILD_ORTHOMOSAIC_CHECK, stackPane, pb, true);
    }

    public void buildOrthomosaic(StackPane stackPane, String psxFile, WorkflowType workflowType){
        Path pythonPath = Paths.get(Settings.getSetting(Setting.AGISOFTEXECPATH));
        Path filePath = Paths.get(snippetsPath, "build_orthomosaic.py");

        ProcessBuilder pb = new ProcessBuilder(pythonPath.toFile().getAbsolutePath(), "-r",
                filePath.toFile().getAbsolutePath(), "-psxFile", psxFile, "-chunk_label", chunkLabel(workflowType));

        enqueue(AgisoftTask.BUILD_ORTHOMOSAIC, stackPane, pb, false);
        buildOrthomosaicCheck(stackPane, psxFile, workflowType);
    }

    public void exportDemCheck(StackPane stackPane, String psxFile, String targetFile, WorkflowType workflowType){
        Path pythonPath = Paths.get(Settings.getSetting(Setting.AGISOFTEXECPATH));
        Path filePath = Paths.get(snippetsPath, "export_dem_check.py");

        ProcessBuilder pb = new ProcessBuilder(pythonPath.toFile().getAbsolutePath(), "-r",
                filePath.toFile().getAbsolutePath(), "-psxFile", psxFile, "-demFile", targetFile,
                "-chunk_label", chunkLabel(workflowType));

        enqueue(AgisoftTask.EXPORT_DEM_CHECK, stackPane, pb, true);

    }
    public void exportDem(StackPane stackPane, String psxFile, String targetFile, WorkflowType workflowType){
        Path pythonPath = Paths.get(Settings.getSetting(Setting.AGISOFTEXECPATH));
        Path filePath = Paths.get(snippetsPath, "export_dem.py");

        ProcessBuilder pb = new ProcessBuilder(pythonPath.toFile().getAbsolutePath(), "-r",
                filePath.toFile().getAbsolutePath(), "-psxFile", psxFile, "-demFile", targetFile,
                "-chunk_label", chunkLabel(workflowType));

        enqueue(AgisoftTask.EXPORT_DEM, stackPane, pb, false);
        exportDemCheck(stackPane, psxFile, targetFile, workflowType);
    }

    public void exportOrthoCheck(StackPane stackPane, String psxFile, String targetFile, WorkflowType workflowType){
        Path pythonPath = Paths.get(Settings.getSetting(Setting.AGISOFTEXECPATH));
        Path filePath = Paths.get(snippetsPath, "export_orthomosaic_check.py");

        ProcessBuilder pb = new ProcessBuilder(pythonPath.toFile().getAbsolutePath(), "-r",
                filePath.toFile().getAbsolutePath(), "-psxFile", psxFile, "-orthoFile", targetFile,
                "-chunk_label", chunkLabel(workflowType));

        enqueue(AgisoftTask.EXPORT_ORTHOMOSAIC_CHECK, stackPane, pb, true);

    }
    public void exportOrtho(StackPane stackPane, String psxFile, String targetFile, WorkflowType workflowType){
        Path pythonPath = Paths.get(Settings.getSetting(Setting.AGISOFTEXECPATH));
        Path filePath = Paths.get(snippetsPath, "export_orthomosaic.py");

        ProcessBuilder pb = new ProcessBuilder(pythonPath.toFile().getAbsolutePath(), "-r",
                filePath.toFile().getAbsolutePath(), "-psxFile", psxFile, "-orthoFile", targetFile,
                "-chunk_label", chunkLabel(workflowType));

        enqueue(AgisoftTask.EXPORT_ORTHOMOSAIC, stackPane, pb, false);
        exportOrthoCheck(stackPane, psxFile, targetFile, workflowType);
    }

    public void generateReportCheck(StackPane stackPane, String psxFile, String targetFile, WorkflowType workflowType){
        Path pythonPath = Paths.get(Settings.getSetting(Setting.AGISOFTEXECPATH));
        Path filePath = Paths.get(snippetsPath, "generate_report_check.py");

        ProcessBuilder pb = new ProcessBuilder(pythonPath.toFile().getAbsolutePath(), "-r",
                filePath.toFile().getAbsolutePath(), "-psxFile", psxFile, "-reportFile", targetFile,
                "-chunk_label", chunkLabel(workflowType));

        enqueue(AgisoftTask.GENERATE_REPORT_CHECK, stackPane, pb, true);

    }
    public void generateReport(StackPane stackPane, String psxFile, String targetFile, String flightName, String description, WorkflowType workflowType){
        Path pythonPath = Paths.get(Settings.getSetting(Setting.AGISOFTEXECPATH));
        Path filePath = Paths.get(snippetsPath, "generate_report.py");

        ProcessBuilder pb = new ProcessBuilder(pythonPath.toFile().getAbsolutePath(), "-r",
                filePath.toFile().getAbsolutePath(), "-psxFile", psxFile, "-reportFile", targetFile,
                "-flightName", flightName, "-description", description, "-chunk_label", chunkLabel(workflowType));

        enqueue(AgisoftTask.GENERATE_REPORT, stackPane, pb, false);
        generateReportCheck(stackPane, psxFile, targetFile, workflowType);

    }

    public void completeBuildRGB(List<StackPane> stackPanes, List<String> folders, int brightness, int contrast,
                                 String psxFile, String demFile, String orthoFile, String reportFile,
                                 String flightName, String reportDescription){

        // addPhotos(stackPanes.get(0), psxFile, folders);
        // setBrightness(stackPanes.get(1), psxFile, brightness, contrast);
        // alignPhotos(stackPanes.get(2), psxFile);
        // optimizeCameras(stackPanes.get(3), psxFile);
        // buildPointCloudCheck(stackPanes.get(4), psxFile);
        // buildDemCheck(stackPanes.get(5), psxFile);
        // buildOrthomosaic(stackPanes.get(6), psxFile);
        // exportDem(stackPanes.get(7), psxFile, demFile);
        // exportOrtho(stackPanes.get(8), psxFile, orthoFile);
        // generateReport(stackPanes.get(9), psxFile, reportFile, flightName, reportDescription);

    }


    private void enqueue(AgisoftTask task, Pane pane, ProcessBuilder pb, boolean nextIfFailed){
        this.agisoftQueueListener.enqueue(task);

        queue.add(() -> CompletableFuture.supplyAsync(() -> {
            this.agisoftQueueListener.started(task);
            try{
                pb.redirectErrorStream(true);
                Process p = pb.start();

                Pair<AgisoftTask, String> success = watchForSignal("vn:", p.getInputStream(), agisoftCallbackListener, task, pane);

                int exitCode = p.waitFor();

                if (exitCode != 0) return new Pair<>(AgisoftTask.UNDEFINED, "False");

                return success;
            }catch (IOException | InterruptedException e){
                return new Pair<>(AgisoftTask.UNDEFINED, "False");
            }
        }) .thenAcceptAsync(result -> {
            boolean success = Boolean.parseBoolean(result.getValue());

            try {
                agisoftCallbackListener.callback(pane, result.getKey(), success);
            } catch (UMASException e) {
                throw new RuntimeException(e);
            }

            agisoftQueueListener.finish();

            if(success) {
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
    public static Pair<AgisoftTask, String> watchForSignal(String signalKey, InputStream inputStream, AgisoftCallbackListener listener, AgisoftTask task, Pane pane) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                if(listener != null && line.startsWith("vp: ") || line.startsWith("xvp: ")) {
                    String finalLine = line;
                    Platform.runLater(() -> {
                        try{
                            listener.progress(Float.parseFloat(finalLine.substring(4)));
                        } catch (NumberFormatException ignored) {  }
                    });
                }
                if(line.startsWith(signalKey)){
                    String[] split = line.split(":");

                    AgisoftTask currentTask = AgisoftTask.valueOf(split[1]);

                    if(currentTask == task){
                        if(listener != null)
                            Platform.runLater(() -> listener.progress(0));

                        return new Pair<>(currentTask, split[2]);
                    }else{
                        if(listener != null && pane != null)
                            listener.callback(pane, currentTask, Boolean.parseBoolean(split[2]));
                    }
                }
            }
        } catch (UMASException e) {
            throw new RuntimeException(e);
        }
        return new Pair<>(AgisoftTask.UNDEFINED, Boolean.FALSE.toString());
    }

    private static synchronized void processNext() {
        Runnable nextTask = queue.poll();
        if (nextTask != null) {
            nextTask.run();
        } else {
            isRunning = false;
        }
    }

    private String chunkLabel(WorkflowType workflowType){
        return workflowType.name();
    }

}
