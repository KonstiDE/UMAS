package wue.eorc.umas.controller.scenes.views.panes.components;

import com.google.gson.Gson;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.control.Dialog;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import wue.eorc.umas.agisoft.AgisoftCaller;
import wue.eorc.umas.controller.customs.UMASDialog;
import wue.eorc.umas.controller.scenes.views.dialogs.StaticDialogController;
import wue.eorc.umas.controller.scenes.views.dialogs.agisoft.*;
import wue.eorc.umas.controller.scenes.main.DisplayController;
import wue.eorc.umas.controller.scenes.views.panes.ShowProcessingController;
import wue.eorc.umas.enums.ErrorType;
import wue.eorc.umas.enums.ImageType;
import wue.eorc.umas.enums.WorkflowType;
import wue.eorc.umas.enums.agisoft.*;
import wue.eorc.umas.exception.UMASException;
import wue.eorc.umas.models.Flight;
import wue.eorc.umas.utils.DirectoryUtils;
import wue.eorc.umas.utils.GsonTypeTokens;
import wue.eorc.umas.utils.ItemSearcher;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static wue.eorc.umas.enums.agisoft.AgisoftTask.*;

public class ProcessActionsPreparer {

    //TODO open settings window
    // best maybe to start is a dynamic settings dialog that takes in the ALIGN_IMAGES.getParameters() and
    // returns a modified version that is then passed to the agisoftCaller alignImages method
    // However, how can I modify AND batch process. Maybe a second menu option, modify entire batch, and
    // then everything from the current workflowType gets loaded into the dialog
    // This idea is nice but it does not work since dialogs elements interact with each other, very annoying

    private final AgisoftCaller agisoftCaller;

    private final Flight flight;
    private final AnchorPane workflowPane;
    private final WorkflowType workflowType;

    private final DisplayController display;

    private final Gson gson = new Gson();

    public StackPane addPhotos;
    public StackPane setBrightness;
    public StackPane setCalibrateReflectance;
    public StackPane alignImages;
    public StackPane optimizeCameras;
    public StackPane buildPointCloud;
    public StackPane buildDem;
    public StackPane buildOrthomosaic;
    public StackPane exportDem;
    public StackPane exportOrthomosaic;
    public StackPane generateReport;

    public ProcessActionsPreparer(Flight flight, WorkflowType workflowType, DisplayController display, ShowProcessingController showProcessingController, AgisoftCaller agisoftCaller) throws UMASException {
        this.flight = flight;
        this.workflowPane = switch (workflowType){
            case RGB -> (AnchorPane) display.getSceneLoader().getScene("rgb_workflow");
            case MULTISPECTRAL, RGB_PLUS_MULTISPECTRAL -> (AnchorPane) display.getSceneLoader().getScene("ms_workflow");
            case IR -> (AnchorPane) display.getSceneLoader().getScene("rgb_workflow");
            case RGB_PLUS_IR -> (AnchorPane) display.getSceneLoader().getScene("rgb_workflow");
            case HYPERSPECTRAL -> (AnchorPane) display.getSceneLoader().getScene("rgb_workflow");
            case LIDAR -> (AnchorPane) display.getSceneLoader().getScene("rgb_workflow");
            case INVALID -> null;
        };
        this.workflowType = workflowType;
        this.display = display;
        this.agisoftCaller = agisoftCaller;

        setupWorkflowActions();
    }

    public void setupWorkflowActions() throws UMASException {
        switch (this.workflowType){
            case RGB -> {
                this.addPhotos = setupAddPhotos();
                this.setBrightness = setupSetBrightness();
                this.alignImages = setupAlignPhotos();
                this.optimizeCameras = setupOptimizeCameras();
                this.buildPointCloud = setupBuildPointCloud();
                this.buildDem = setupBuildDem();
                this.buildOrthomosaic = setupBuildOrthomosaic();
                this.exportDem = setupExportDem();
                this.exportOrthomosaic = setupExportOrthomosaic();
                this.generateReport = setupGenerateReports();

                setupCheckProject();
            }
            case MULTISPECTRAL, RGB_PLUS_MULTISPECTRAL -> {
                this.addPhotos = setupAddPhotos();
                this.setBrightness = setupSetBrightness();
                this.setCalibrateReflectance = setupCalibrateReflectance();
                this.alignImages = setupAlignPhotos();
                this.optimizeCameras = setupOptimizeCameras();
                this.buildPointCloud = setupBuildPointCloud();
                this.buildDem = setupBuildDem();
                this.buildOrthomosaic = setupBuildOrthomosaic();
                this.exportDem = setupExportDem();
                this.exportOrthomosaic = setupExportOrthomosaic();
                this.generateReport = setupGenerateReports();

                setupCheckProject();
            }
            case IR -> {}
            case LIDAR -> {}
            case HYPERSPECTRAL -> {}
        }
    }

    private void setupCheckProject() throws UMASException {
        AnchorPane workflowParent = ItemSearcher.getItemById("workflowpane", this.workflowPane, AnchorPane.class);

        String demFile = Paths.get(DirectoryUtils.figureExportPath(this.flight), this.flight.getExportDemName()).toFile().getAbsolutePath();
        String orthoFile = Paths.get(DirectoryUtils.figureExportPath(this.flight), this.flight.getExportOrthomosaicName()).toFile().getAbsolutePath();
        String reportFile = Paths.get(DirectoryUtils.figureReportPath(this.flight), this.flight.getGenerateReportName()).toFile().getAbsolutePath();

        agisoftCaller.checkChunk(workflowParent, DirectoryUtils.figureAgisoftFilePath(this.flight), demFile,
                orthoFile, reportFile, this.workflowType);
    }

    private StackPane setupAddPhotos() throws UMASException {
        StackPane addPhotos = ItemSearcher.getItemById("processing." + ADD_PHOTOS, this.workflowPane, StackPane.class);

        addPhotos.setCursor(Cursor.HAND);
        addPhotos.setOnMouseClicked(mouseEvent -> {
            if(mouseEvent.getButton() == MouseButton.PRIMARY){
                switch (this.workflowType){
                    case RGB -> agisoftCaller.addPhotos(addPhotos, DirectoryUtils.figureAgisoftFilePath(this.flight),
                            this.flight.getImageTypes().keySet().stream()
                                    .filter(i -> this.workflowType.getImageTypes().contains(i))
                                    .map(i -> this.flight.getImageTypes().get(i)).toList(),
                            List.of(),
                            this.workflowType, false);
                    case MULTISPECTRAL, RGB_PLUS_MULTISPECTRAL -> agisoftCaller.addPhotos(addPhotos, DirectoryUtils.figureAgisoftFilePath(this.flight),

                            this.flight.getImageTypes().keySet().stream()
                                    .filter(i -> this.workflowType.getImageTypes().contains(i) && i != ImageType.CALIBRATION)
                                    .map(i -> this.flight.getImageTypes().get(i)).toList(),

                            List.of(this.flight.getImageTypes().get(ImageType.CALIBRATION)),

                            this.workflowType, false);
                }

            }else if (mouseEvent.getButton() == MouseButton.SECONDARY){
                setupModificationDialog(addPhotos, mouseEvent, ADD_PHOTOS, null);
            }
        });

        return addPhotos;

    }

    private StackPane setupSetBrightness() throws UMASException {
        StackPane setBrightness = ItemSearcher.getItemById("processing." + SET_BRIGHTNESS, this.workflowPane, StackPane.class);

        setBrightness.setCursor(Cursor.HAND);
        setBrightness.setOnMouseClicked(mouseEvent -> {
            if(mouseEvent.getButton() == MouseButton.PRIMARY){
                DialogPane dialogPane = (DialogPane) display.getSceneLoader().getScene(AgisoftDialog.SET_BRIGHTNESS.getDialogId());
                Dialog<String> dialog = new UMASDialog(dialogPane, "Set Brightness", true, true);

                SetBrightnessController controller = new SetBrightnessController(this);
                try {
                    controller.init(display, dialog);
                } catch (UMASException e) {
                    throw new RuntimeException(e);
                }

                Optional<String> result = dialog.showAndWait();

                if(result.isPresent()){
                    HashMap<String, String> agisoftParameters = gson.fromJson(result.get(), GsonTypeTokens.hashmapToken);
                    agisoftCaller.setBrightness(setBrightness, DirectoryUtils.figureAgisoftFilePath(this.flight), this.workflowType, agisoftParameters);
                }
            }else if (mouseEvent.getButton() == MouseButton.SECONDARY) {
                setupModificationDialog(setBrightness, mouseEvent, SET_BRIGHTNESS, null);
            }

        });

        return setBrightness;
    }

    public void callBrightnessEstimate(Pane pane) throws UMASException {
        agisoftCaller.setBrightnessEstimate(pane, DirectoryUtils.figureAgisoftFilePath(this.flight), this.workflowType);
    }

    private StackPane setupCalibrateReflectance() throws UMASException {
        StackPane calibrateReflectance = ItemSearcher.getItemById("processing." + CALIBRATE_REFLECTANCE, this.workflowPane, StackPane.class);

        calibrateReflectance.setCursor(Cursor.HAND);
        calibrateReflectance.setOnMouseClicked(mouseEvent -> {
            if(mouseEvent.getButton() == MouseButton.PRIMARY){
                agisoftCaller.calibrateReflectance(calibrateReflectance, DirectoryUtils.figureAgisoftFilePath(this.flight),
                        this.workflowType, new HashMap<>() {  }, false);

            }else if (mouseEvent.getButton() == MouseButton.SECONDARY){
                setupModificationDialog(calibrateReflectance, mouseEvent, CALIBRATE_REFLECTANCE, event -> {});

            }
        });

        return calibrateReflectance;

    }

    private StackPane setupAlignPhotos() throws UMASException {
        StackPane alignPhotos = ItemSearcher.getItemById("processing." + ALIGN_IMAGES, this.workflowPane, StackPane.class);

        alignPhotos.setCursor(Cursor.HAND);
        alignPhotos.setOnMouseClicked(mouseEvent -> {
            if(mouseEvent.getButton() == MouseButton.PRIMARY){
                agisoftCaller.alignPhotos(alignPhotos, DirectoryUtils.figureAgisoftFilePath(this.flight),
                        this.workflowType, getDefaultParameters(AlignImages.values()), false);

            }else if(mouseEvent.getButton() == MouseButton.SECONDARY){
                setupModificationDialog(alignPhotos, mouseEvent, ALIGN_IMAGES, event -> {
                    DialogPane parameterPane = (DialogPane)
                            display.getSceneLoader().getScene(AgisoftDialog.ALIGN_IMAGES.getDialogId());

                    StaticDialogController controller = new AlignImagesController();

                    Dialog<String> dialog = new UMASDialog(parameterPane, "Align Images", true, true);
                    try {
                        controller.init(display, dialog);
                    } catch (UMASException e) {
                        throw new RuntimeException(e);
                    }

                    Optional<String> json = dialog.showAndWait();
                    dialog.hide();
                    dialog.close();

                    if (json.isPresent()){
                        agisoftCaller.alignPhotos(alignPhotos, DirectoryUtils.figureAgisoftFilePath(this.flight),
                                this.workflowType, retrieveManualChoice(json.orElse(null)), false);
                    }
                });

            }
        });

        return alignPhotos;
    }

    private StackPane setupOptimizeCameras() throws UMASException {
        StackPane optimizeCameras = ItemSearcher.getItemById("processing." + OPTIMIZE_CAMERAS, this.workflowPane, StackPane.class);

        optimizeCameras.setCursor(Cursor.HAND);
        optimizeCameras.setOnMouseClicked(mouseEvent -> {
            if(mouseEvent.getButton() == MouseButton.PRIMARY) {
                agisoftCaller.optimizeCameras(optimizeCameras, DirectoryUtils.figureAgisoftFilePath(this.flight),
                        this.workflowType, getDefaultParameters(OptimizeCameras.values()));

            }else if (mouseEvent.getButton() == MouseButton.SECONDARY){
                setupModificationDialog(optimizeCameras, mouseEvent, OPTIMIZE_CAMERAS, event -> {
                    DialogPane parameterPane = (DialogPane)
                            display.getSceneLoader().getScene(AgisoftDialog.OPTIMIZE_CAMERAS.getDialogId());

                    OptimizeCamerasController controller = new OptimizeCamerasController();

                    Dialog<String> dialog = new UMASDialog(parameterPane, "Optimize Camera Alignment", true, true);
                    try {
                        controller.init(display, dialog);
                    } catch (UMASException e) {
                        throw new RuntimeException(e);
                    }

                    Optional<String> json = dialog.showAndWait();
                    dialog.hide();
                    dialog.close();

                    if (json.isPresent()){
                        agisoftCaller.optimizeCameras(optimizeCameras, DirectoryUtils.figureAgisoftFilePath(this.flight),
                                this.workflowType, retrieveManualChoice(json.orElse(null)));
                    }
                });
            }
        });

        return optimizeCameras;
    }

    private StackPane setupBuildPointCloud() throws UMASException {
        StackPane buildPointCloud = ItemSearcher.getItemById("processing." + BUILD_POINT_CLOUD, this.workflowPane, StackPane.class);

        buildPointCloud.setCursor(Cursor.HAND);
        buildPointCloud.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton() == MouseButton.PRIMARY){
                agisoftCaller.buildPointCloud(buildPointCloud, DirectoryUtils.figureAgisoftFilePath(this.flight),
                        this.workflowType, getDefaultParameters(BuildPointCloud.values()), false);

            }else if (mouseEvent.getButton() == MouseButton.SECONDARY){
                setupModificationDialog(buildPointCloud, mouseEvent, BUILD_POINT_CLOUD, event -> {
                    DialogPane parameterPane = (DialogPane)
                            display.getSceneLoader().getScene(AgisoftDialog.BUILD_POINT_CLOUD.getDialogId());

                    BuildPointCloudController controller = new BuildPointCloudController();

                    Dialog<String> dialog = new UMASDialog(parameterPane, "Build Dense (Point) Cloud", true, true);
                    try {
                        controller.init(display, dialog);
                    } catch (UMASException e) {
                        throw new RuntimeException(e);
                    }

                    Optional<String> json = dialog.showAndWait();
                    dialog.hide();
                    dialog.close();

                    if (json.isPresent()){
                        agisoftCaller.buildPointCloud(buildPointCloud, DirectoryUtils.figureAgisoftFilePath(this.flight),
                                this.workflowType, retrieveManualChoice(json.orElse(null)), false);
                    }
                });
            }
        });

        return buildPointCloud;
    }

    private StackPane setupBuildDem() throws UMASException {
        StackPane buildDem = ItemSearcher.getItemById("processing." + BUILD_DEM, this.workflowPane, StackPane.class);

        buildDem.setCursor(Cursor.HAND);
        buildDem.setOnMouseClicked(mouseEvent -> {
            if(mouseEvent.getButton() == MouseButton.PRIMARY) {
                agisoftCaller.buildDem(buildDem, DirectoryUtils.figureAgisoftFilePath(this.flight),
                        this.workflowType, getDefaultParameters(BuildDem.values()), false);

            } else if (mouseEvent.getButton() == MouseButton.SECONDARY){
                setupModificationDialog(buildDem, mouseEvent, BUILD_DEM, event -> {
                    DialogPane parameterPane = (DialogPane)
                            display.getSceneLoader().getScene(AgisoftDialog.BUILD_DEM.getDialogId());

                    BuildDemController controller = new BuildDemController();

                    Dialog<String> dialog = new UMASDialog(parameterPane, "Build DEM", true, true);
                    try {
                        controller.init(display, dialog);
                    } catch (UMASException e) {
                        throw new RuntimeException(e);
                    }

                    Optional<String> json = dialog.showAndWait();
                    dialog.hide();
                    dialog.close();

                    if (json.isPresent()){
                        agisoftCaller.buildDem(buildDem, DirectoryUtils.figureAgisoftFilePath(this.flight),
                                this.workflowType, retrieveManualChoice(json.orElse(null)), false);
                    }
                });
            }
        });

        return buildDem;
    }

    private StackPane setupBuildOrthomosaic() throws UMASException {
        StackPane buildOrthomosaic = ItemSearcher.getItemById("processing." + BUILD_ORTHOMOSAIC, this.workflowPane, StackPane.class);

        buildOrthomosaic.setCursor(Cursor.HAND);
        buildOrthomosaic.setOnMouseClicked(mouseEvent -> {
            if(mouseEvent.getButton() == MouseButton.PRIMARY){
                agisoftCaller.buildOrthomosaic(buildOrthomosaic, DirectoryUtils.figureAgisoftFilePath(this.flight),
                        this.workflowType, getDefaultParameters(BuildOrthomosaic.values()), false);

            }else{
                setupModificationDialog(buildOrthomosaic, mouseEvent, BUILD_ORTHOMOSAIC, event -> {
                    DialogPane parameterPane = (DialogPane)
                            display.getSceneLoader().getScene(AgisoftDialog.BUILD_ORTHOMOSAIC.getDialogId());

                    BuildOrthomosaicController controller = new BuildOrthomosaicController();

                    Dialog<String> dialog = new UMASDialog(parameterPane, "Build Orthomosaic", true, true);
                    try {
                        controller.init(display, dialog);
                    } catch (UMASException e) {
                        throw new RuntimeException(e);
                    }

                    Optional<String> json = dialog.showAndWait();
                    dialog.hide();
                    dialog.close();

                    if (json.isPresent()){
                        agisoftCaller.buildOrthomosaic(buildOrthomosaic, DirectoryUtils.figureAgisoftFilePath(this.flight),
                                this.workflowType, retrieveManualChoice(json.orElse(null)), false);
                    }
                });
            }
        });

        return buildOrthomosaic;
    }

    private StackPane setupExportDem() throws UMASException {
        StackPane exportDem = ItemSearcher.getItemById("processing." + EXPORT_DEM, this.workflowPane, StackPane.class);

        exportDem.setCursor(Cursor.HAND);
        exportDem.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton() == MouseButton.PRIMARY) {
                agisoftCaller.exportDem(exportDem, DirectoryUtils.figureAgisoftFilePath(this.flight), Paths.get(
                        DirectoryUtils.figureExportPath(this.flight),
                        this.flight.getExportDemName()
                ).toFile().getAbsolutePath(), this.workflowType, getDefaultParameters(ExportDem.values()), false);

            } else if (mouseEvent.getButton() == MouseButton.SECONDARY) {
                setupModificationDialog(exportDem, mouseEvent, EXPORT_DEM, event -> {
                    DialogPane parameterPane = (DialogPane)
                            display.getSceneLoader().getScene(AgisoftDialog.EXPORT_DEM.getDialogId());

                    ExportDemController controller = new ExportDemController();

                    Dialog<String> dialog = new UMASDialog(parameterPane, "Export DEM - TIFF", true, true);
                    try {
                        controller.init(display, dialog);
                    } catch (UMASException e) {
                        throw new RuntimeException(e);
                    }

                    Optional<String> json = dialog.showAndWait();
                    dialog.hide();
                    dialog.close();

                    if (json.isPresent()){
                        agisoftCaller.exportDem(exportDem, DirectoryUtils.figureAgisoftFilePath(this.flight), Paths.get(
                                DirectoryUtils.figureExportPath(this.flight),
                                this.flight.getExportDemName()
                        ).toFile().getAbsolutePath(), this.workflowType, retrieveManualChoice(json.orElse(null))
                                , false);
                    }
                });
            }
        });

        return exportDem;
    }

    private StackPane setupExportOrthomosaic() throws UMASException {
        StackPane exportOrtho = ItemSearcher.getItemById("processing." + EXPORT_ORTHOMOSAIC, this.workflowPane, StackPane.class);

        exportOrtho.setCursor(Cursor.HAND);
        exportOrtho.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton() == MouseButton.PRIMARY) {

                agisoftCaller.exportOrtho(exportOrtho, DirectoryUtils.figureAgisoftFilePath(this.flight), Paths.get(
                        DirectoryUtils.figureExportPath(this.flight),
                        this.flight.getExportOrthomosaicName()
                ).toFile().getAbsolutePath(), this.workflowType, getDefaultParameters(ExportOrthomosaic.values()), false);

            } else if (mouseEvent.getButton() == MouseButton.SECONDARY) {
                setupModificationDialog(exportOrthomosaic, mouseEvent, EXPORT_ORTHOMOSAIC, event -> {
                    DialogPane parameterPane = (DialogPane)
                            display.getSceneLoader().getScene(AgisoftDialog.EXPORT_ORTHOMOSAIC.getDialogId());

                    ExportOrthomosaicController controller = new ExportOrthomosaicController();

                    Dialog<String> dialog = new UMASDialog(parameterPane, "Export Orthomosaic - TIFF", true, true);
                    try {
                        controller.init(display, dialog);
                    } catch (UMASException e) {
                        throw new RuntimeException(e);
                    }

                    Optional<String> json = dialog.showAndWait();
                    dialog.hide();
                    dialog.close();

                    if (json.isPresent()){
                        agisoftCaller.exportOrtho(exportOrtho, DirectoryUtils.figureAgisoftFilePath(this.flight), Paths.get(
                                DirectoryUtils.figureExportPath(this.flight),
                                this.flight.getExportOrthomosaicName()
                        ).toFile().getAbsolutePath(), this.workflowType, retrieveManualChoice(json.orElse(null)), false);
                    }
                });
            }
        });

        return exportOrtho;
    }

    public StackPane setupGenerateReports() throws UMASException {
        StackPane generateReport = ItemSearcher.getItemById("processing." + GENERATE_REPORT, this.workflowPane, StackPane.class);

        generateReport.setCursor(Cursor.HAND);
        generateReport.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton() == MouseButton.PRIMARY){
                agisoftCaller.generateReport(
                        generateReport, DirectoryUtils.figureAgisoftFilePath(this.flight), Paths.get(
                                DirectoryUtils.figureReportPath(this.flight),
                                this.flight.getGenerateReportName()
                        ).toFile().getAbsolutePath(),
                        this.flight.getGenerateReportName(),
                        "Automatically generated Report",
                        this.workflowType, false
                );
            }else if(mouseEvent.getButton() == MouseButton.SECONDARY){
                setupModificationDialog(generateReport, mouseEvent, GENERATE_REPORT, null);
            }
        });

        return generateReport;
    }


    public Flight getFlight() {
        return flight;
    }

    public AnchorPane getWorkflowPane() {
        return workflowPane;
    }

    public HashMap<String, String> retrieveManualChoice(String json) {
        return gson.fromJson(json, GsonTypeTokens.hashmapToken);
    }

    public void setupModificationDialog(StackPane stackPane, MouseEvent mouseEvent, AgisoftTask agisoftTask,
                                        EventHandler<ActionEvent> eventHandler){

        final ContextMenu contextMenu = new ContextMenu();
        final MenuItem separator = new SeparatorMenuItem();

        final MenuItem modify = new MenuItem("Modify");
        final MenuItem remove = new MenuItem("Remove " + toRemoveFromAgisoftTask(agisoftTask));
        final MenuItem modifyBatch = new MenuItem("Modify Batch");
        final MenuItem runBatch = new MenuItem("Run Batch");

        if(eventHandler != null) {
            modify.setOnAction(eventHandler);
            contextMenu.getItems().add(modify);
        }

        if(List.of(ADD_PHOTOS, CALIBRATE_REFLECTANCE, ALIGN_IMAGES, BUILD_POINT_CLOUD, BUILD_DEM, BUILD_ORTHOMOSAIC).contains(agisoftTask)){
            remove.setOnAction(handleRemove(stackPane, agisoftTask));
            contextMenu.getItems().add(remove);
        }

        modifyBatch.setOnAction(handleModifyBatch());
        runBatch.setOnAction(handleRunBatch());

        contextMenu.getItems().add(modifyBatch);
        contextMenu.getItems().add(separator);
        contextMenu.getItems().add(runBatch);
        contextMenu.show(getWorkflowPane().getScene().getWindow(), mouseEvent.getScreenX(), mouseEvent.getScreenY());
    }

    public EventHandler<ActionEvent> handleModifyBatch(){
        return actionEvent -> {
            switch (workflowType){
                case RGB -> {
                    DialogPane dialogPane = (DialogPane) display.getSceneLoader().getScene(AgisoftDialog.BATCH_EDIT.getDialogId());
                    BatchEditController batchEditController = new BatchEditController(WorkflowType.RGB, ProcessActionsPreparer.this);

                    UMASDialog dialog = new UMASDialog(dialogPane, "Batch Edit", true, true);

                    try {
                        batchEditController.init(display.getRootController().getDisplayController(), dialog);
                    } catch (UMASException e) {
                        throw new RuntimeException(e);
                    }

                    Optional<String> result = dialog.showAndWait();
                    dialog.hide();
                    dialog.close();

                    HashMap<String, String> map = gson.fromJson(result.orElse(null), GsonTypeTokens.hashmapToken);

                    if(map != null){
                        agisoftCaller.completeBuildRGB(
                                List.of(addPhotos, setBrightness, alignImages, optimizeCameras, buildPointCloud, buildDem,
                                        buildOrthomosaic, exportDem, exportOrthomosaic, generateReport),
                                List.of(
                                        gson.fromJson(map.get(SET_BRIGHTNESS.name()), GsonTypeTokens.hashmapToken),
                                        gson.fromJson(map.get(ALIGN_IMAGES.name()), GsonTypeTokens.hashmapToken),
                                        gson.fromJson(map.get(OPTIMIZE_CAMERAS.name()), GsonTypeTokens.hashmapToken),
                                        gson.fromJson(map.get(BUILD_POINT_CLOUD.name()), GsonTypeTokens.hashmapToken),
                                        gson.fromJson(map.get(BUILD_DEM.name()), GsonTypeTokens.hashmapToken),
                                        gson.fromJson(map.get(BUILD_ORTHOMOSAIC.name()), GsonTypeTokens.hashmapToken),
                                        gson.fromJson(map.get(EXPORT_DEM.name()), GsonTypeTokens.hashmapToken),
                                        gson.fromJson(map.get(EXPORT_ORTHOMOSAIC.name()), GsonTypeTokens.hashmapToken)
                                ),
                                flight.getOriginFlightDirs(),
                                DirectoryUtils.figureAgisoftFilePath(flight),
                                Paths.get(DirectoryUtils.figureExportPath(flight), flight.getExportDemName()).toFile().getAbsolutePath(),
                                Paths.get(DirectoryUtils.figureExportPath(flight), flight.getExportOrthomosaicName()).toFile().getAbsolutePath(),
                                Paths.get(DirectoryUtils.figureReportPath(flight), flight.getGenerateReportName()).toFile().getAbsolutePath(),
                                flight.getGenerateReportName(),
                                "Automatically generated Report"
                        );
                    }else{
                        UMASException.throwWindow(ErrorType.INTERNAL, "Could not run process");
                    }
                }
            }

        };
    }

    public EventHandler<ActionEvent> handleRunBatch(){
        return actionEvent -> {
            switch (workflowType) {
                case RGB -> agisoftCaller.completeBuildRGB(
                    List.of(addPhotos, setBrightness, alignImages, optimizeCameras, buildPointCloud, buildDem,
                            buildOrthomosaic, exportDem, exportOrthomosaic, generateReport),
                    List.of(
                            getDefaultParameters(SetBrightness.values()),
                            getDefaultParameters(AlignImages.values()),
                            getDefaultParameters(OptimizeCameras.values()),
                            getDefaultParameters(BuildPointCloud.values()),
                            getDefaultParameters(BuildDem.values()),
                            getDefaultParameters(BuildOrthomosaic.values()),
                            getDefaultParameters(ExportDem.values()),
                            getDefaultParameters( ExportOrthomosaic.values())
                    ),
                    flight.getOriginFlightDirs(),
                    DirectoryUtils.figureAgisoftFilePath(flight),
                    Paths.get(DirectoryUtils.figureExportPath(flight), flight.getExportDemName()).toFile().getAbsolutePath(),
                    Paths.get(DirectoryUtils.figureExportPath(flight), flight.getExportOrthomosaicName()).toFile().getAbsolutePath(),
                    Paths.get(DirectoryUtils.figureReportPath(flight), flight.getGenerateReportName()).toFile().getAbsolutePath(),
                    flight.getGenerateReportName(),
                    "Automatically generated Report"
                );
                case IR, LIDAR, HYPERSPECTRAL, MULTISPECTRAL, RGB_PLUS_IR, RGB_PLUS_MULTISPECTRAL -> {
                }
            }
        };
    }

    public EventHandler<ActionEvent> handleRemove(StackPane stackPane, AgisoftTask agisoftTask){
        return actionEvent -> {
            agisoftCaller.removeComponent(
                    stackPane,
                    DirectoryUtils.figureAgisoftFilePath(this.flight),
                    agisoftTask,
                    this.workflowType
            );
            //TODO trigger refresh!
        };
    }


    public HashMap<String, String> getDefaultParameters(AgisoftParameter[] agisoftParameters) {
        HashMap<String, String> parameters = new HashMap<>();

        for(AgisoftParameter parameter : agisoftParameters){
            parameters.put(parameter.getId(), parameter.getChoices().get(parameter.getDefaultIndex()));
        }

        return parameters;
    }

    private String toRemoveFromAgisoftTask(AgisoftTask agisoftTask){
        return switch (agisoftTask) {
            case ADD_PHOTOS -> "Photos";
            case CALIBRATE_REFLECTANCE -> "Reflectance Calibration";
            case ALIGN_IMAGES -> "Alignment";
            case BUILD_POINT_CLOUD -> "Point Cloud";
            case BUILD_DEM -> "DEM";
            case BUILD_ORTHOMOSAIC -> "Orthomosaic";
            default -> "INVALID";
        };
    }

}
