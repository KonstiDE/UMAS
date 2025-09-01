package wue.eorc.umas.controller.scenes.views.dialogs.agisoft;

import com.google.gson.Gson;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import wue.eorc.umas.controller.scenes.main.DisplayController;
import wue.eorc.umas.controller.scenes.views.dialogs.StaticDialogController;
import wue.eorc.umas.enums.agisoft.BuildDem;
import wue.eorc.umas.enums.agisoft.BuildOrthomosaic;
import wue.eorc.umas.exception.UMASException;
import wue.eorc.umas.utils.AgisoftParamInitiator;
import wue.eorc.umas.utils.GsonTypeTokens;
import wue.eorc.umas.utils.ItemSearcher;

import java.util.HashMap;

public class BuildOrthomosaicController implements StaticDialogController {

    private Label epsgLabel;
    private Button epsgSelect;

    private ComboBox<String> surface;
    private ComboBox<String> blendingMode;

    private CheckBox refineSeamlines;
    private CheckBox enableHoleFilling;
    private CheckBox enableGhostingFilter;
    private CheckBox enableBackFaceCulling;


    @Override
    public void init(Pane pane, DisplayController display, Dialog<String> dialog) throws UMASException {
        String prefix = "agisoft.buildorthomosaic.";

        epsgLabel = ItemSearcher.getItemById(prefix + "epsglabel", pane, Label.class);
        AgisoftParamInitiator.initLabel(epsgLabel, BuildDem.COORDINATE_SYSTEM);

        epsgSelect = ItemSearcher.getItemById(prefix + "epsgselect", pane, Button.class);


        surface = ItemSearcher.getGenericControlById(prefix + "surface", pane, ComboBox.class, String.class);
        AgisoftParamInitiator.initComboBox(surface, BuildOrthomosaic.SURFACE);

        blendingMode = ItemSearcher.getGenericControlById(prefix + "blendingmode", pane, ComboBox.class, String.class);
        AgisoftParamInitiator.initComboBox(blendingMode, BuildOrthomosaic.BLENDING_MODE);

        refineSeamlines = ItemSearcher.getItemById(prefix + "refineseamlines", pane, CheckBox.class);
        AgisoftParamInitiator.initCheckBox(refineSeamlines, BuildOrthomosaic.REFINE_SEAMLINES);

        enableHoleFilling = ItemSearcher.getItemById(prefix + "enableholefilling", pane, CheckBox.class);
        AgisoftParamInitiator.initCheckBox(enableHoleFilling, BuildOrthomosaic.ENABLE_HOLE_FILLING);

        enableGhostingFilter = ItemSearcher.getItemById(prefix + "enableghostingfilter", pane, CheckBox.class);
        AgisoftParamInitiator.initCheckBox(enableGhostingFilter, BuildOrthomosaic.ENABLE_GHOSTING_FILTER);

        enableBackFaceCulling = ItemSearcher.getItemById(prefix + "enablebackfaceculling", pane, CheckBox.class);
        AgisoftParamInitiator.initCheckBox(enableBackFaceCulling, BuildOrthomosaic.ENABLE_BACK_FACE_CULLING);

        blendingMode.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> opt, String oldVal, String newVal) {
                if(newVal.equalsIgnoreCase("Average")){
                    refineSeamlines.setDisable(true);
                    refineSeamlines.setSelected(false);
                }else{
                    refineSeamlines.setDisable(false);
                }
            }
        });

    }

    @Override
    public String jsonCallback(ButtonType buttonType) {
        if (buttonType == ButtonType.OK) {
            Gson gson = new Gson();

            HashMap<String, String> parameterMap = new HashMap<>(){{
                put("coordinatesystem", epsgLabel.getText());
                put("surface", surface.getSelectionModel().getSelectedItem());
                put("blendingmode", blendingMode.getSelectionModel().getSelectedItem());
                put("refineseamlines", refineSeamlines.isSelected() ? "True" : "False");
                put("enableholefilling", enableHoleFilling.isSelected() ? "True" : "False");
                put("enableghostingfilter", enableGhostingFilter.isSelected() ? "True" : "False");
                put("enablebackfaceculling", enableBackFaceCulling.isSelected() ? "True" : "False");
            }};

            return gson.toJson(parameterMap, GsonTypeTokens.hashmapToken);
        } else if (buttonType == ButtonType.CANCEL) {
            return null;
        }

        return null;
    }
}
