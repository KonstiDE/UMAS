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
    private SplitPane rootControl;

    private AnchorPane left;
    private ScrollPane center;
    private AnchorPane right;

    public DisplayController(SplitPane rootControl){
        this.rootControl = rootControl;
    }

    public boolean switchSceneTo(SplitPanePosition key, Pane pane, ViewController trigger) throws UMASException {
        trigger.init(pane, this);

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
