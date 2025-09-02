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
import wue.eorc.umas.enums.WorkflowType;
import wue.eorc.umas.enums.agisoft.*;
import wue.eorc.umas.exception.UMASException;
import wue.eorc.umas.models.Flight;
import wue.eorc.umas.utils.DirectoryUtils;
import wue.eorc.umas.utils.GsonTypeTokens;
import wue.eorc.umas.utils.ItemSearcher;

import java.nio.file.Paths;
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
            case RGB_PLUS_MULTISPECTRAL -> (AnchorPane) display.getSceneLoader().getScene("rgb_workflow");
            case IR -> (AnchorPane) display.getSceneLoader().getScene("rgb_workflow");
            case RGB_PLUS_IR -> (AnchorPane) display.getSceneLoader().getScene("rgb_workflow");
            case HYPERSPECTRAL -> (AnchorPane) display.getSceneLoader().getScene("rgb_workflow");
            case LIDAR -> (AnchorPane) display.getSceneLoader().getScene("rgb_workflow");
            case MULTISPECTRAL -> (AnchorPane) display.getSceneLoader().getScene("rgb_workflow");
            case INVALID -> null;
        };
        this.workflowType = workflowType;
        this.display = display;
        this.agisoftCaller = agisoftCaller;

        setupWorkflowActions();
    }

    public void setupWorkflowActions() throws UMASException {
        switch (this.workflowType){
            case RGB, MULTISPECTRAL -> {
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
                agisoftCaller.addPhotos(addPhotos, DirectoryUtils.figureAgisoftFilePath(this.flight),
                        this.flight.getImageTypes().keySet().stream()
                                .filter(i -> this.workflowType.getImageTypes().contains(i))
                                .map(i -> this.flight.getImageTypes().get(i)).toList(),
                        this.workflowType);

            }else if (mouseEvent.getButton() == MouseButton.SECONDARY){

            }
        });

        return addPhotos;

    }

    private StackPane setupSetBrightness() throws UMASException {
        StackPane setBrightness = ItemSearcher.getItemById("processing." + SET_BRIGHTNESS, this.workflowPane, StackPane.class);

        setBrightness.setCursor(Cursor.HAND);
        setBrightness.setOnMouseClicked(_ignored -> {
            DialogPane dialogPane = (DialogPane) display.getSceneLoader().getScene("agisoft_set_brightness"); 
            Dialog<String> dialog = new UMASDialog(dialogPane, "Set Brightness", true, true);

            SetBrightnessController controller = new SetBrightnessController(this);
            try {
                controller.init(dialogPane, display, dialog);
            } catch (UMASException e) {
                throw new RuntimeException(e);
            }
            dialog.setResultConverter(controller::jsonCallback);

            Optional<String> result = dialog.showAndWait();

            if(result.isPresent()){
                HashMap<String, String> agisoftParameters = gson.fromJson(result.get(), GsonTypeTokens.hashmapToken);
                agisoftCaller.setBrightness(setBrightness, DirectoryUtils.figureAgisoftFilePath(this.flight), this.workflowType, agisoftParameters);
            }

        });

        return setBrightness;
    }

    public void callBrightnessEstimate(Pane pane) throws UMASException {
        agisoftCaller.setBrightnessEstimate(pane, DirectoryUtils.figureAgisoftFilePath(this.flight), this.workflowType);
    }

    private StackPane setupAlignPhotos() throws UMASException {
        StackPane alignPhotos = ItemSearcher.getItemById("processing." + ALIGN_IMAGES, this.workflowPane, StackPane.class);

        alignPhotos.setCursor(Cursor.HAND);
        alignPhotos.setOnMouseClicked(mouseEvent -> {
            if(mouseEvent.getButton() == MouseButton.PRIMARY){
                agisoftCaller.alignPhotos(alignPhotos, DirectoryUtils.figureAgisoftFilePath(this.flight),
                        this.workflowType, getDefaultParameters(AlignImages.values()));

            }else if(mouseEvent.getButton() == MouseButton.SECONDARY){
                setupModificationDialog(mouseEvent, event -> {
                    DialogPane parameterPane = (DialogPane)
                            display.getSceneLoader().getScene("agisoft_align_photos");

                    StaticDialogController controller = new AlignImagesController();

                    Dialog<String> dialog = new UMASDialog(parameterPane, "Align Images", true, true);
                    try {
                        controller.init(parameterPane, display, dialog);
                    } catch (UMASException e) {
                        throw new RuntimeException(e);
                    }
                    dialog.setResultConverter(controller::jsonCallback);
                    Optional<String> json = dialog.showAndWait();
                    dialog.hide();
                    dialog.close();

                    if (json.isPresent()){
                        agisoftCaller.alignPhotos(alignPhotos, DirectoryUtils.figureAgisoftFilePath(this.flight),
                                this.workflowType, retrieveManualChoice(json.orElse(null)));
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
                setupModificationDialog(mouseEvent, event -> {
                    DialogPane parameterPane = (DialogPane)
                            display.getSceneLoader().getScene("agisoft_optimize_cameras");

                    OptimizeCamerasController controller = new OptimizeCamerasController();

                    Dialog<String> dialog = new UMASDialog(parameterPane, "Optimize Camera Alignment", true, true);
                    try {
                        controller.init(parameterPane, display, dialog);
                    } catch (UMASException e) {
                        throw new RuntimeException(e);
                    }
                    dialog.setResultConverter(controller::jsonCallback);
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
                        this.workflowType, getDefaultParameters(BuildPointCloud.values()));

            }else if (mouseEvent.getButton() == MouseButton.SECONDARY){
                setupModificationDialog(mouseEvent, event -> {
                    DialogPane parameterPane = (DialogPane)
                            display.getSceneLoader().getScene("agisoft_build_point_cloud");

                    BuildPointCloudController controller = new BuildPointCloudController();

                    Dialog<String> dialog = new UMASDialog(parameterPane, "Build Dense (Point) Cloud", true, true);
                    try {
                        controller.init(parameterPane, display, dialog);
                    } catch (UMASException e) {
                        throw new RuntimeException(e);
                    }
                    dialog.setResultConverter(controller::jsonCallback);
                    Optional<String> json = dialog.showAndWait();
                    dialog.hide();
                    dialog.close();

                    if (json.isPresent()){
                        agisoftCaller.buildPointCloud(buildPointCloud, DirectoryUtils.figureAgisoftFilePath(this.flight),
                                this.workflowType, retrieveManualChoice(json.orElse(null)));
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
                        this.workflowType, getDefaultParameters(BuildDem.values()));

            } else if (mouseEvent.getButton() == MouseButton.SECONDARY){
                setupModificationDialog(mouseEvent, event -> {
                    DialogPane parameterPane = (DialogPane)
                            display.getSceneLoader().getScene("agisoft_build_dem");

                    BuildDemController controller = new BuildDemController();

                    Dialog<String> dialog = new UMASDialog(parameterPane, "Build DEM", true, true);
                    try {
                        controller.init(parameterPane, display, dialog);
                    } catch (UMASException e) {
                        throw new RuntimeException(e);
                    }
                    dialog.setResultConverter(controller::jsonCallback);

                    Optional<String> json = dialog.showAndWait();
                    dialog.hide();
                    dialog.close();

                    if (json.isPresent()){
                        agisoftCaller.buildDem(buildDem, DirectoryUtils.figureAgisoftFilePath(this.flight),
                                this.workflowType, retrieveManualChoice(json.orElse(null)));
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
                        this.workflowType, getDefaultParameters(BuildOrthomosaic.values()));

            }else{
                setupModificationDialog(mouseEvent, event -> {
                    DialogPane parameterPane = (DialogPane)
                            display.getSceneLoader().getScene("agisoft_build_orthomosaic");

                    BuildOrthomosaicController controller = new BuildOrthomosaicController();

                    Dialog<String> dialog = new UMASDialog(parameterPane, "Build Orthomosaic", true, true);
                    try {
                        controller.init(parameterPane, display, dialog);
                    } catch (UMASException e) {
                        throw new RuntimeException(e);
                    }
                    dialog.setResultConverter(controller::jsonCallback);

                    Optional<String> json = dialog.showAndWait();
                    dialog.hide();
                    dialog.close();

                    if (json.isPresent()){
                        agisoftCaller.buildOrthomosaic(buildOrthomosaic, DirectoryUtils.figureAgisoftFilePath(this.flight),
                                this.workflowType, retrieveManualChoice(json.orElse(null)));
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
                ).toFile().getAbsolutePath(), this.workflowType, getDefaultParameters(ExportDem.values()));

            } else if (mouseEvent.getButton() == MouseButton.SECONDARY) {
                setupModificationDialog(mouseEvent, event -> {
                    DialogPane parameterPane = (DialogPane)
                            display.getSceneLoader().getScene("agisoft_export_dem");

                    ExportDemController controller = new ExportDemController();

                    Dialog<String> dialog = new UMASDialog(parameterPane, "Export DEM - TIFF", true, true);
                    try {
                        controller.init(parameterPane, display, dialog);
                    } catch (UMASException e) {
                        throw new RuntimeException(e);
                    }
                    dialog.setResultConverter(controller::jsonCallback);

                    Optional<String> json = dialog.showAndWait();
                    dialog.hide();
                    dialog.close();

                    if (json.isPresent()){
                        agisoftCaller.exportDem(exportDem, DirectoryUtils.figureAgisoftFilePath(this.flight), Paths.get(
                                DirectoryUtils.figureExportPath(this.flight),
                                this.flight.getExportDemName()
                        ).toFile().getAbsolutePath(), this.workflowType, retrieveManualChoice(json.orElse(null)));
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
                ).toFile().getAbsolutePath(), this.workflowType, getDefaultParameters(ExportOrthomosaic.values()));

            } else if (mouseEvent.getButton() == MouseButton.SECONDARY) {
                setupModificationDialog(mouseEvent, event -> {
                    DialogPane parameterPane = (DialogPane)
                            display.getSceneLoader().getScene("agisoft_export_orthomosaic");

                    ExportOrthomosaicController controller = new ExportOrthomosaicController();

                    Dialog<String> dialog = new UMASDialog(parameterPane, "Export Orthomosaic - TIFF", true, true);
                    try {
                        controller.init(parameterPane, display, dialog);
                    } catch (UMASException e) {
                        throw new RuntimeException(e);
                    }
                    dialog.setResultConverter(controller::jsonCallback);

                    Optional<String> json = dialog.showAndWait();
                    dialog.hide();
                    dialog.close();

                    if (json.isPresent()){
                        agisoftCaller.exportOrtho(exportOrtho, DirectoryUtils.figureAgisoftFilePath(this.flight), Paths.get(
                                DirectoryUtils.figureExportPath(this.flight),
                                this.flight.getExportOrthomosaicName()
                        ).toFile().getAbsolutePath(), this.workflowType, retrieveManualChoice(json.orElse(null)));
                    }
                });
            }
        });

        return exportOrtho;
    }

    public StackPane setupGenerateReports() throws UMASException {
        StackPane generateReport = ItemSearcher.getItemById("processing." + GENERATE_REPORT, this.workflowPane, StackPane.class);

        generateReport.setCursor(Cursor.HAND);
        generateReport.setOnMouseClicked(_ignored -> {
            agisoftCaller.generateReport(
                    generateReport, DirectoryUtils.figureAgisoftFilePath(this.flight), Paths.get(
                            DirectoryUtils.figureReportPath(this.flight),
                            this.flight.getGenerateReportName()
                    ).toFile().getAbsolutePath(),
                    this.flight.getGenerateReportName(),
                    "Automatically generated Report",
                    this.workflowType
            );
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

    public void setupModificationDialog(MouseEvent mouseEvent, EventHandler<ActionEvent> eventHandler){
        final ContextMenu contextMenu = new ContextMenu();
        final MenuItem separator = new SeparatorMenuItem();

        final MenuItem modify = new MenuItem("Modify");
        final MenuItem modifyBatch = new MenuItem("Modify Batch");
        final MenuItem runBatch = new MenuItem("Run Batch");

        modify.setOnAction(eventHandler);
        modifyBatch.setOnAction(handleModifyBatch);
        runBatch.setOnAction(handleRunBatch);

        contextMenu.getItems().add(modify);
        contextMenu.getItems().add(modifyBatch);
        contextMenu.getItems().add(separator);
        contextMenu.getItems().add(runBatch);
        contextMenu.show(getWorkflowPane().getScene().getWindow(), mouseEvent.getScreenX(), mouseEvent.getScreenY());
    }

    public EventHandler<ActionEvent> handleModifyBatch = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent actionEvent) {

        }
    };

    public EventHandler<ActionEvent> handleRunBatch = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent actionEvent) {
            switch (workflowType){
                case RGB -> agisoftCaller.completeBuildRGB(
                        List.of(addPhotos, setBrightness, alignImages, optimizeCameras, buildPointCloud, buildDem,
                                buildOrthomosaic, exportDem, exportOrthomosaic, generateReport),
                        List.of(SetBrightness.values(), AlignImages.values(), OptimizeCameras.values(),
                                BuildPointCloud.values(), BuildDem.values(), BuildOrthomosaic.values(),
                                ExportDem.values(), ExportOrthomosaic.values()),
                        flight.getOriginFlightDirs(),
                        DirectoryUtils.figureAgisoftFilePath(flight),
                        Paths.get(DirectoryUtils.figureExportPath(flight), flight.getExportDemName()).toFile().getAbsolutePath(),
                        Paths.get(DirectoryUtils.figureExportPath(flight), flight.getExportOrthomosaicName()).toFile().getAbsolutePath(),
                        Paths.get(DirectoryUtils.figureReportPath(flight), flight.getGenerateReportName()).toFile().getAbsolutePath(),
                        flight.getGenerateReportName(),
                        "Automatically generated Report"
                );
                case IR, LIDAR, HYPERSPECTRAL, MULTISPECTRAL, RGB_PLUS_IR, RGB_PLUS_MULTISPECTRAL -> {}
            }
        }
    };


    public HashMap<String, String> getDefaultParameters(AgisoftParameter[] agisoftParameters) {
        HashMap<String, String> parameters = new HashMap<>();

        for(AgisoftParameter parameter : agisoftParameters){
            parameters.put(parameter.getId(), parameter.getChoices().get(parameter.getDefaultIndex()));
        }

        return parameters;
    }

}
