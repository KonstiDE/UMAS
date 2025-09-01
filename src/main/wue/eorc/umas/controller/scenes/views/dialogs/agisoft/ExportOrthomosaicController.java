package wue.eorc.umas.controller.scenes.views.dialogs.agisoft;

import com.google.gson.Gson;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import org.controlsfx.control.PlusMinusSlider;
import wue.eorc.umas.controller.customs.UMASDialog;
import wue.eorc.umas.controller.scenes.main.DisplayController;
import wue.eorc.umas.controller.scenes.views.dialogs.CoordinateSelector;
import wue.eorc.umas.controller.scenes.views.dialogs.StaticDialogController;
import wue.eorc.umas.enums.ErrorType;
import wue.eorc.umas.enums.agisoft.BuildDem;
import wue.eorc.umas.enums.agisoft.ExportOrthomosaic;
import wue.eorc.umas.exception.UMASException;
import wue.eorc.umas.models.CoordinateSystem;
import wue.eorc.umas.utils.AgisoftParamInitiator;
import wue.eorc.umas.utils.GsonTypeTokens;
import wue.eorc.umas.utils.ItemSearcher;

import java.util.HashMap;
import java.util.Optional;

public class ExportOrthomosaicController implements StaticDialogController {

    private Label epsgLabel;
    private Button epsgSelect;

    private ComboBox<String> backgroundColor;

    private CheckBox writeKml;
    private CheckBox writeWorldFile;
    private CheckBox writeTileScheme;

    private TextField imageDescription;

    private ComboBox<String> tiffCompression;

    private Label jpegQualityLabel;
    private Slider jpegQualitySlider;
    private Button jpegQualityPlus;
    private Button jpegQualityMinus;

    private CheckBox writeTiledTiff;
    private CheckBox writeBigTiffFile;
    private CheckBox generateTiffOverviews;
    private CheckBox saveAlphaChannel;

    @Override
    public void init(Pane pane, DisplayController display, Dialog<String> dialog) throws UMASException {
        String prefix = "agisoft.exportorthomosaic.";

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
            coordinateDialog.setResultConverter(controller::jsonCallback);

            Optional<String> result = coordinateDialog.showAndWait();
            coordinateDialog.hide();
            coordinateDialog.close();

            if (result.isPresent()){
                CoordinateSystem coordinateSystem = CoordinateSelector.fromString(controller.toString());

                epsgLabel.setText(coordinateSystem.id());
            }
        });

        backgroundColor = ItemSearcher.getGenericControlById(prefix + "backgroundcolor", pane, ComboBox.class, String.class);
        AgisoftParamInitiator.initComboBox(backgroundColor, ExportOrthomosaic.BACKGROUND_COLOR);

        writeKml = ItemSearcher.getItemById(prefix + "writekml", pane, CheckBox.class);
        AgisoftParamInitiator.initCheckBox(writeKml, ExportOrthomosaic.WRITE_KML);

        writeWorldFile = ItemSearcher.getItemById(prefix + "writeworldfile", pane, CheckBox.class);
        AgisoftParamInitiator.initCheckBox(writeWorldFile, ExportOrthomosaic.WRITE_WORLD_FILE);

        writeTileScheme = ItemSearcher.getItemById(prefix + "writetilescheme", pane, CheckBox.class);
        AgisoftParamInitiator.initCheckBox(writeTileScheme, ExportOrthomosaic.WRITE_TILE_SCHEME);


        imageDescription = ItemSearcher.getItemById(prefix + "imagedescription", pane, TextField.class);


        tiffCompression = ItemSearcher.getGenericControlById(prefix + "tiffcompression", pane, ComboBox.class, String.class);
        AgisoftParamInitiator.initComboBox(tiffCompression, ExportOrthomosaic.TIFF_COMPRESSION);

        jpegQualityLabel = ItemSearcher.getItemById(prefix + "jpegqualitylabel", pane, Label.class);
        AgisoftParamInitiator.initLabel(jpegQualityLabel, ExportOrthomosaic.JPEG_QUALITY);

        jpegQualitySlider = ItemSearcher.getItemById(prefix + "jpegqualityslider", pane, Slider.class);
        jpegQualitySlider.valueProperty().addListener((opt, oldVal, newVal) -> jpegQualityLabel.setText("" + newVal.intValue()));

        jpegQualityPlus = ItemSearcher.getItemById(prefix + "jpegqualityplus", pane, Button.class);
        jpegQualityPlus.setOnAction((ae) -> {
            if(jpegQualitySlider.getValue() < 100) jpegQualitySlider.setValue(jpegQualitySlider.getValue() + 1);
        });
        jpegQualityMinus = ItemSearcher.getItemById(prefix + "jpegqualityminus", pane, Button.class);
        jpegQualityMinus.setOnAction((ae) -> {
            if(jpegQualitySlider.getValue() > 1) jpegQualitySlider.setValue(jpegQualitySlider.getValue() - 1);
        });

        writeTiledTiff = ItemSearcher.getItemById(prefix + "writetiledtiff", pane, CheckBox.class);
        AgisoftParamInitiator.initCheckBox(writeTiledTiff, ExportOrthomosaic.WRITE_TILED_TIFF);

        generateTiffOverviews = ItemSearcher.getItemById(prefix + "generatetiffoverviews", pane, CheckBox.class);
        AgisoftParamInitiator.initCheckBox(generateTiffOverviews, ExportOrthomosaic.GENERATE_TIFF_OVERVIEWS);

        writeBigTiffFile = ItemSearcher.getItemById(prefix + "writebigtifffile", pane, CheckBox.class);
        AgisoftParamInitiator.initCheckBox(writeBigTiffFile, ExportOrthomosaic.WRITE_BIG_TIFF_FILE);

        saveAlphaChannel = ItemSearcher.getItemById(prefix + "savealphachannel", pane, CheckBox.class);
        AgisoftParamInitiator.initCheckBox(saveAlphaChannel, ExportOrthomosaic.SAVE_ALPHA_CHANNEL);


    }

    @Override
    public String jsonCallback(ButtonType buttonType) {
        if (buttonType == ButtonType.OK) {
            Gson gson = new Gson();

            HashMap<String, String> parameterMap = new HashMap<>(){{
                put("coordinatesystem", epsgLabel.getText());
                put("backgroundcolor", backgroundColor.getSelectionModel().getSelectedItem());
                put("writekml", writeKml.isSelected() ? "True" : "False");
                put("writeworldfile", writeWorldFile.isSelected() ? "True" : "False");
                put("writetilescheme", writeTileScheme.isSelected() ? "True" : "False");
                put("imagedescription", imageDescription.getText());
                put("writetiledtiff", writeTiledTiff.isSelected() ? "True" : "False");
                put("generatetiffoverviews", generateTiffOverviews.isSelected() ? "True" : "False");
                put("writebigtifffile", writeBigTiffFile.isSelected() ? "True" : "False");
                put("savealphachannel", saveAlphaChannel.isSelected() ? "True" : "False");
                put("tiffcompression", tiffCompression.getSelectionModel().getSelectedItem());
                put("jpegquality", jpegQualityLabel.getText());
            }};

            return gson.toJson(parameterMap, GsonTypeTokens.hashmapToken);
        } else if (buttonType == ButtonType.CANCEL) {
            return null;
        }

        return null;
    }
}
