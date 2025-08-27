package wue.eorc.umas.controller.scenes.views.dialogs.agisoft;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.layout.Pane;
import wue.eorc.umas.controller.scenes.main.DisplayController;
import wue.eorc.umas.controller.scenes.views.dialogs.StaticDialogController;
import wue.eorc.umas.exception.UMASException;

public class AlignImagesController implements StaticDialogController {

    @Override
    public void init(Pane pane, DisplayController display, Dialog<String> dialog) throws UMASException {

    }

    @Override
    public String jsonCallback(ButtonType buttonType) {
        return "";
    }
}
