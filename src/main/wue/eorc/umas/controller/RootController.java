package wue.eorc.umas.controller;

import javafx.scene.control.MenuBar;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import wue.eorc.umas.controller.scenes.main.*;
import wue.eorc.umas.exception.UMASException;
import wue.eorc.umas.loader.SceneLoader;

import java.io.FileNotFoundException;

public class RootController {

    private final MenuController menuController;
    private final DisplayController splitPaneController;
    private final StatusController statusController;

    private final SceneLoader sceneLoader;

    public RootController(VBox vBox, SceneLoader sceneLoader) throws UMASException, FileNotFoundException {
        this.menuController = new MenuController(this, (MenuBar) vBox.getChildren().get(0));
        this.splitPaneController = new DisplayController(this, (SplitPane) vBox.getChildren().get(1));
        this.statusController = new StatusController((HBox) vBox.getChildren().get(2), this.splitPaneController);

        this.sceneLoader = sceneLoader;
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

    public SceneLoader getSceneLoader() { return sceneLoader; }
}
