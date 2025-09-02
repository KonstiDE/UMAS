package wue.eorc.umas.controller.scenes.views.dialogs;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.layout.Pane;
import wue.eorc.umas.controller.scenes.main.DisplayController;
import wue.eorc.umas.exception.UMASException;

public class ClosingController implements StaticDialogController {

    @Override
    public void init(Pane pane, DisplayController display, Dialog<String> dialog) throws UMASException {  }

    @Override
    public void setupResultConverter(Dialog<String> dialog) {
        //Not really json

        if(buttonType == ButtonType.OK){
            return "true";
        } else if(buttonType == ButtonType.CANCEL){
            return null;
        }

        return null;
    }
}
