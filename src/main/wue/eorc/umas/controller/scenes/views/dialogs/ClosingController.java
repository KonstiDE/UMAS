package wue.eorc.umas.controller.scenes.views.dialogs;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.layout.Pane;
import wue.eorc.umas.controller.scenes.main.DisplayController;
import wue.eorc.umas.exception.UMASException;

public class ClosingController implements StaticDialogController {

    @Override
    public void init(DisplayController display, Dialog<String> dialog) throws UMASException {
        setupResultConverter(dialog);
    }

    @Override
    public void setupResultConverter(Dialog<String> dialog) {
        //Not really json
        dialog.setResultConverter(buttonType -> {
            if(buttonType == ButtonType.OK){
                return "true";
            }
            return null;
        });
    }
}
