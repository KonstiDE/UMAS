package wue.eorc.umas.controller.scenes.views.dialogs;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.layout.Pane;
import wue.eorc.umas.controller.scenes.main.DisplayController;
import wue.eorc.umas.exception.UMASException;

import java.util.concurrent.CompletableFuture;

public interface StaticDialogController {

    void init(Pane pane, DisplayController display, Dialog<String> dialog) throws UMASException;

    void setupResultConverter(Dialog<String> dialog);

}
