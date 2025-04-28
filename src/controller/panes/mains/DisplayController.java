package controller.panes.mains;

import controller.panes.views.dialogs.DialogController;
import controller.panes.views.panes.ViewController;
import enums.SplitPanePosition;
import exception.UMASException;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Window;
import models.Flight;

import java.util.Optional;

public class DisplayController {
    
    public SplitPane rootControl;

    private final AnchorPane left;
    private final ScrollPane center;
    private final AnchorPane right;

    public DisplayController(SplitPane rootControl){
        this.rootControl = rootControl;

        this.left = (AnchorPane) rootControl.getItems().getFirst();
        this.center = (ScrollPane) rootControl.getItems().get(1);
        this.right = (AnchorPane) rootControl.getItems().getLast();
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

    public Flight openDialog(DialogPane pane, DialogController trigger) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setDialogPane(pane);
        dialog.setTitle("Add a new flight");
        dialog.setResultConverter(trigger::jsonCallback);

        Window window = dialog.getDialogPane().getScene().getWindow();
        window.setOnCloseRequest(_ -> window.hide());

        try{
            trigger.init(pane, this);
        }catch (UMASException e){
            e.printStackTrace();
        }

        Optional<String> json = dialog.showAndWait();

        return json.map(Flight::factoryFromJson).orElse(null);
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

}
