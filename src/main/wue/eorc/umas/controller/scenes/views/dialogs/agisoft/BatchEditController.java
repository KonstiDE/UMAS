package wue.eorc.umas.controller.scenes.views.dialogs.agisoft;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import wue.eorc.umas.controller.scenes.main.DisplayController;
import wue.eorc.umas.controller.scenes.views.dialogs.StaticDialogController;
import wue.eorc.umas.enums.WorkflowType;
import wue.eorc.umas.exception.UMASException;
import wue.eorc.umas.utils.ItemSearcher;

public class BatchEditController implements StaticDialogController {

    private final WorkflowType workflowType;

    public BatchEditController(WorkflowType workflowType){
        this.workflowType = workflowType;
    }

    @Override
    public void init(Pane pane, DisplayController display, Dialog<String> dialog) throws UMASException {
        VBox vBox = ItemSearcher.getItemById("agisoft.batchedit.vbox", pane, VBox.class);

        Label title = new Label("Align Photos");
        vBox.getChildren().add(title);

        DialogPane dialogPane = (DialogPane) display.getSceneLoader().getScene("agisoft_align_photos");
        vBox.getChildren().add(dialogPane.getContent());

        StaticDialogController dialogController = new AlignImagesController();
        dialogController.init(dialogPane, display, dialog);



        Label title2 = new Label("Optimize Cameras");
        vBox.getChildren().add(title2);

        DialogPane dialogPane2 = (DialogPane) display.getSceneLoader().getScene("agisoft_optimize_cameras");
        vBox.getChildren().add(dialogPane2.getContent());

        StaticDialogController dialogController2 = new OptimizeCamerasController();
        dialogController2.init(dialogPane2, display, dialog);

    }

    @Override
    public String jsonCallback(ButtonType buttonType) {
        return "";
    }
}
