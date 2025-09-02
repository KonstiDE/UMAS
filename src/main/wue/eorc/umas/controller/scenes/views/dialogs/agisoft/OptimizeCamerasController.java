package wue.eorc.umas.controller.scenes.views.dialogs.agisoft;

import com.google.gson.Gson;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.layout.Pane;
import wue.eorc.umas.controller.scenes.main.DisplayController;
import wue.eorc.umas.controller.scenes.views.dialogs.StaticDialogController;
import wue.eorc.umas.enums.agisoft.OptimizeCameras;
import wue.eorc.umas.exception.UMASException;
import wue.eorc.umas.utils.AgisoftParamInitiator;
import wue.eorc.umas.utils.GsonTypeTokens;
import wue.eorc.umas.utils.ItemSearcher;

import java.util.HashMap;

public class OptimizeCamerasController implements StaticDialogController {

    private CheckBox fitF;
    private CheckBox fitK1;
    private CheckBox fitK2;
    private CheckBox fitK3;
    private CheckBox fitK4;

    private CheckBox fitCxCy;
    private CheckBox fitP1;
    private CheckBox fitP2;
    private CheckBox fitB1;
    private CheckBox fitB2;

    private CheckBox adaptiveFitting;
    private CheckBox estimateTieCov;
    private CheckBox fitAdditional;

    @Override
    public void init(Pane pane, DisplayController display, Dialog<String> dialog) throws UMASException {
        String prefix = "agisoft.optimizecameras.";

        fitF = ItemSearcher.getItemById(prefix + "fitf", pane, CheckBox.class);
        AgisoftParamInitiator.initCheckBox(fitF, OptimizeCameras.FIT_F);

        fitK1 = ItemSearcher.getItemById(prefix + "fitk1", pane, CheckBox.class);
        AgisoftParamInitiator.initCheckBox(fitK1, OptimizeCameras.FIT_K1);

        fitK2 = ItemSearcher.getItemById(prefix + "fitk2", pane, CheckBox.class);
        AgisoftParamInitiator.initCheckBox(fitK2, OptimizeCameras.FIT_K2);

        fitK3 = ItemSearcher.getItemById(prefix + "fitk3", pane, CheckBox.class);
        AgisoftParamInitiator.initCheckBox(fitK3, OptimizeCameras.FIT_K3);

        fitK4 = ItemSearcher.getItemById(prefix + "fitk4", pane, CheckBox.class);
        AgisoftParamInitiator.initCheckBox(fitK4, OptimizeCameras.FIT_K4);


        fitCxCy = ItemSearcher.getItemById(prefix + "fitcxcy", pane, CheckBox.class);
        AgisoftParamInitiator.initCheckBox(fitCxCy, OptimizeCameras.FIT_CX_CY);

        fitP1 = ItemSearcher.getItemById(prefix + "fitp1", pane, CheckBox.class);
        AgisoftParamInitiator.initCheckBox(fitP1, OptimizeCameras.FIT_P1);

        fitP2 = ItemSearcher.getItemById(prefix + "fitp2", pane, CheckBox.class);
        AgisoftParamInitiator.initCheckBox(fitP2, OptimizeCameras.FIT_P2);

        fitB1 = ItemSearcher.getItemById(prefix + "fitb1", pane, CheckBox.class);
        AgisoftParamInitiator.initCheckBox(fitB1, OptimizeCameras.FIT_B1);

        fitB2 = ItemSearcher.getItemById(prefix + "fitb2", pane, CheckBox.class);
        AgisoftParamInitiator.initCheckBox(fitB2, OptimizeCameras.FIT_B2);


        adaptiveFitting = ItemSearcher.getItemById(prefix + "adaptivecameramodelfitting", pane, CheckBox.class);
        AgisoftParamInitiator.initCheckBox(adaptiveFitting, OptimizeCameras.ADAPTIVE_FITTING);

        estimateTieCov = ItemSearcher.getItemById(prefix + "estimatetiepointcovariance", pane, CheckBox.class);
        AgisoftParamInitiator.initCheckBox(estimateTieCov, OptimizeCameras.ESTIMATING_TIE_COV);

        fitAdditional = ItemSearcher.getItemById(prefix + "fitadditionalcorrections", pane, CheckBox.class);
        AgisoftParamInitiator.initCheckBox(fitAdditional, OptimizeCameras.FIT_ADDITIONAL);


        adaptiveFitting.selectedProperty().addListener((opt, oldVal, newVal) -> {
            if (newVal){
                disableCheckboxes(true,
                        fitF, fitK1, fitK2, fitK3, fitK4, fitCxCy, fitP1, fitP2, fitB1, fitB2, fitAdditional);
            }else{
                disableCheckboxes(false,
                        fitF, fitK1, fitK2, fitK3, fitK4, fitCxCy, fitP1, fitP2, fitB1, fitB2, fitAdditional);
            }
        });



    }

    @Override
    public void setupResultConverter(Dialog<String> dialog) {
        if (buttonType == ButtonType.OK) {
            Gson gson = new Gson();

            HashMap<String, String> parameterMap = new HashMap<>(){{
                put("fitf", fitF.isSelected() ? "True" : "False");
                put("fitk1", fitK1.isSelected() ? "True" : "False");
                put("fitk2", fitK2.isSelected() ? "True" : "False");
                put("fitk3", fitK3.isSelected() ? "True" : "False");
                put("fitk4", fitK4.isSelected() ? "True" : "False");
                put("fitcxcy", fitCxCy.isSelected() ? "True" : "False");
                put("fitp1", fitP1.isSelected() ? "True" : "False");
                put("fitp2", fitP2.isSelected() ? "True" : "False");
                put("fitb1", fitB1.isSelected() ? "True" : "False");
                put("fitb2", fitB2.isSelected() ? "True" : "False");
                put("adaptivecameramodelfitting", adaptiveFitting.isSelected() ? "True" : "False");
                put("estimatetiepointcovariance", estimateTieCov.isSelected() ? "True" : "False");
                put("fitadditionalcorrections", fitAdditional.isSelected() ? "True" : "False");
            }};

            return gson.toJson(parameterMap, GsonTypeTokens.hashmapToken);
        } else if (buttonType == ButtonType.CANCEL) {
            return null;
        }

        return null;
    }

    private void disableCheckboxes(boolean disable, CheckBox... boxes){
        for(CheckBox checkBox : boxes){
            checkBox.setDisable(disable);
        }
    }

}
