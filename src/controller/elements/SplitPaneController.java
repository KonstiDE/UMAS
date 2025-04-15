package controller.elements;

import enums.SplitPanePosition;
import javafx.fxml.FXML;
import javafx.scene.control.Control;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

public class SplitPaneController {

    @FXML
    private SplitPane rootControl;

    private AnchorPane left;
    private ScrollPane center;
    private AnchorPane right;

    public SplitPaneController(SplitPane rootControl){
        this.rootControl = rootControl;
        this.left = (AnchorPane) rootControl.getItems().getFirst();
        this.center = (ScrollPane) rootControl.getItems().get(1);
        this.right = (AnchorPane) rootControl.getItems().getLast();
    }

    public boolean switchSceneTo(SplitPanePosition key, Pane pane){
        return switch (key){
            case LEFT -> setLeft(pane);
            case CENTER -> setCenter(pane);
            case RIGHT -> setRight(pane);
        };
    }

    private boolean setLeft(Pane pane){
        this.left.getChildren().clear();
        return this.left.getChildren().add(pane);
    }

    private boolean setCenter(Pane pane){
        this.left.getChildren().clear();
        return this.left.getChildren().add(pane);
    }

    private boolean setRight(Pane pane){
        this.left.getChildren().clear();
        return this.left.getChildren().add(pane);
    }

}
