package controller;

import controller.elements.MenuController;
import controller.elements.SplitPaneController;
import controller.elements.StatusController;
import javafx.fxml.FXML;
import javafx.scene.control.Control;
import javafx.scene.control.MenuBar;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class RootController {

    @FXML
    private VBox vBox;

    private final MenuController menuController;
    private final SplitPaneController splitPaneController;
    private final StatusController statusController;

    public RootController(VBox vBox){
        this.vBox = vBox;

        this.menuController = new MenuController((MenuBar) vBox.getChildren().getFirst());
        this.splitPaneController = new SplitPaneController((SplitPane) vBox.getChildren().get(1));
        this.statusController = new StatusController((HBox) vBox.getChildren().getLast());

    }

    public MenuController getMenuController() {
        return menuController;
    }

    public SplitPaneController getSplitPaneController() {
        return splitPaneController;
    }

    public StatusController getStatusController() {
        return statusController;
    }
}
