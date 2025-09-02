package wue.eorc.umas.controller.scenes.views.dialogs.agisoft;

import com.google.gson.Gson;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import wue.eorc.umas.controller.customs.UMASDialog;
import wue.eorc.umas.controller.scenes.main.DisplayController;
import wue.eorc.umas.controller.scenes.views.dialogs.CoordinateSelector;
import wue.eorc.umas.controller.scenes.views.dialogs.StaticDialogController;
import wue.eorc.umas.enums.ErrorType;
import wue.eorc.umas.enums.agisoft.BuildDem;
import wue.eorc.umas.exception.UMASException;
import wue.eorc.umas.models.CoordinateSystem;
import wue.eorc.umas.utils.AgisoftParamInitiator;
import wue.eorc.umas.utils.GsonTypeTokens;
import wue.eorc.umas.utils.ItemSearcher;

import java.util.HashMap;
import java.util.Optional;

public class BuildDemController implements StaticDialogController {

    private Label epsgLabel;
    private Button epsgSelect;

    private ComboBox<String> sourceData;
    private ComboBox<String> quality;
    private ComboBox<String> interpolation;

    @Override
    public void init(Pane pane, DisplayController display, Dialog<String> dialog) throws UMASException {
        String prefix = "agisoft.builddem.";

        epsgLabel = ItemSearcher.getItemById(prefix + "epsglabel", pane, Label.class);
        AgisoftParamInitiator.initLabel(epsgLabel, BuildDem.COORDINATE_SYSTEM);

        epsgSelect = ItemSearcher.getItemById(prefix + "epsgselect", pane, Button.class);

        epsgSelect.setOnAction(ae -> {
            DialogPane dialogPane = (DialogPane) display.getSceneLoader().getScene("coordinate_system_select");
            Dialog<String> coordinateDialog = new UMASDialog(dialogPane, "Select coordinate system", true, true);
            CoordinateSelector controller = new CoordinateSelector();

            try {
                controller.init(dialogPane, display, coordinateDialog);
            } catch (UMASException e) {
                UMASException.throwWindow(ErrorType.INTERNAL, "Could not setup coordinate dialog. The system " +
                        "will fallback to EPSG:4326.");
            }

            Optional<String> result = coordinateDialog.showAndWait();
            coordinateDialog.hide();
            coordinateDialog.close();

            if (result.isPresent()){
                CoordinateSystem coordinateSystem = CoordinateSelector.fromString(controller.toString());

                epsgLabel.setText(coordinateSystem.id());
            }
        });

        sourceData = ItemSearcher.getGenericControlById(prefix + "sourcedata", pane, ComboBox.class, String.class);
        AgisoftParamInitiator.initComboBox(sourceData, BuildDem.SOURCE_DATA);

        quality = ItemSearcher.getGenericControlById(prefix + "quality", pane, ComboBox.class, String.class);
        AgisoftParamInitiator.initComboBox(quality, BuildDem.QUALITY);

        interpolation = ItemSearcher.getGenericControlById(prefix + "interpolation", pane, ComboBox.class, String.class);
        AgisoftParamInitiator.initComboBox(interpolation, BuildDem.INTERPOLATION);

        setupResultConverter(dialog);
    }

    @Override
    public void setupResultConverter(Dialog<String> dialog) {
        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                Gson gson = new Gson();

                HashMap<String, String> parameterMap = new HashMap<>(){{
                    put("coordinatesystem", epsgLabel.getText());
                    put("sourcedata", sourceData.getSelectionModel().getSelectedItem());
                    put("quality", quality.getSelectionModel().getSelectedItem());
                    put("interpolation", interpolation.getSelectionModel().getSelectedItem());
                }};

                return gson.toJson(parameterMap, GsonTypeTokens.hashmapToken);
            }
            return null;
        });
    }

}
