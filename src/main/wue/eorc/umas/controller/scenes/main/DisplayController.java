package wue.eorc.umas.controller.scenes.main;

import javafx.scene.layout.StackPane;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import wue.eorc.umas.controller.RootController;
import wue.eorc.umas.controller.customs.UMASDialog;
import wue.eorc.umas.controller.scenes.views.dialogs.StaticDialogController;
import wue.eorc.umas.controller.scenes.views.dialogs.agisoft.AlignImagesController;
import wue.eorc.umas.controller.scenes.views.panes.ViewController;
import wue.eorc.umas.enums.ErrorType;
import wue.eorc.umas.enums.SplitPanePosition;
import wue.eorc.umas.exception.UMASException;
import wue.eorc.umas.loader.SceneLoader;
import wue.eorc.umas.models.Flight;

import java.io.FileNotFoundException;
import java.util.Optional;

public class DisplayController {
    
    public SplitPane rootControl;
    public RootController rootController;
    public MapController mapController;

    private final AnchorPane left;
    private final StackPane map;

    private final ScrollPane center;
    private final AnchorPane right;

    public DisplayController(RootController rootController, SplitPane rootControl) throws FileNotFoundException {
        this.rootController = rootController;
        this.rootControl = rootControl;

        this.left = (AnchorPane) ((SplitPane) rootControl.getItems().get(0)).getItems().get(0);
        this.map = (StackPane) ((SplitPane) rootControl.getItems().get(0)).getItems().get(1);
        this.center = (ScrollPane) rootControl.getItems().get(1);
        this.right = (AnchorPane) rootControl.getItems().get(2);

        this.mapController = new MapController(map);
    }

    public void switchSceneTo(SplitPanePosition key, Pane pane, ViewController trigger) {
        switch (key) {
            case LEFT -> setLeft(pane);
            case CENTER -> setCenter(pane);
            case RIGHT -> setRight(pane);
        }

        if(trigger != null){
            try{
                trigger.init(pane, this);
            }catch (UMASException _ignored){
                UMASException.throwWindow(ErrorType.INTERNAL, "Something went terribly wrong. Please contact Caipi!");
            }
        }

    }

    public Flight openFlightDialog(DialogPane pane, StaticDialogController trigger) {
        Dialog<String> dialog = new UMASDialog(pane, "New flight", true, false);
        dialog.initModality(Modality.NONE);

        try{
            trigger.init(this, dialog);
        }catch (UMASException e){
            UMASException.throwWindow(ErrorType.INTERNAL, "Could not open the flight dialog! Please restart the application.");
        }

        Optional<String> json = dialog.showAndWait();
        dialog.hide();
        dialog.close();

        return json.map(Flight::factoryFromJson).orElse(null);
    }

    public void openSettingsDialog(DialogPane pane, StaticDialogController trigger) {
        Dialog<String> dialog = new UMASDialog(pane, "Settings", false, false);

        try{
            trigger.init(this, dialog);
        }catch (UMASException e){
            UMASException.throwWindow(ErrorType.INTERNAL, "Could not open the settings dialog! Please restart the application.");
        }

        dialog.showAndWait();
        dialog.hide();
        dialog.close();
    }

    private void setLeft(Pane pane){
        this.left.getChildren().clear();
        this.left.getChildren().add(pane);
    }

    private void setMap(Pane pane){
        this.map.getChildren().clear();
        this.map.getChildren().add(pane);
    }

    private void setCenter(Pane pane){
        ((AnchorPane) this.center.getContent()).getChildren().clear();
        ((AnchorPane) this.center.getContent()).getChildren().add(pane);
    }

    private void setRight(Pane pane){
        this.right.getChildren().clear();
        this.right.getChildren().add(pane);
    }

    public RootController getRootController() {
        return rootController;
    }

    public MapController getMapController() {
        return mapController;
    }

    public SceneLoader getSceneLoader(){
        return getRootController().getSceneLoader();
    }
}
