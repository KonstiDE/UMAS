package controller.panes.mains;

import controller.panes.views.ViewController;
import enums.SplitPanePosition;
import exception.UMASException;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

public class DisplayController {

    @FXML
    public SplitPane rootControl;

    private AnchorPane left;
    private ScrollPane center;
    private AnchorPane right;

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
