package wue.eorc.umas.controller.scenes.views.dialogs.agisoft;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import wue.eorc.umas.controller.scenes.main.DisplayController;
import wue.eorc.umas.controller.scenes.views.dialogs.StaticDialogController;
import wue.eorc.umas.controller.scenes.views.panes.components.ProcessActionsPreparer;
import wue.eorc.umas.enums.WorkflowType;
import wue.eorc.umas.enums.agisoft.AgisoftDialog;
import wue.eorc.umas.enums.agisoft.AgisoftTask;
import wue.eorc.umas.exception.UMASException;
import wue.eorc.umas.utils.ItemSearcher;

public class BatchEditController implements StaticDialogController {

    private GridPane grid;
    private int i = 0;

    private final WorkflowType workflowType;
    private final ProcessActionsPreparer processActionsPreparer;

    public BatchEditController(WorkflowType workflowType, ProcessActionsPreparer processActionsPreparer){
        this.workflowType = workflowType;
        this.processActionsPreparer = processActionsPreparer;
    }

    @Override
    public void init(Pane pane, DisplayController display, Dialog<String> dialog) throws UMASException {
        grid = ItemSearcher.getItemById("agisoft.batchedit.grid", pane, GridPane.class);

        for(AgisoftTask agisoftTask : this.workflowType.getAgisoftTasks()){
            Label title = new Label(toTitleCase(
                    agisoftTask.name().replace("_", " ").toLowerCase()
            ));
            Font font = new Font(13);
            title.setFont(font);
            grid.addRow(i, title);
            i++;

            switch (agisoftTask){
                case SET_BRIGHTNESS -> setupDialogRegion(display, AgisoftDialog.SET_BRIGHTNESS, new SetBrightnessController(processActionsPreparer), dialog);
                case ALIGN_IMAGES -> setupDialogRegion(display, AgisoftDialog.ALIGN_IMAGES, new AlignImagesController(), dialog);
                case OPTIMIZE_CAMERAS -> setupDialogRegion(display, AgisoftDialog.OPTIMIZE_CAMERAS, new OptimizeCamerasController(), dialog);
                case BUILD_POINT_CLOUD -> setupDialogRegion(display, AgisoftDialog.BUILD_POINT_CLOUD, new BuildPointCloudController(), dialog);
                case BUILD_DEM -> setupDialogRegion(display, AgisoftDialog.BUILD_DEM, new BuildDemController(), dialog);
                case BUILD_ORTHOMOSAIC -> setupDialogRegion(display, AgisoftDialog.BUILD_ORTHOMOSAIC, new BuildOrthomosaicController(), dialog);
                case EXPORT_DEM -> setupDialogRegion(display, AgisoftDialog.EXPORT_DEM, new ExportDemController(), dialog);
                case EXPORT_ORTHOMOSAIC -> setupDialogRegion(display, AgisoftDialog.EXPORT_ORTHOMOSAIC, new ExportOrthomosaicController(), dialog);
            }

        }

    }

    @Override
    public String jsonCallback(ButtonType buttonType) {
        return "";
    }

    private void setupDialogRegion(DisplayController display, AgisoftDialog agisoftDialogDefinition,
                                   StaticDialogController controller, Dialog<String> dialog) throws UMASException {

        DialogPane dialogPane = (DialogPane) display.getSceneLoader().getScene(agisoftDialogDefinition.getDialogId());
        dialogPane.getButtonTypes().clear();

        grid.addRow(i, dialogPane);
        i++;

        controller.init(dialogPane, display, dialog);
    }

    private String toTitleCase(String input) {
        StringBuilder titleCase = new StringBuilder(input.length());
        boolean nextTitleCase = true;

        for (char c : input.toCharArray()) {
            if (Character.isSpaceChar(c)) {
                nextTitleCase = true;
            } else if (nextTitleCase) {
                c = Character.toTitleCase(c);
                nextTitleCase = false;
            }

            titleCase.append(c);
        }

        return titleCase.toString();
    }

}
