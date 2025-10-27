package wue.eorc.umas.controller.scenes.views.dialogs.agisoft;

import com.google.gson.Gson;
import javafx.scene.control.*;
import wue.eorc.umas.controller.customs.UMASDialog;
import wue.eorc.umas.controller.scenes.main.DisplayController;
import wue.eorc.umas.controller.scenes.views.dialogs.CoordinateSelector;
import wue.eorc.umas.controller.scenes.views.dialogs.StaticDialogController;
import wue.eorc.umas.enums.ErrorType;
import wue.eorc.umas.enums.agisoft.BuildDem;
import wue.eorc.umas.enums.agisoft.ExportDem;
import wue.eorc.umas.exception.UMASException;
import wue.eorc.umas.models.CoordinateSystem;
import wue.eorc.umas.utils.agisoft.AgisoftParamInitiator;
import wue.eorc.umas.utils.system.GsonTypeTokens;
import wue.eorc.umas.utils.ui.ItemSearcher;

import java.util.HashMap;
import java.util.Optional;

public class ExportDemController implements StaticDialogController {

    private Label epsgLabel;
    private Button epsgSelect;

    private ComboBox<String> rasterTransform;
    private TextField noDataValue;

    private CheckBox writeKml;
    private CheckBox writeWorldFile;
    private CheckBox writeTileScheme;

    private TextField imageDescription;

    private CheckBox writeTiledTiff;
    private CheckBox writeBigTiffFile;
    private CheckBox generateTiffOverviews;
    private CheckBox saveAlphaChannel;

    @Override
    public void init(DisplayController display, Dialog<String> dialog) throws UMASException {
        String prefix = "agisoft.exportdem.";
        DialogPane pane = dialog.getDialogPane();

        epsgLabel = ItemSearcher.getItemById(prefix + "epsglabel", pane, Label.class);
        AgisoftParamInitiator.initLabel(epsgLabel, BuildDem.COORDINATE_SYSTEM);

        epsgSelect = ItemSearcher.getItemById(prefix + "epsgselect", pane, Button.class);
        epsgSelect.setOnAction(ae -> {
            DialogPane dialogPane = (DialogPane) display.getSceneLoader().getScene("coordinate_system_select");
            Dialog<String> coordinateDialog = new UMASDialog(dialogPane, "Select coordinate system", true, true);
            CoordinateSelector controller = new CoordinateSelector();

            try {
                controller.init(display, coordinateDialog);
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

        noDataValue = ItemSearcher.getItemById(prefix + "nodatavalue", pane, TextField.class);

        rasterTransform = ItemSearcher.getGenericControlById(prefix + "rastertransform", pane, ComboBox.class, String.class);
        AgisoftParamInitiator.initComboBox(rasterTransform, ExportDem.RASTER_TRANSFORM);

        writeKml = ItemSearcher.getItemById(prefix + "writekml", pane, CheckBox.class);
        AgisoftParamInitiator.initCheckBox(writeKml, ExportDem.WRITE_KML);

        writeWorldFile = ItemSearcher.getItemById(prefix + "writeworldfile", pane, CheckBox.class);
        AgisoftParamInitiator.initCheckBox(writeWorldFile, ExportDem.WRITE_WORLD_FILE);

        writeTileScheme = ItemSearcher.getItemById(prefix + "writetilescheme", pane, CheckBox.class);
        AgisoftParamInitiator.initCheckBox(writeTileScheme, ExportDem.WRITE_TILE_SCHEME);


        imageDescription = ItemSearcher.getItemById(prefix + "imagedescription", pane, TextField.class);


        writeTiledTiff = ItemSearcher.getItemById(prefix + "writetiledtiff", pane, CheckBox.class);
        AgisoftParamInitiator.initCheckBox(writeTiledTiff, ExportDem.WRITE_TILED_TIFF);

        generateTiffOverviews = ItemSearcher.getItemById(prefix + "generatetiffoverviews", pane, CheckBox.class);
        AgisoftParamInitiator.initCheckBox(generateTiffOverviews, ExportDem.GENERATE_TIFF_OVERVIEWS);

        writeBigTiffFile = ItemSearcher.getItemById(prefix + "writebigtifffile", pane, CheckBox.class);
        AgisoftParamInitiator.initCheckBox(writeBigTiffFile, ExportDem.WRITE_BIG_TIFF_FILE);


        setupResultConverter(dialog);
    }

    @Override
    public void setupResultConverter(Dialog<String> dialog) {
        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                Gson gson = new Gson();

                HashMap<String, String> parameterMap = new HashMap<>() {{
                    put("coordinatesystem", epsgLabel.getText());
                    put("rastertransform", rasterTransform.getSelectionModel().getSelectedItem());
                    put("nodatavalue", noDataValue.getText());
                    put("writekml", writeKml.isSelected() ? "True" : "False");
                    put("writeworldfile", writeWorldFile.isSelected() ? "True" : "False");
                    put("writetilescheme", writeTileScheme.isSelected() ? "True" : "False");
                    put("imagedescription", imageDescription.getText());
                    put("writetiledtiff", writeTiledTiff.isSelected() ? "True" : "False");
                    put("generatetiffoverviews", generateTiffOverviews.isSelected() ? "True" : "False");
                    put("writebigtifffile", writeBigTiffFile.isSelected() ? "True" : "False");
                }};

                return gson.toJson(parameterMap, GsonTypeTokens.hashmapToken);
            }
            return null;
        });
    }
}
