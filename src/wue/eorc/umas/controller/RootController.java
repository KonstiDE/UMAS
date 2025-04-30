package wue.eorc.umas.controller;

import wue.eorc.umas.controller.panes.mains.MenuController;
import wue.eorc.umas.controller.panes.mains.DisplayController;
import wue.eorc.umas.controller.panes.mains.StatusController;
import wue.eorc.umas.exception.UMASException;
import javafx.scene.control.MenuBar;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import wue.eorc.umas.loader.SceneLoader;

public class RootController {

    private final MenuController menuController;
    private final DisplayController splitPaneController;
    private final StatusController statusController;

    public RootController(SceneLoader sceneLoader, VBox vBox) throws UMASException {
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
