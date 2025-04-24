package controller.panes.views;

import controller.panes.mains.DisplayController;
import exception.UMASException;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Pane;
import models.Flight;

public interface DialogController {

    void init(Pane pane, DisplayController display) throws UMASException;

    String jsonCallback(ButtonType buttonType);
}
