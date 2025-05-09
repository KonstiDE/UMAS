package wue.eorc.umas.controller.panes.views.panes.components;

import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import wue.eorc.umas.agisoft.AgisoftCaller;
import wue.eorc.umas.controller.listeners.AgisoftCallbackListener;
import wue.eorc.umas.enums.AgisoftTask;
import wue.eorc.umas.enums.WorkflowType;
import wue.eorc.umas.exception.UMASException;
import wue.eorc.umas.loader.SceneLoader;
import wue.eorc.umas.models.Flight;
import wue.eorc.umas.utils.Colors;
import wue.eorc.umas.utils.DirectoryUtils;
import wue.eorc.umas.utils.ItemSearcher;

public class ProcessActionsPreparer implements AgisoftCallbackListener {

    private final Flight flight;
    private final AnchorPane workflowPane;
    private final WorkflowType workflowType;

    public ProcessActionsPreparer(Flight flight, WorkflowType workflowType) {
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
    }

    public void setupWorkflowActions() throws UMASException {
        switch (this.workflowType){
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
        StackPane addPhotos = ItemSearcher.getItemById("processing.addphotos", this.workflowPane, StackPane.class);
        AgisoftCaller.addPhotosCheck(addPhotos, DirectoryUtils.figureAgisoftFilePath(this.flight), this);

//        if (!true) {
//            addPhotos.setFill(Colors.PROC_RED);
//            addPhotos.setCursor(Cursor.HAND);
//            addPhotos.setOnMouseClicked(_ignored -> {
//                boolean success = AgisoftCaller.addPhotos(DirectoryUtils.figureAgisoftFilePath(this.flight),
//                        this.flight.getImageTypes().keySet().stream()
//                                .filter(i -> this.workflowType.getImageTypes().contains(i))
//                                .map(i -> this.flight.getImageTypes().get(i)).toList());
//
//                if(!success) {
//                    UMASException.throwWindow(ErrorType.INTERNAL, "Could not add photos!");
//                }
//            });
//        } else {
//            addPhotos.setFill(Colors.PROC_GREEN);
//        }
    }

    private void setupSetBrightness(){

    }

    @Override
    public void callback(StackPane source, AgisoftTask task, boolean result) throws UMASException {
        switch (task){
            case ADD_PHOTOS_CHECK -> {
                Rectangle rectangle = ItemSearcher.getItemById("processing.rectangle", source, Rectangle.class);
                ProgressIndicator indicator = ItemSearcher.getItemById("processing.indicator", source, ProgressIndicator.class);

                indicator.setVisible(false);
                if(result){
                    rectangle.setFill(Colors.PROC_GREEN);
                }else{
                    rectangle.setFill(Colors.PROC_RED);
                }
            }
        }
    }

    public Flight getFlight() {
        return flight;
    }

    public AnchorPane getWorkflowPane() {
        return workflowPane;
    }

}
