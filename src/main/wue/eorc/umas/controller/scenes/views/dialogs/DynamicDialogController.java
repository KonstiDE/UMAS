package wue.eorc.umas.controller.scenes.views.dialogs;

import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.layout.Pane;
import wue.eorc.umas.controller.scenes.main.DisplayController;
import wue.eorc.umas.exception.UMASException;

import java.util.HashMap;

public interface DynamicDialogController {

    void init(Pane pane, DisplayController display, Dialog<String> dialog, HashMap<Node, Node> data) throws UMASException;

    String jsonCallback(ButtonType buttonType);

}
