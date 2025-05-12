package wue.eorc.umas.controller.panes.mains;

import wue.eorc.umas.controller.RootController;
import wue.eorc.umas.controller.panes.views.dialogs.DialogController;
import wue.eorc.umas.controller.panes.views.panes.ViewController;
import wue.eorc.umas.enums.SplitPanePosition;
import wue.eorc.umas.exception.UMASException;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import wue.eorc.umas.models.Flight;

import java.util.Optional;

public class DisplayController {
    
    public SplitPane rootControl;
    public RootController rootController;

    private final AnchorPane left;
    private final ScrollPane center;
    private final AnchorPane right;

    public DisplayController(RootController rootController, SplitPane rootControl){
        this.rootController = rootController;
        this.rootControl = rootControl;

        this.left = (AnchorPane) rootControl.getItems().get(0);
        this.center = (ScrollPane) rootControl.getItems().get(1);
        this.right = (AnchorPane) rootControl.getItems().get(2);
    }

    public void switchSceneTo(SplitPanePosition key, Pane pane, ViewController trigger) {
        switch (key) {
            case LEFT -> setLeft(pane);
            case CENTER -> setCenter(pane);
            case RIGHT -> setRight(pane);
        }

        try{
            trigger.init(pane, this);
        }catch (UMASException e){
            e.printStackTrace();
        }

    }

    public Flight openFlightDialog(DialogPane pane, DialogController trigger) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setDialogPane(pane);
        dialog.setTitle("Add a new flight");

        try{
            trigger.init(pane, this, dialog);
        }catch (UMASException e){
            e.printStackTrace();
        }

        Optional<String> json = dialog.showAndWait();
        dialog.hide();
        dialog.close();

        return json.map(Flight::factoryFromJson).orElse(null);
    }

    public void openSettingsDialog(DialogPane pane, DialogController trigger) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setDialogPane(pane);
        dialog.setTitle("Settings");

        try{
            trigger.init(pane, this, dialog);
        }catch (UMASException e){
            e.printStackTrace();
        }

        dialog.showAndWait();
        dialog.hide();
        dialog.close();
    }

    private void setLeft(Pane pane){
        this.left.getChildren().clear();
        this.left.getChildren().add(pane);
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
}
