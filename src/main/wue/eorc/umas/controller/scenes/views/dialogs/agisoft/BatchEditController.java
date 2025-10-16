package wue.eorc.umas.controller.scenes.views.dialogs.agisoft;

import com.google.gson.Gson;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.util.Callback;
import wue.eorc.umas.controller.scenes.main.DisplayController;
import wue.eorc.umas.controller.scenes.views.dialogs.StaticDialogController;
import wue.eorc.umas.controller.scenes.views.panes.components.ProcessActionsPreparer;
import wue.eorc.umas.enums.WorkflowType;
import wue.eorc.umas.enums.agisoft.AgisoftDialog;
import wue.eorc.umas.enums.agisoft.AgisoftTask;
import wue.eorc.umas.exception.UMASException;
import wue.eorc.umas.utils.GsonTypeTokens;
import wue.eorc.umas.utils.ItemSearcher;

import java.util.HashMap;
import java.util.Map;

import static wue.eorc.umas.enums.agisoft.AgisoftTask.*;

public class BatchEditController implements StaticDialogController {

    private GridPane grid;
    private int i = 0;

    private final WorkflowType workflowType;
    private final ProcessActionsPreparer processActionsPreparer;

    public BatchEditController(WorkflowType workflowType, ProcessActionsPreparer processActionsPreparer){
        this.workflowType = workflowType;
        this.processActionsPreparer = processActionsPreparer;
    }

    public HashMap<AgisoftTask, Dialog<String>> dialogs = new HashMap<>();

    @Override
    public void init(DisplayController display, Dialog<String> dialog) throws UMASException {
        DialogPane pane = dialog.getDialogPane();

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
                case SET_BRIGHTNESS -> dialogs.put(SET_BRIGHTNESS, setupDialogRegion(display, AgisoftDialog.SET_BRIGHTNESS, new SetBrightnessController(processActionsPreparer)));
                case ALIGN_IMAGES -> dialogs.put(ALIGN_IMAGES, setupDialogRegion(display, AgisoftDialog.ALIGN_IMAGES, new AlignImagesController()));
                case OPTIMIZE_CAMERAS -> dialogs.put(OPTIMIZE_CAMERAS, setupDialogRegion(display, AgisoftDialog.OPTIMIZE_CAMERAS, new OptimizeCamerasController()));
                case BUILD_POINT_CLOUD -> dialogs.put(BUILD_POINT_CLOUD, setupDialogRegion(display, AgisoftDialog.BUILD_POINT_CLOUD, new BuildPointCloudController()));
                case BUILD_DEM -> dialogs.put(BUILD_DEM, setupDialogRegion(display, AgisoftDialog.BUILD_DEM, new BuildDemController()));
                case BUILD_ORTHOMOSAIC -> dialogs.put(BUILD_ORTHOMOSAIC, setupDialogRegion(display, AgisoftDialog.BUILD_ORTHOMOSAIC, new BuildOrthomosaicController()));
                case EXPORT_DEM -> dialogs.put(EXPORT_DEM, setupDialogRegion(display, AgisoftDialog.EXPORT_DEM, new ExportDemController()));
                case EXPORT_ORTHOMOSAIC -> dialogs.put(EXPORT_ORTHOMOSAIC, setupDialogRegion(display, AgisoftDialog.EXPORT_ORTHOMOSAIC, new ExportOrthomosaicController()));
            }

        }

        setupResultConverter(dialog);

    }

    private Dialog<String> setupDialogRegion(DisplayController display, AgisoftDialog agisoftDialogDefinition,
                                             StaticDialogController controller) throws UMASException {

        Dialog<String> newDialog = new Dialog<>();
        DialogPane dialogPane = (DialogPane) display.getSceneLoader().getScene(agisoftDialogDefinition.getDialogId());
        dialogPane.getButtonTypes().clear();

        newDialog.setDialogPane(dialogPane);

        grid.addRow(i, dialogPane);
        i++;

        controller.init(display, newDialog);

        return newDialog;
    }

    @Override
    public void setupResultConverter(Dialog<String> dialog) {
        dialog.setResultConverter(buttonType -> {
            if(buttonType == ButtonType.OK){
                Gson gson = new Gson();

                HashMap<String, String> completeResult = new HashMap<>();

                for (Map.Entry<AgisoftTask, Dialog<String>> entry : dialogs.entrySet()) {
                    Callback<ButtonType, String> converter = entry.getValue().getResultConverter();

                    if (converter != null) {
                        String value = converter.call(ButtonType.OK);
                        completeResult.put(entry.getKey().name(), value);
                    } else {
                        completeResult.put(entry.getKey().name(), null);
                    }
                }

                return gson.toJson(completeResult, GsonTypeTokens.hashmapToken);
            }else{
                return null;
            }
        });
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
