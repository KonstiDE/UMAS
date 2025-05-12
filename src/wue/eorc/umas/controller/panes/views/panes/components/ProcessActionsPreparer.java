package wue.eorc.umas.controller.panes.views.panes.components;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.util.Pair;
import wue.eorc.umas.agisoft.AgisoftCaller;
import wue.eorc.umas.controller.listeners.AgisoftCallbackListener;
import wue.eorc.umas.controller.panes.mains.DisplayController;
import wue.eorc.umas.controller.panes.views.panes.ShowProcessingController;
import wue.eorc.umas.enums.AgisoftTask;
import wue.eorc.umas.enums.ErrorType;
import wue.eorc.umas.enums.WorkflowType;
import wue.eorc.umas.exception.UMASException;
import wue.eorc.umas.loader.SceneLoader;
import wue.eorc.umas.models.Flight;
import wue.eorc.umas.utils.Colors;
import wue.eorc.umas.utils.DirectoryUtils;
import wue.eorc.umas.utils.ItemSearcher;

import java.util.Optional;

public class ProcessActionsPreparer implements AgisoftCallbackListener {

    private final Flight flight;
    private final AnchorPane workflowPane;
    private final WorkflowType workflowType;

    private final DisplayController display;
    private final ShowProcessingController showProcessingController;

    public ProcessActionsPreparer(Flight flight, WorkflowType workflowType, DisplayController display, ShowProcessingController showProcessingController) {
        this.flight = flight;
        this.workflowPane = switch (workflowType){
            case RGB -> (AnchorPane) SceneLoader.getAvailableScenes().get("rgb_workflow");
            case RGB_PLUS_MULTISPECTRAL -> (AnchorPane) SceneLoader.getAvailableScenes().get("rgb_workflow");
            case IR -> (AnchorPane) SceneLoader.getAvailableScenes().get("rgb_workflow");
            case RGB_PLUS_IR -> (AnchorPane) SceneLoader.getAvailableScenes().get("rgb_workflow");
            case HYPERSPECTRAL -> (AnchorPane) SceneLoader.getAvailableScenes().get("rgb_workflow");
            case LIDAR -> (AnchorPane) SceneLoader.getAvailableScenes().get("rgb_workflow");
            case MULTISPECTRAL -> (AnchorPane) SceneLoader.getAvailableScenes().get("rgb_workflow");
            case INVALID -> null;
        };
        this.workflowType = workflowType;
        this.display = display;
        this.showProcessingController = showProcessingController;
    }

    public void setupWorkflowActions() throws UMASException {
        switch (this.workflowType){
            case RGB -> {
                setupAddPhotos();
                setupSetBrightness();
                setupAlignPhotos();
                // ...
            }
            case IR -> {}
            case LIDAR -> {}
            case HYPERSPECTRAL -> {}
            case MULTISPECTRAL -> {}
        }
    }

    private void setupAddPhotos() throws UMASException {
        StackPane addPhotos = ItemSearcher.getItemById("processing.addphotos", this.workflowPane, StackPane.class);
        AgisoftCaller.addPhotosCheck(addPhotos, DirectoryUtils.figureAgisoftFilePath(this.flight), this);

        addPhotos.setCursor(Cursor.HAND);
        addPhotos.setOnMouseClicked(_ignored -> {
            AgisoftCaller.addPhotos(addPhotos, DirectoryUtils.figureAgisoftFilePath(this.flight),
                    this.flight.getImageTypes().keySet().stream()
                            .filter(i -> this.workflowType.getImageTypes().contains(i))
                            .map(i -> this.flight.getImageTypes().get(i)).toList(), this);
        });

    }

    private void setupSetBrightness() throws UMASException {
        StackPane setBrightness = ItemSearcher.getItemById("processing.setbrightness", this.workflowPane, StackPane.class);
        AgisoftCaller.setBrightnessCheck(setBrightness, DirectoryUtils.figureAgisoftFilePath(this.flight), this);

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

                    AgisoftCaller.setBrightness(setBrightness, DirectoryUtils.figureAgisoftFilePath(this.flight), brightness, contrast, this);
                } catch (NumberFormatException e) {
                    UMASException.throwWindow(ErrorType.USER, "Please provide non-decimal numbers!");
                }
            }

        });
    }

    private void setupAlignPhotos() throws UMASException {
        StackPane alignPhotos = ItemSearcher.getItemById("processing.alignimages", this.workflowPane, StackPane.class);
        AgisoftCaller.alignPhotosCheck(alignPhotos, DirectoryUtils.figureAgisoftFilePath(this.flight), this);

        alignPhotos.setCursor(Cursor.HAND);
        alignPhotos.setOnMouseClicked(_ignored -> {
            AgisoftCaller.alignPhotos(alignPhotos, DirectoryUtils.figureAgisoftFilePath(this.flight), this);
        });
    }

    @Override
    public void callback(StackPane source, AgisoftTask task, boolean result) throws UMASException {
        switch (task){
            case ADD_PHOTOS_CHECK, SET_BRIGHTNESS_CHECK, ALIGN_IMAGES_CHECK -> {
                Rectangle rectangle = ItemSearcher.getItemById("processing.rectangle", source, Rectangle.class);
                ProgressIndicator indicator = ItemSearcher.getItemById("processing.indicator", source, ProgressIndicator.class);

                indicator.setVisible(false);
                if(result){
                    rectangle.setFill(Colors.PROC_GREEN);
                    //this.showProcessingController.refresh(this.showProcessingController.getProcessingPaneRoot(), display);
                }else{
                    rectangle.setFill(Colors.PROC_RED);
                }
            }
        }
    }

    @Override
    public void progress(float f) {
        this.display.getRootController().getStatusController().updateStatus(f);
    }

    public Flight getFlight() {
        return flight;
    }

    public AnchorPane getWorkflowPane() {
        return workflowPane;
    }

}
