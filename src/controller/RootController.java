package controller;

import controller.panes.mains.MenuController;
import controller.panes.mains.DisplayController;
import controller.panes.mains.StatusController;
import exception.UMASException;
import javafx.fxml.FXML;
import javafx.scene.control.MenuBar;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class RootController {

    @FXML
    private VBox vBox;

    private final MenuController menuController;
    private final DisplayController splitPaneController;
    private final StatusController statusController;

    public RootController(VBox vBox) throws UMASException {
        this.vBox = vBox;

        this.menuController = new MenuController(this, (MenuBar) vBox.getChildren().getFirst());
        this.splitPaneController = new DisplayController((SplitPane) vBox.getChildren().get(1));
        this.statusController = new StatusController((HBox) vBox.getChildren().getLast());

    }

    public MenuController getMenuController() {
        return menuController;
    }

    public DisplayController getDisplayController() {
        return splitPaneController;
    }

    public StatusController getStatusController() {
        return statusController;
    }
}
