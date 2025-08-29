package wue.eorc.umas.controller.scenes.views.dialogs.agisoft;

import com.google.gson.Gson;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.layout.Pane;
import wue.eorc.umas.controller.scenes.main.DisplayController;
import wue.eorc.umas.controller.scenes.views.dialogs.StaticDialogController;
import wue.eorc.umas.enums.agisoft.BuildPointCloud;
import wue.eorc.umas.exception.UMASException;
import wue.eorc.umas.utils.AgisoftParamInitiator;
import wue.eorc.umas.utils.GsonTypeTokens;
import wue.eorc.umas.utils.ItemSearcher;

import java.util.HashMap;

public class BuildPointCloudController implements StaticDialogController {

    private ComboBox<String> quality;
    private ComboBox<String> depthFiltering;
    private CheckBox reuseDepthMaps;
    private CheckBox calculatePointColors;
    private CheckBox calculatePointConfidence;

    @Override
    public void init(Pane pane, DisplayController display, Dialog<String> dialog) throws UMASException {
        String prefix = "agisoft.buildpointcloud.";

        quality = ItemSearcher.getGenericControlById(prefix + "quality", pane, ComboBox.class, String.class);
        AgisoftParamInitiator.initComboBox(quality, BuildPointCloud.ACCURACY);

        depthFiltering = ItemSearcher.getGenericControlById(prefix + "depthfiltering", pane, ComboBox.class, String.class);
        AgisoftParamInitiator.initComboBox(depthFiltering, BuildPointCloud.DEPTH_FILTERING);

        reuseDepthMaps = ItemSearcher.getItemById(prefix + "reusedepthmaps", pane, CheckBox.class);
        AgisoftParamInitiator.initCheckBox(reuseDepthMaps, BuildPointCloud.REUSE_DEPTH_MAPS);

        calculatePointColors = ItemSearcher.getItemById(prefix + "calculatepointcolors", pane, CheckBox.class);
        AgisoftParamInitiator.initCheckBox(calculatePointColors, BuildPointCloud.CALCULATE_POINT_COLORS);

        calculatePointConfidence = ItemSearcher.getItemById(prefix + "calculatepointconfidence", pane, CheckBox.class);
        AgisoftParamInitiator.initCheckBox(calculatePointConfidence, BuildPointCloud.CALCULATE_POINT_CONFIDENCE);

    }

    @Override
    public String jsonCallback(ButtonType buttonType) {
        if (buttonType == ButtonType.OK) {
            Gson gson = new Gson();

            HashMap<String, String> parameterMap = new HashMap<>(){{
                put("quality", quality.getSelectionModel().getSelectedItem());
                put("depthfiltering", depthFiltering.getSelectionModel().getSelectedItem());
                put("reusedepthmaps", reuseDepthMaps.isSelected() ? "True" : "False");
                put("calculatepointcolors", calculatePointColors.isSelected() ? "True" : "False");
                put("calculatepointconfidence", calculatePointConfidence.isSelected() ? "True" : "False");
            }};

            return gson.toJson(parameterMap, GsonTypeTokens.hashmapToken);
        } else if (buttonType == ButtonType.CANCEL) {
            return null;
        }

        return null;
    }
}
