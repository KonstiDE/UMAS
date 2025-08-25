package wue.eorc.umas.controller.scenes.views.dialogs;

import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.layout.Pane;
import wue.eorc.umas.controller.scenes.main.DisplayController;
import wue.eorc.umas.exception.UMASException;
import wue.eorc.umas.models.AgiSoftTaskBlueprint;

import java.util.HashMap;
import java.util.List;

public interface DynamicDialogController {

    void init(Pane pane, DisplayController display, Dialog<String> dialog, List<AgiSoftTaskBlueprint> data) throws UMASException;

    String jsonCallback(ButtonType buttonType);

}
