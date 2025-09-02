package wue.eorc.umas.controller.scenes.views.dialogs.agisoft;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
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
        GridPane grid = ItemSearcher.getItemById("agisoft.batchedit.grid", pane, GridPane.class);

        Label title = new Label("Align Photos");
        grid.addRow(0, title);

        DialogPane dialogPane = (DialogPane) display.getSceneLoader().getScene("agisoft_align_photos");
        dialogPane.getButtonTypes().clear();
        grid.addRow(1, dialogPane);

        StaticDialogController dialogController = new AlignImagesController();
        dialogController.init(dialogPane, display, dialog);



        Label title2 = new Label("Optimize Cameras");
        grid.addRow(2, title2);

        DialogPane dialogPane2 = (DialogPane) display.getSceneLoader().getScene("agisoft_optimize_cameras");
        dialogPane2.getButtonTypes().clear();
        grid.addRow(3, dialogPane2);

        StaticDialogController dialogController2 = new OptimizeCamerasController();
        dialogController2.init(dialogPane2, display, dialog);

    }

    @Override
    public String jsonCallback(ButtonType buttonType) {
        return "";
    }
}
