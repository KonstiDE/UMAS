package controller.panes.views.dialogs;

import controller.panes.mains.DisplayController;
import exception.UMASException;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.layout.Pane;

import java.io.IOException;

public interface DialogController {

    void init(Pane pane, DisplayController display, Dialog<String> dialog) throws UMASException;

    String jsonCallback(ButtonType buttonType);


}
