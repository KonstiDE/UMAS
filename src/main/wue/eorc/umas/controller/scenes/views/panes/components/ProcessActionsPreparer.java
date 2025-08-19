package wue.eorc.umas.controller.scenes.views.panes.components;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.util.Pair;
import wue.eorc.umas.agisoft.AgisoftCaller;
import wue.eorc.umas.controller.scenes.main.DisplayController;
import wue.eorc.umas.controller.scenes.views.panes.ShowProcessingController;
import wue.eorc.umas.enums.ErrorType;
import wue.eorc.umas.enums.WorkflowType;
import wue.eorc.umas.exception.UMASException;
import wue.eorc.umas.models.Flight;
import wue.eorc.umas.utils.DirectoryUtils;
import wue.eorc.umas.utils.ItemSearcher;

import java.nio.file.Paths;
import java.util.Optional;

import static wue.eorc.umas.enums.AgisoftTask.*;

public class ProcessActionsPreparer {

    private final AgisoftCaller agisoftCaller;

    private final Flight flight;
    private final AnchorPane workflowPane;
    private final WorkflowType workflowType;

    private final DisplayController display;
    private final ShowProcessingController showProcessingController;

    public ProcessActionsPreparer(Flight flight, WorkflowType workflowType, DisplayController display, ShowProcessingController showProcessingController, AgisoftCaller agisoftCaller) {
        this.flight = flight;
        this.workflowPane = switch (workflowType){
            case RGB -> (AnchorPane) display.getRootController().getSceneLoader().getScene("rgb_workflow");
            case RGB_PLUS_MULTISPECTRAL -> (AnchorPane) display.getRootController().getSceneLoader().getScene("rgb_workflow");
            case IR -> (AnchorPane) display.getRootController().getSceneLoader().getScene("rgb_workflow");
            case RGB_PLUS_IR -> (AnchorPane) display.getRootController().getSceneLoader().getScene("rgb_workflow");
            case HYPERSPECTRAL -> (AnchorPane) display.getRootController().getSceneLoader().getScene("rgb_workflow");
            case LIDAR -> (AnchorPane) display.getRootController().getSceneLoader().getScene("rgb_workflow");
            case MULTISPECTRAL -> (AnchorPane) display.getRootController().getSceneLoader().getScene("rgb_workflow");
            case INVALID -> null;
        };
        this.workflowType = workflowType;
        this.display = display;
        this.showProcessingController = showProcessingController;
        this.agisoftCaller = agisoftCaller;
    }

    public void setupWorkflowActions() throws UMASException {
        switch (this.workflowType){
            case RGB -> {
                setupAddPhotos();
                setupSetBrightness();
                setupAlignPhotos();
                setupOptimizeCameras();
                setupBuildPointCloud();
                setupBuildDem();
                setupBuildOrthomosaic();
                setupExportDem();
                setupExportOrthomosaic();
                setupGenerateReports();

                setupCheckProject();
            }
            case IR -> {}
            case LIDAR -> {}
            case HYPERSPECTRAL -> {}
            case MULTISPECTRAL -> {}
        }
    }

    private void setupCheckProject() throws UMASException {
        AnchorPane workflowParent = ItemSearcher.getItemById("workflowpane", this.workflowPane, AnchorPane.class);
        agisoftCaller.checkProject(workflowParent, DirectoryUtils.figureAgisoftFilePath(this.flight));
    }

    private void setupAddPhotos() throws UMASException {
        StackPane addPhotos = ItemSearcher.getItemById("processing." + ADD_PHOTOS, this.workflowPane, StackPane.class);

        addPhotos.setCursor(Cursor.HAND);
        addPhotos.setOnMouseClicked(_ignored -> {
            agisoftCaller.addPhotos(addPhotos, DirectoryUtils.figureAgisoftFilePath(this.flight),
                    this.flight.getImageTypes().keySet().stream()
                            .filter(i -> this.workflowType.getImageTypes().contains(i))
                            .map(i -> this.flight.getImageTypes().get(i)).toList());
        });

    }

    private void setupSetBrightness() throws UMASException {
        StackPane setBrightness = ItemSearcher.getItemById("processing." + SET_BRIGHTNESS, this.workflowPane, StackPane.class);

        setBrightness.setCursor(Cursor.HAND);
        setBrightness.setOnMouseClicked(_ignored -> {
            Dialog<Pair<String, String>> dialog = new Dialog<>();
            dialog.setTitle("Set Parameters");

            ButtonType loginButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

            GridPane gridPane = new GridPane();
            gridPane.setHgap(10);
            gridPane.setVgap(10);
            gridPane.setPadding(new Insets(20, 150, 10, 10));

            TextField from = new TextField();
            from.setPromptText("Brightness");
            TextField to = new TextField();
            to.setPromptText("Contrast");

            gridPane.add(new Label("Brightness"), 0, 0);
            gridPane.add(new Label("Contrast"), 1, 0);

            gridPane.add(from, 0, 1);
            gridPane.add(to, 1, 1);

            dialog.getDialogPane().setContent(gridPane);

            Platform.runLater(from::requestFocus);

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == loginButtonType) {
                    return new Pair<>(from.getText(), to.getText());
                }
                return null;
            });

            Optional<Pair<String, String>> result = dialog.showAndWait();

            if(result.isPresent()){
                try {
                    int brightness = Integer.parseInt(result.get().getKey());
                    int contrast = Integer.parseInt(result.get().getValue());

                    agisoftCaller.setBrightness(setBrightness, DirectoryUtils.figureAgisoftFilePath(this.flight), brightness, contrast);
                } catch (NumberFormatException e) {
                    UMASException.throwWindow(ErrorType.USER, "Please provide non-decimal numbers!");
                }
            }

        });
    }

    private void setupAlignPhotos() throws UMASException {
        StackPane alignPhotos = ItemSearcher.getItemById("processing." + ALIGN_IMAGES, this.workflowPane, StackPane.class);

        alignPhotos.setCursor(Cursor.HAND);
        alignPhotos.setOnMouseClicked(_ignored -> {
            agisoftCaller.alignPhotos(alignPhotos, DirectoryUtils.figureAgisoftFilePath(this.flight));
        });
    }

    private void setupOptimizeCameras() throws UMASException {
        StackPane optimizeCameras = ItemSearcher.getItemById("processing." + OPTIMIZE_CAMERAS, this.workflowPane, StackPane.class);

        optimizeCameras.setCursor(Cursor.HAND);
        optimizeCameras.setOnMouseClicked(_ignored -> {
            agisoftCaller.optimizeCameras(optimizeCameras, DirectoryUtils.figureAgisoftFilePath(this.flight));
        });
    }

    private void setupBuildPointCloud() throws UMASException {
        StackPane buildPointCloud = ItemSearcher.getItemById("processing." + BUILD_POINT_CLOUD, this.workflowPane, StackPane.class);

        buildPointCloud.setCursor(Cursor.HAND);
        buildPointCloud.setOnMouseClicked(_ignored -> {
            agisoftCaller.buildPointCloud(buildPointCloud, DirectoryUtils.figureAgisoftFilePath(this.flight));
        });
    }

    private void setupBuildDem() throws UMASException {
        StackPane buildDem = ItemSearcher.getItemById("processing." + BUILD_DEM, this.workflowPane, StackPane.class);

        buildDem.setCursor(Cursor.HAND);
        buildDem.setOnMouseClicked(_ignored -> {
            agisoftCaller.buildDem(buildDem, DirectoryUtils.figureAgisoftFilePath(this.flight));
        });
    }

    private void setupBuildOrthomosaic() throws UMASException {
        StackPane buildOrthomosaic = ItemSearcher.getItemById("processing." + BUILD_ORTHOMOSAIC, this.workflowPane, StackPane.class);

        buildOrthomosaic.setCursor(Cursor.HAND);
        buildOrthomosaic.setOnMouseClicked(_ignored -> {
            agisoftCaller.buildOrthomosaic(buildOrthomosaic, DirectoryUtils.figureAgisoftFilePath(this.flight));
        });
    }

    private void setupExportDem() throws UMASException {
        StackPane exportDem = ItemSearcher.getItemById("processing." + EXPORT_DEM, this.workflowPane, StackPane.class);

        exportDem.setCursor(Cursor.HAND);
        exportDem.setOnMouseClicked(_ignored -> {
            agisoftCaller.exportDem(exportDem, DirectoryUtils.figureAgisoftFilePath(this.flight), Paths.get(
                    DirectoryUtils.figureExportPath(this.flight),
                    this.flight.getExportDemName()
            ).toFile().getAbsolutePath());
        });
    }

    private void setupExportOrthomosaic() throws UMASException {
        StackPane exportOrtho = ItemSearcher.getItemById("processing." + EXPORT_ORTHOMOSAIC, this.workflowPane, StackPane.class);

        exportOrtho.setCursor(Cursor.HAND);
        exportOrtho.setOnMouseClicked(_ignored -> {
            agisoftCaller.exportOrtho(exportOrtho, DirectoryUtils.figureAgisoftFilePath(this.flight), Paths.get(
                    DirectoryUtils.figureExportPath(this.flight),
                    this.flight.getExportOrthomosaicName()
            ).toFile().getAbsolutePath());
        });
    }

    public void setupGenerateReports() throws UMASException {
        StackPane generateReport = ItemSearcher.getItemById("processing." + GENERATE_REPORT, this.workflowPane, StackPane.class);

        generateReport.setCursor(Cursor.HAND);
        generateReport.setOnMouseClicked(_ignored -> {
            agisoftCaller.generateReport(
                    generateReport, DirectoryUtils.figureAgisoftFilePath(this.flight), Paths.get(
                            DirectoryUtils.figureReportPath(this.flight),
                            this.flight.getGenerateReportName()
                    ).toFile().getAbsolutePath(),
                    this.flight.getGenerateReportName(),
                    "Automatically generated Report"
            );
        });
    }


    public Flight getFlight() {
        return flight;
    }

    public AnchorPane getWorkflowPane() {
        return workflowPane;
    }

}
