package wue.eorc.umas.controller.panes.views.panes.components;

import javafx.scene.Cursor;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import wue.eorc.umas.agisoft.AgisoftCaller;
import wue.eorc.umas.enums.ErrorType;
import wue.eorc.umas.enums.ImageType;
import wue.eorc.umas.enums.WorkflowType;
import wue.eorc.umas.exception.UMASException;
import wue.eorc.umas.loader.SceneLoader;
import wue.eorc.umas.models.Flight;
import wue.eorc.umas.utils.Colors;
import wue.eorc.umas.utils.DirectoryUtils;
import wue.eorc.umas.utils.ItemSearcher;

import java.util.List;

public class ProcessActionsPreparer {

    private final Flight flight;
    private final AnchorPane workflowPane;
    private final List<ImageType> imageType;

    private final WorkflowType workflowType;

    public ProcessActionsPreparer(Flight flight, ImageType imageType) {
        this.flight = flight;
        this.workflowPane = switch (imageType){
            case RGB -> (AnchorPane) SceneLoader.getAvailableScenes().get("rgb_workflow");
            case IR -> (AnchorPane) SceneLoader.getAvailableScenes().get("ir_workflow");
            case HYPERSPECTRAL -> (AnchorPane) SceneLoader.getAvailableScenes().get("rgb_workflow");
            case LIDAR -> (AnchorPane) SceneLoader.getAvailableScenes().get("rgb_workflow");
            case MULTISPECTRAL -> (AnchorPane) SceneLoader.getAvailableScenes().get("rgb_workflow");
        };
        this.imageType = imageType;
    }

    public void setupWorkflowActions() throws UMASException {
        switch (this.imageType){
            case RGB -> {
                setupAddPhotos();
                setupSetBrightness();
                // ...
            }
            case IR -> {}
            case LIDAR -> {}
            case HYPERSPECTRAL -> {}
            case MULTISPECTRAL -> {}
        }
    }

    private void setupAddPhotos() throws UMASException {
        Rectangle addPhotos = ItemSearcher.getItemById("processing.rectangle.addphotos", this.workflowPane, Rectangle.class);

        if (!AgisoftCaller.addPhotosCheck(DirectoryUtils.figureAgisoftFilePath(this.flight))) {
            addPhotos.setFill(Colors.PROC_RED);
            addPhotos.setCursor(Cursor.HAND);
            addPhotos.setOnMouseClicked(_ignored -> {
                boolean success = AgisoftCaller.addPhotos(DirectoryUtils.figureAgisoftFilePath(this.flight),
                        this.flight.getImageTypes().keySet().stream().filter(i -> i == this.imageType).map(i -> this.flight.getImageTypes().get(i)).toList());
                if(!success) {
                    UMASException.throwWindow(ErrorType.INTERNAL, "Could not add photos!");
                }
            });
        } else {
            addPhotos.setFill(Colors.PROC_GREEN);
        }
    }

    private void setupSetBrightness(){

    }

    public Flight getFlight() {
        return flight;
    }

    public AnchorPane getWorkflowPane() {
        return workflowPane;
    }

    public ImageType getImageType() {
        return imageType;
    }

}
