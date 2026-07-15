package wue.eorc.umas.agisoft;

import javafx.application.Platform;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Pair;
import wue.eorc.umas.controller.customs.UMASDialog;
import wue.eorc.umas.controller.listeners.CallbackListener;
import wue.eorc.umas.controller.listeners.QueueListener;
import wue.eorc.umas.controller.scenes.main.DisplayController;
import wue.eorc.umas.controller.scenes.main.StatusController;
import wue.eorc.umas.controller.scenes.views.dialogs.agisoft.AgisoftErrorController;
import wue.eorc.umas.enums.agisoft.AgisoftParameter;
import wue.eorc.umas.enums.agisoft.AgisoftTask;
import wue.eorc.umas.enums.Setting;
import wue.eorc.umas.enums.WorkflowType;
import wue.eorc.umas.exception.UMASException;
import wue.eorc.umas.loader.Settings;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class AgisoftCaller {

    private final String snippetsPath = new File(Objects.requireNonNull(
            getClass().getClassLoader().getResource("python")).getFile()).getAbsolutePath();

    public final QueueListener queueListener;
    public final CallbackListener callbackListener;

    public final DisplayController display;

    public AgisoftCaller(CallbackListener callbackListener, DisplayController display){
        this.queueListener = display.getRootController().getStatusController();
        this.callbackListener = callbackListener;
        this.display = display;
    }

    public boolean createProject(String psxFilePath) throws IOException, InterruptedException {
        Path pythonPath = Paths.get(Settings.getSetting(Setting.AGISOFT_EXEC_PATH));
        Path filePath = Paths.get(snippetsPath, "create_project.py");

        ProcessBuilder pb = new ProcessBuilder(pythonPath.toFile().getAbsolutePath(), "-r",
                filePath.toFile().getAbsolutePath(), "-psxFile", psxFilePath);
        pb.redirectErrorStream(true);
        Process p = pb.start();

        Pair<AgisoftTask, String> success = watchForSignal("vn: ", p.getInputStream(), null, null, AgisoftTask.CREATE_PROJECT, null);

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

            Pair<AgisoftTask, String> versionNumber = watchForSignal("vn:", p.getInputStream(), null, null, AgisoftTask.CHECK_VERSION, null);

            int exitCode = p.waitFor();

            return exitCode == 0 ? versionNumber.getValue() : null;
        }catch (IOException e){
            return null;
        }
    }

    public void checkChunk(AnchorPane anchorPane, String psxFile, String demFile, String orthoFile, String reportFile,
                           WorkflowType workflowType){

        Path pythonPath = Paths.get(Settings.getSetting(Setting.AGISOFT_EXEC_PATH));
        Path filePath = Paths.get(snippetsPath, "check_chunk.py");

        ProcessBuilder pb = new ProcessBuilder(pythonPath.toFile().getAbsolutePath(), "-r",
                filePath.toFile().getAbsolutePath(), "-psxFile", psxFile, "-demFile", demFile, "-orthoFile", orthoFile,
                "-reportFile", reportFile, "-chunk_label", chunkLabel(workflowType));

        enqueue(workflowType, AgisoftTask.CHECK_CHUNK, anchorPane, pb, true);
    }

    public void addPhotosCheck(StackPane stackPane, String psxFile, WorkflowType workflowType){
        Path pythonPath = Paths.get(Settings.getSetting(Setting.AGISOFT_EXEC_PATH));
        Path filePath = Paths.get(snippetsPath, "add_photos_check.py");

        ProcessBuilder pb = new ProcessBuilder(pythonPath.toFile().getAbsolutePath(), "-r",
                filePath.toFile().getAbsolutePath(), "-psxFile", psxFile, "-chunk_label", chunkLabel(workflowType));

        enqueue(workflowType, AgisoftTask.ADD_PHOTOS_CHECK, stackPane, pb, true);
    }

    public void addPhotos(StackPane stackPane, String psxFile, List<String> flightFolders, List<String> calibFolders, WorkflowType workflowType, boolean batch){
        Path pythonPath = Paths.get(Settings.getSetting(Setting.AGISOFT_EXEC_PATH));
        Path filePath = Paths.get(snippetsPath, "add_photos.py");

        ProcessBuilder pb = new ProcessBuilder(pythonPath.toFile().getAbsolutePath(), "-r", filePath.toFile().getAbsolutePath(),
                "-psxFile", psxFile, "-chunk_label", chunkLabel(workflowType),
                "-photo_folders", flightFolders.size() > 1 ? String.join(",", flightFolders) : flightFolders.get(0),
                "-calib_folders", calibFolders.size() > 1 ? String.join(",", calibFolders) : calibFolders.isEmpty() ? "" : calibFolders.get(0),
                "-batch", "" + batch);

        enqueue(workflowType, AgisoftTask.ADD_PHOTOS, stackPane, pb, true);
        addPhotosCheck(stackPane, psxFile, workflowType);
    }

    public void setBrightnessCheck(StackPane stackPane, String psxFile, WorkflowType workflowType){
        Path pythonPath = Paths.get(Settings.getSetting(Setting.AGISOFT_EXEC_PATH));
        Path filePath = Paths.get(snippetsPath, "set_brightness_check.py");

        ProcessBuilder pb = new ProcessBuilder(pythonPath.toFile().getAbsolutePath(), "-r",
                filePath.toFile().getAbsolutePath(), "-psxFile", psxFile, "-chunk_label", chunkLabel(workflowType));

        enqueue(workflowType, AgisoftTask.SET_BRIGHTNESS_CHECK, stackPane, pb, true);
    }

    public void setBrightness(StackPane stackPane, String psxFile, WorkflowType workflowType, HashMap<String, String> agisoftParams){
        Path pythonPath = Paths.get(Settings.getSetting(Setting.AGISOFT_EXEC_PATH));
        Path filePath = Paths.get(snippetsPath, "set_brightness.py");

        ProcessBuilder pb = new ProcessBuilder(pythonPath.toFile().getAbsolutePath(), "-r",
                filePath.toFile().getAbsolutePath(), "-psxFile", psxFile, "-chunk_label", chunkLabel(workflowType));
        extendProcessBuilder(pb, agisoftParams);

        enqueue(workflowType, AgisoftTask.SET_BRIGHTNESS, stackPane, pb, false);
        setBrightnessCheck(stackPane, psxFile, workflowType);
    }

    public void setBrightnessEstimate(Pane pane, String psxFile, WorkflowType workflowType){
        Path pythonPath = Paths.get(Settings.getSetting(Setting.AGISOFT_EXEC_PATH));
        Path filePath = Paths.get(snippetsPath, "set_brightness_estimate.py");

        ProcessBuilder pb = new ProcessBuilder(pythonPath.toFile().getAbsolutePath(), "-r",
                filePath.toFile().getAbsolutePath(), "-psxFile", psxFile, "-chunk_label", chunkLabel(workflowType));

        enqueue(workflowType, AgisoftTask.SET_BRIGHTNESS_ESTIMATE, pane, pb, false);
    }

    public void calibrateReflectanceCheck(StackPane stackPane, String psxFile, WorkflowType workflowType){
        Path pythonPath = Paths.get(Settings.getSetting(Setting.AGISOFT_EXEC_PATH));
        Path filePath = Paths.get(snippetsPath, "calibrate_reflectance_check.py");

        ProcessBuilder pb = new ProcessBuilder(pythonPath.toFile().getAbsolutePath(), "-r",
                filePath.toFile().getAbsolutePath(), "-psxFile", psxFile, "-chunk_label", chunkLabel(workflowType));

        enqueue(workflowType, AgisoftTask.CALIBRATE_REFLECTANCE_CHECK, stackPane, pb, true);
    }

    public void calibrateReflectance(StackPane stackPane, String psxFile, WorkflowType workflowType, HashMap<String, String> agisoftParams, boolean batch){
        Path pythonPath = Paths.get(Settings.getSetting(Setting.AGISOFT_EXEC_PATH));
        Path filePath = Paths.get(snippetsPath, "calibrate_reflectance.py");

        ProcessBuilder pb = new ProcessBuilder(pythonPath.toFile().getAbsolutePath(), "-r",
                filePath.toFile().getAbsolutePath(), "-psxFile", psxFile, "-chunk_label", chunkLabel(workflowType),
                "-batch", "" + batch);
        extendProcessBuilder(pb, agisoftParams);

        enqueue(workflowType, AgisoftTask.CALIBRATE_REFLECTANCE, stackPane, pb, false);
        calibrateReflectanceCheck(stackPane, psxFile, workflowType);
    }

    public void calibrateThermaleCheck(StackPane stackPane, String psxFile, WorkflowType workflowType){
        Path pythonPath = Paths.get(Settings.getSetting(Setting.AGISOFT_EXEC_PATH));
        Path filePath = Paths.get(snippetsPath, "calibrate_reflectance_check.py");

        ProcessBuilder pb = new ProcessBuilder(pythonPath.toFile().getAbsolutePath(), "-r",
                filePath.toFile().getAbsolutePath(), "-psxFile", psxFile, "-chunk_label", chunkLabel(workflowType));

        enqueue(workflowType, AgisoftTask.CALIBRATE_THERMAL_CHECK, stackPane, pb, true);
    }

    public void calibrateThermal(StackPane stackPane, String psxFile, WorkflowType workflowType, HashMap<String, String> agisoftParams, boolean batch){
        Path pythonPath = Paths.get(Settings.getSetting(Setting.AGISOFT_EXEC_PATH));
        Path filePath = Paths.get(snippetsPath, "calibrate_thermal.py");

        ProcessBuilder pb = new ProcessBuilder(pythonPath.toFile().getAbsolutePath(), "-r",
                filePath.toFile().getAbsolutePath(), "-psxFile", psxFile, "-chunk_label", chunkLabel(workflowType),
                "-batch", "" + batch);
        extendProcessBuilder(pb, agisoftParams);

        enqueue(workflowType, AgisoftTask.CALIBRATE_THERMAL, stackPane, pb, false);
        calibrateReflectanceCheck(stackPane, psxFile, workflowType);
    }

    public void alignPhotosCheck(StackPane stackPane, String psxFile, WorkflowType workflowType){
        Path pythonPath = Paths.get(Settings.getSetting(Setting.AGISOFT_EXEC_PATH));
        Path filePath = Paths.get(snippetsPath, "align_images_check.py");

        ProcessBuilder pb = new ProcessBuilder(pythonPath.toFile().getAbsolutePath(), "-r",
                filePath.toFile().getAbsolutePath(), "-psxFile", psxFile, "-chunk_label", chunkLabel(workflowType));

        enqueue(workflowType, AgisoftTask.ALIGN_IMAGES_CHECK, stackPane, pb, true);
    }

    public void alignPhotos(StackPane stackPane, String psxFile, WorkflowType workflowType, HashMap<String, String> agisoftParams, boolean batch, String exportPath){
        Path pythonPath = Paths.get(Settings.getSetting(Setting.AGISOFT_EXEC_PATH));
        Path filePath = Paths.get(snippetsPath, "align_images.py");

        ProcessBuilder pb = new ProcessBuilder(pythonPath.toFile().getAbsolutePath(), "-r",
                filePath.toFile().getAbsolutePath(), "-psxFile", psxFile, "-chunk_label", chunkLabel(workflowType),
                "-batch", "" + batch, "-export_path", exportPath);
        extendProcessBuilder(pb, agisoftParams);

        enqueue(workflowType, AgisoftTask.ALIGN_IMAGES, stackPane, pb, false);
        alignPhotosCheck(stackPane, psxFile, workflowType);
    }

    public void optimizeCamerasCheck(StackPane stackPane, String psxFile, WorkflowType workflowType){
        Path pythonPath = Paths.get(Settings.getSetting(Setting.AGISOFT_EXEC_PATH));
        Path filePath = Paths.get(snippetsPath, "optimize_cameras_check.py");

        ProcessBuilder pb = new ProcessBuilder(pythonPath.toFile().getAbsolutePath(), "-r",
                filePath.toFile().getAbsolutePath(), "-psxFile", psxFile, "-chunk_label", chunkLabel(workflowType));

        enqueue(workflowType, AgisoftTask.OPTIMIZE_CAMERAS_CHECK, stackPane, pb, true);
    }

    public void optimizeCameras(StackPane stackPane, String psxFile, WorkflowType workflowType, HashMap<String, String> agisoftParams){
        Path pythonPath = Paths.get(Settings.getSetting(Setting.AGISOFT_EXEC_PATH));
        Path filePath = Paths.get(snippetsPath, "optimize_cameras.py");

        ProcessBuilder pb = new ProcessBuilder(pythonPath.toFile().getAbsolutePath(), "-r",
                filePath.toFile().getAbsolutePath(), "-psxFile", psxFile, "-chunk_label", chunkLabel(workflowType));
        extendProcessBuilder(pb, agisoftParams);

        enqueue(workflowType, AgisoftTask.OPTIMIZE_CAMERAS, stackPane, pb, false);
        optimizeCamerasCheck(stackPane, psxFile, workflowType);
    }

    public void buildPointCloudCheck(StackPane stackPane, String psxFile, WorkflowType workflowType){
        Path pythonPath = Paths.get(Settings.getSetting(Setting.AGISOFT_EXEC_PATH));
        Path filePath = Paths.get(snippetsPath, "build_point_cloud_check.py");

        ProcessBuilder pb = new ProcessBuilder(pythonPath.toFile().getAbsolutePath(), "-r",
                filePath.toFile().getAbsolutePath(), "-psxFile", psxFile, "-chunk_label", chunkLabel(workflowType));

        enqueue(workflowType, AgisoftTask.BUILD_POINT_CLOUD_CHECK, stackPane, pb, true);
    }

    public void buildPointCloud(StackPane stackPane, String psxFile, WorkflowType workflowType, HashMap<String, String> agisoftParams, boolean batch, String exportPath){
        Path pythonPath = Paths.get(Settings.getSetting(Setting.AGISOFT_EXEC_PATH));
        Path filePath = Paths.get(snippetsPath, "build_point_cloud.py");

        ProcessBuilder pb = new ProcessBuilder(pythonPath.toFile().getAbsolutePath(), "-r",
                filePath.toFile().getAbsolutePath(), "-psxFile", psxFile, "-chunk_label", chunkLabel(workflowType),
                "-batch", "" + batch, "-export_path", exportPath);
        extendProcessBuilder(pb, agisoftParams);

        enqueue(workflowType, AgisoftTask.BUILD_POINT_CLOUD, stackPane, pb, false);
        buildPointCloudCheck(stackPane, psxFile, workflowType);
    }

    public void buildDemCheck(StackPane stackPane, String psxFile, WorkflowType workflowType){
        Path pythonPath = Paths.get(Settings.getSetting(Setting.AGISOFT_EXEC_PATH));
        Path filePath = Paths.get(snippetsPath, "build_dem_check.py");

        ProcessBuilder pb = new ProcessBuilder(pythonPath.toFile().getAbsolutePath(), "-r",
                filePath.toFile().getAbsolutePath(), "-psxFile", psxFile, "-chunk_label", chunkLabel(workflowType));

        enqueue(workflowType, AgisoftTask.BUILD_DEM_CHECK, stackPane, pb, true);
    }

    public void buildDem(StackPane stackPane, String psxFile, WorkflowType workflowType, HashMap<String, String> agisoftParams, boolean batch){
        Path pythonPath = Paths.get(Settings.getSetting(Setting.AGISOFT_EXEC_PATH));
        Path filePath = Paths.get(snippetsPath, "build_dem.py");

        ProcessBuilder pb = new ProcessBuilder(pythonPath.toFile().getAbsolutePath(), "-r",
                filePath.toFile().getAbsolutePath(), "-psxFile", psxFile, "-chunk_label", chunkLabel(workflowType),
                "-batch", "" + batch);
        extendProcessBuilder(pb, agisoftParams);

        enqueue(workflowType, AgisoftTask.BUILD_DEM, stackPane, pb, false);
        buildDemCheck(stackPane, psxFile, workflowType);
    }

    public void buildOrthomosaicCheck(StackPane stackPane, String psxFile, WorkflowType workflowType){
        Path pythonPath = Paths.get(Settings.getSetting(Setting.AGISOFT_EXEC_PATH));
        Path filePath = Paths.get(snippetsPath, "build_orthomosaic_check.py");

        ProcessBuilder pb = new ProcessBuilder(pythonPath.toFile().getAbsolutePath(), "-r",
                filePath.toFile().getAbsolutePath(), "-psxFile", psxFile, "-chunk_label", chunkLabel(workflowType));

        enqueue(workflowType, AgisoftTask.BUILD_ORTHOMOSAIC_CHECK, stackPane, pb, true);
    }

    public void buildOrthomosaic(StackPane stackPane, String psxFile, WorkflowType workflowType, HashMap<String, String> agisoftParams, boolean batch){
        Path pythonPath = Paths.get(Settings.getSetting(Setting.AGISOFT_EXEC_PATH));
        Path filePath = Paths.get(snippetsPath, "build_orthomosaic.py");

        ProcessBuilder pb = new ProcessBuilder(pythonPath.toFile().getAbsolutePath(), "-r",
                filePath.toFile().getAbsolutePath(), "-psxFile", psxFile, "-chunk_label", chunkLabel(workflowType),
                "-batch", "" + batch);
        extendProcessBuilder(pb, agisoftParams);

        enqueue(workflowType, AgisoftTask.BUILD_ORTHOMOSAIC, stackPane, pb, false);
        buildOrthomosaicCheck(stackPane, psxFile, workflowType);
    }

    public void exportDemCheck(StackPane stackPane, String psxFile, String targetFile, WorkflowType workflowType){
        Path pythonPath = Paths.get(Settings.getSetting(Setting.AGISOFT_EXEC_PATH));
        Path filePath = Paths.get(snippetsPath, "export_dem_check.py");

        ProcessBuilder pb = new ProcessBuilder(pythonPath.toFile().getAbsolutePath(), "-r",
                filePath.toFile().getAbsolutePath(), "-psxFile", psxFile, "-demFile", targetFile,
                "-chunk_label", chunkLabel(workflowType));

        enqueue(workflowType, AgisoftTask.EXPORT_DEM_CHECK, stackPane, pb, true);

    }
    public void exportDem(StackPane stackPane, String psxFile, String targetFile, WorkflowType workflowType, HashMap<String, String> agisoftParams, boolean batch){
        Path pythonPath = Paths.get(Settings.getSetting(Setting.AGISOFT_EXEC_PATH));
        Path filePath = Paths.get(snippetsPath, "export_dem.py");

        ProcessBuilder pb = new ProcessBuilder(pythonPath.toFile().getAbsolutePath(), "-r",
                filePath.toFile().getAbsolutePath(), "-psxFile", psxFile, "-demFile", targetFile,
                "-chunk_label", chunkLabel(workflowType), "-batch", "" + batch);
        extendProcessBuilder(pb, agisoftParams);

        enqueue(workflowType, AgisoftTask.EXPORT_DEM, stackPane, pb, false);
        exportDemCheck(stackPane, psxFile, targetFile, workflowType);
    }

    public void exportOrthoCheck(StackPane stackPane, String psxFile, String targetFile, WorkflowType workflowType){
        Path pythonPath = Paths.get(Settings.getSetting(Setting.AGISOFT_EXEC_PATH));
        Path filePath = Paths.get(snippetsPath, "export_orthomosaic_check.py");

        ProcessBuilder pb = new ProcessBuilder(pythonPath.toFile().getAbsolutePath(), "-r",
                filePath.toFile().getAbsolutePath(), "-psxFile", psxFile, "-orthoFile", targetFile,
                "-chunk_label", chunkLabel(workflowType));

        enqueue(workflowType, AgisoftTask.EXPORT_ORTHOMOSAIC_CHECK, stackPane, pb, true);

    }
    public void exportOrtho(StackPane stackPane, String psxFile, String targetFile, WorkflowType workflowType, HashMap<String, String> agisoftParams, boolean batch){
        Path pythonPath = Paths.get(Settings.getSetting(Setting.AGISOFT_EXEC_PATH));
        Path filePath = Paths.get(snippetsPath, "export_orthomosaic.py");

        ProcessBuilder pb = new ProcessBuilder(pythonPath.toFile().getAbsolutePath(), "-r",
                filePath.toFile().getAbsolutePath(), "-psxFile", psxFile, "-orthoFile", targetFile,
                "-chunk_label", chunkLabel(workflowType), "-batch", "" + batch);
        extendProcessBuilder(pb, agisoftParams);

        enqueue(workflowType, AgisoftTask.EXPORT_ORTHOMOSAIC, stackPane, pb, false);
        exportOrthoCheck(stackPane, psxFile, targetFile, workflowType);
    }

    public void generateReportCheck(StackPane stackPane, String psxFile, String targetFile, WorkflowType workflowType){
        Path pythonPath = Paths.get(Settings.getSetting(Setting.AGISOFT_EXEC_PATH));
        Path filePath = Paths.get(snippetsPath, "generate_report_check.py");

        ProcessBuilder pb = new ProcessBuilder(pythonPath.toFile().getAbsolutePath(), "-r",
                filePath.toFile().getAbsolutePath(), "-psxFile", psxFile, "-reportFile", targetFile,
                "-chunk_label", chunkLabel(workflowType));

        enqueue(workflowType, AgisoftTask.GENERATE_REPORT_CHECK, stackPane, pb, true);

    }
    public void generateReport(StackPane stackPane, String psxFile, String targetFile, String flightName, String description, WorkflowType workflowType, boolean batch){
        Path pythonPath = Paths.get(Settings.getSetting(Setting.AGISOFT_EXEC_PATH));
        Path filePath = Paths.get(snippetsPath, "generate_report.py");

        ProcessBuilder pb = new ProcessBuilder(pythonPath.toFile().getAbsolutePath(), "-r",
                filePath.toFile().getAbsolutePath(), "-psxFile", psxFile, "-reportFile", targetFile,
                "-flightName", flightName, "-description", description, "-chunk_label", chunkLabel(workflowType),
                "-batch", "" + batch);

        enqueue(workflowType, AgisoftTask.GENERATE_REPORT, stackPane, pb, false);
        generateReportCheck(stackPane, psxFile, targetFile, workflowType);

    }

    public void removeComponent(StackPane stackPane, String psxFile, AgisoftTask agisoftTask, WorkflowType workflowType){
        Path pythonPath = Paths.get(Settings.getSetting(Setting.AGISOFT_EXEC_PATH));
        Path filePath = Paths.get(snippetsPath, "remove_component.py");

        ProcessBuilder pb = new ProcessBuilder(pythonPath.toFile().getAbsolutePath(), "-r",
                filePath.toFile().getAbsolutePath(), "-psxFile", psxFile, "-chunk_label", chunkLabel(workflowType),
                "-agisofttask", "" + agisoftTask);
        enqueue(workflowType, AgisoftTask.REMOVE_COMPONENT, null, pb, false);

        switch (agisoftTask){
            case ADD_PHOTOS -> addPhotosCheck(stackPane, psxFile, workflowType);
            case ALIGN_IMAGES -> alignPhotosCheck(stackPane, psxFile, workflowType);
            case BUILD_POINT_CLOUD -> buildPointCloudCheck(stackPane, psxFile, workflowType);
            case BUILD_DEM -> buildDemCheck(stackPane, psxFile, workflowType);
            case BUILD_ORTHOMOSAIC -> buildOrthomosaicCheck(stackPane, psxFile, workflowType);
        }

    }

    public void completeBuildRGB(List<StackPane> stackPanes, List<HashMap<String, String>> agisoftParameters,
                                 List<String> folders, String psxFile, String demFile, String orthoFile,
                                 String reportFile, String flightName, String reportDescription,
                                 String cloudExportTie, String cloudExportDense){

        addPhotos(stackPanes.get(0), psxFile, folders, List.of(), WorkflowType.RGB, true);
        setBrightness(stackPanes.get(1), psxFile, WorkflowType.RGB, agisoftParameters.get(0));
        alignPhotos(stackPanes.get(2), psxFile, WorkflowType.RGB, agisoftParameters.get(1), true, cloudExportTie);
        optimizeCameras(stackPanes.get(3), psxFile, WorkflowType.RGB, agisoftParameters.get(2));
        buildPointCloud(stackPanes.get(4), psxFile, WorkflowType.RGB, agisoftParameters.get(3), true, cloudExportDense);
        buildDem(stackPanes.get(5), psxFile, WorkflowType.RGB, agisoftParameters.get(4), true);
        buildOrthomosaic(stackPanes.get(6), psxFile, WorkflowType.RGB, agisoftParameters.get(5), true);
        exportDem(stackPanes.get(7), psxFile, demFile, WorkflowType.RGB, agisoftParameters.get(6), true);
        exportOrtho(stackPanes.get(8), psxFile, orthoFile, WorkflowType.RGB, agisoftParameters.get(7), true);
        generateReport(stackPanes.get(9), psxFile, reportFile, flightName, reportDescription, WorkflowType.RGB, true);

    }

    public void completeBuildMSOption(WorkflowType workflowType, List<StackPane> stackPanes,
                                      List<HashMap<String, String>> agisoftParameters, List<String> folders,
                                      String psxFile, String demFile, String orthoFile, String reportFile,
                                      String flightName, String reportDescription, String cloudExportTie,
                                      String cloudExportDense){

        addPhotos(stackPanes.get(0), psxFile, folders, List.of(), workflowType, true);
        setBrightness(stackPanes.get(1), psxFile, workflowType, agisoftParameters.get(0));
        calibrateReflectance(stackPanes.get(2), psxFile, workflowType, agisoftParameters.get(1), true);
        alignPhotos(stackPanes.get(3), psxFile, workflowType, agisoftParameters.get(2), true, cloudExportTie);
        optimizeCameras(stackPanes.get(4), psxFile, workflowType, agisoftParameters.get(3));
        buildPointCloud(stackPanes.get(5), psxFile, workflowType, agisoftParameters.get(4), true, cloudExportDense);
        buildDem(stackPanes.get(6), psxFile, workflowType, agisoftParameters.get(5), true);
        buildOrthomosaic(stackPanes.get(7), psxFile, workflowType, agisoftParameters.get(6), true);
        exportDem(stackPanes.get(8), psxFile, demFile, workflowType, agisoftParameters.get(7), true);
        exportOrtho(stackPanes.get(9), psxFile, orthoFile, workflowType, agisoftParameters.get(8), true);
        generateReport(stackPanes.get(10), psxFile, reportFile, flightName, reportDescription, workflowType, true);

    }

    public void enqueue(WorkflowType workflowType, AgisoftTask task, Pane pane, ProcessBuilder pb, boolean nextIfFailed){
        queueListener.enqueueAgisoft(workflowType, task);

        boolean isCheckCommand = task == AgisoftTask.CHECK_CHUNK;

        Queue<Runnable> queue = isCheckCommand ? StatusController.checkQueue : StatusController.queue;

        queue.add(() -> CompletableFuture.supplyAsync(() -> {
            queueListener.startedAgisoft(workflowType, task);

            try{
                pb.redirectErrorStream(true);
                Process p = pb.start();

                if (isCheckCommand) {
                    StatusController.currentCheckProcess = p;
                } else {
                    StatusController.currentProcess = p;
                }

                Pair<AgisoftTask, String> success = watchForSignal("vn:", p.getInputStream(), callbackListener, workflowType, task, pane);

                int exitCode = p.waitFor();

                if (exitCode != 0) return new Pair<>(AgisoftTask.UNDEFINED, "False");

                return success;
            }catch (IOException | InterruptedException e){
                return new Pair<>(AgisoftTask.UNDEFINED, "False");
            }
        }) .thenAcceptAsync(result -> {
            boolean success = Boolean.parseBoolean(result.getValue());

            try {
                queueListener.finishAgisoft(workflowType, task);
                callbackListener.callbackAgisoft(pane, workflowType, task, result.getValue());
            } catch (UMASException e) {
                throw new RuntimeException(e);
            }

            if(success) {
                if(!queue.isEmpty()){
                    if(isCheckCommand){
                        StatusController.setRunningChecks(true);
                    }else{
                        StatusController.setRunning(true);
                    }
                    processNext(queue);
                }else{
                    if(isCheckCommand){
                        StatusController.setRunningChecks(false);
                    }else{
                        StatusController.setRunning(false);
                    }
                }
            }else{
                if(nextIfFailed) {
                    if(!queue.isEmpty()){
                        if(isCheckCommand){
                            StatusController.setRunningChecks(true);
                        }else{
                            StatusController.setRunning(true);
                        }
                        processNext(queue);
                    }else {
                        if(isCheckCommand){
                            StatusController.setRunningChecks(false);
                        }else{
                            StatusController.setRunning(false);
                        }
                    }
                }else{
                    if(isCheckCommand){
                        StatusController.setRunningChecks(false);
                    }else{
                        StatusController.setRunning(false);
                    }
                }
            }

        }));

        if (task != AgisoftTask.CHECK_CHUNK && !StatusController.isRunning()) {
            StatusController.setRunning(true);
            processNext(queue);
        } else if(task == AgisoftTask.CHECK_CHUNK && !StatusController.isRunningChecks()) {
            StatusController.setRunningChecks(true);
            processNext(queue);
        }
    }

    public Pair<AgisoftTask, String> watchForSignal(String signalKey, InputStream inputStream, CallbackListener listener, WorkflowType workflowType, AgisoftTask task, Pane pane) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                if(listener != null && (line.startsWith("vp: ") || line.startsWith("xvp: "))) {
                    String finalLine = line;
                    Platform.runLater(() -> {
                        try{
                            listener.progress(display, Float.parseFloat(finalLine.substring(4)));
                        } catch (NumberFormatException ignored) {  }
                    });
                }
                if(line.startsWith(signalKey)){
                    String[] split = line.split(":");

                    AgisoftTask currentTask = AgisoftTask.valueOf(split[1]);

                    if(currentTask == task){
                        if(listener != null)
                            Platform.runLater(() -> listener.progress(display, 0));

                        return new Pair<>(currentTask, split[2]);
                    }else{
                        if(listener != null && pane != null)
                            listener.callbackAgisoft(pane, workflowType, currentTask, split[2]);
                    }
                }
                if(line.startsWith("ve:")){
                    final String[] split = line.split(":");

                    Platform.runLater(() -> {
                        AgisoftTask currentTask = AgisoftTask.valueOf(split[1]);

                        DialogPane dialogPane = (DialogPane) display.getSceneLoader().getScene("agisoft_error_dialog");
                        Dialog<String> dialog = new UMASDialog(dialogPane, "Error", true, true);

                        String[] splitForError = split[2].split("~");

                        AgisoftErrorController errorController = new AgisoftErrorController(
                                splitForError[0], splitForError[1], splitForError[2]);

                        try {
                            errorController.init(display, dialog);
                        } catch (UMASException e) {
                            throw new RuntimeException(e);
                        }

                        dialog.show();
                    });

                }
            }
        } catch (UMASException e) {
            throw new RuntimeException(e);
        }
        return new Pair<>(AgisoftTask.UNDEFINED, Boolean.FALSE.toString());
    }

    private synchronized void processNext(Queue<Runnable> queue) {
        Runnable nextTask = queue.poll();
        if (nextTask != null) {
            nextTask.run();
        }
    }

    public static void killAll(){
        for(int i = 0; i < StatusController.queue.size(); i++){
            StatusController.queue.remove();
        }
        for(int i = 0; i < StatusController.checkQueue.size(); i++){
            StatusController.checkQueue.remove();
        }
        if(StatusController.currentProcess != null) {
            StatusController.currentProcess.destroy();
        }
        if(StatusController.currentCheckProcess != null) {
            StatusController.currentCheckProcess.destroy();
        }
    }




    private String chunkLabel(WorkflowType workflowType){
        return workflowType.name();
    }

    private void extendProcessBuilder(ProcessBuilder pb, HashMap<String, String> agisoftParams){
        for(Map.Entry<String, String> entry : agisoftParams.entrySet()){
            pb.command().add("-" + entry.getKey());
            pb.command().add(entry.getValue());
        }
    }

    public HashMap<String, String> getDefaultParameters(AgisoftParameter[] agisoftParameters) {
        HashMap<String, String> parameters = new HashMap<>();

        for(AgisoftParameter parameter : agisoftParameters){
            parameters.put(parameter.getId(), parameter.getChoices().get(parameter.getDefaultIndex()));
        }

        return parameters;
    }

}
