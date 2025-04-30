package wue.eorc.umas.controller.panes.views.dialogs;

import wue.eorc.umas.controller.panes.mains.DisplayController;
import wue.eorc.umas.exception.UMASException;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.layout.Pane;

public interface DialogController {

    void init(Pane pane, DisplayController display, Dialog<String> dialog) throws UMASException;

    String jsonCallback(ButtonType buttonType);


}
