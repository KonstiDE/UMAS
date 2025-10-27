package wue.eorc.umas.controller.scenes.views.dialogs.agisoft;

import com.google.gson.Gson;
import javafx.scene.control.*;
import wue.eorc.umas.controller.scenes.main.DisplayController;
import wue.eorc.umas.controller.scenes.views.dialogs.StaticDialogController;
import wue.eorc.umas.enums.agisoft.AlignImages;
import wue.eorc.umas.exception.UMASException;
import wue.eorc.umas.utils.agisoft.AgisoftParamInitiator;
import wue.eorc.umas.utils.system.GsonTypeTokens;
import wue.eorc.umas.utils.ui.ItemSearcher;

import java.util.HashMap;

public class AlignImagesController implements StaticDialogController {

    private int keyPointLimitSave = 1;
    private int keyPointLimitPerMpxSave = 4000;

    private ComboBox<String> accuracy;
    private CheckBox genericPreselection;
    private CheckBox referencePreselection;
    private ComboBox<String> referencePreselectionCombo;
    private TextField keyPointLimit;
    private Label keyPointLimitLabel;
    private TextField tiePointLimit;
    private CheckBox excludeTiePoints;
    private CheckBox guidedMatching;
    private CheckBox adaptiveFitting;

    @Override
    public void init(DisplayController display, Dialog<String> dialog) throws UMASException {
        String prefix = "agisoft.alignimages.";
        DialogPane pane = dialog.getDialogPane();

        accuracy = ItemSearcher.getGenericControlById(prefix + "accuracy", pane, ComboBox.class, String.class);
        AgisoftParamInitiator.initComboBox(accuracy, AlignImages.ACCURACY);

        genericPreselection = ItemSearcher.getItemById(prefix + "genericpreselection", pane, CheckBox.class);
        AgisoftParamInitiator.initCheckBox(genericPreselection, AlignImages.GENERIC_PRESELECTION);

        referencePreselection = ItemSearcher.getItemById(prefix + "referencepreselection", pane, CheckBox.class);
        AgisoftParamInitiator.initCheckBox(referencePreselection, AlignImages.REFERENCE_PRESELECTION);

        referencePreselectionCombo = ItemSearcher.getGenericControlById(prefix + "referencepreselection.combo", pane, ComboBox.class, String.class);
        AgisoftParamInitiator.initComboBox(referencePreselectionCombo, AlignImages.REFERENCE_PRESELECTION_COMBO);

        keyPointLimit = ItemSearcher.getItemById(prefix + "keypointlimit", pane, TextField.class);
        AgisoftParamInitiator.initTextField(keyPointLimit, AlignImages.KEY_POINT_LIMIT_PER_MPX);

        Label keyPointLimitLabel = ItemSearcher.getItemById(prefix + "keypointlimit.label", pane, Label.class);


        tiePointLimit = ItemSearcher.getItemById(prefix + "tiepointlimit", pane, TextField.class);
        AgisoftParamInitiator.initTextField(tiePointLimit, AlignImages.TIE_POINT_LIMIT);

        excludeTiePoints = ItemSearcher.getItemById(prefix + "excludestationarytiepoints", pane, CheckBox.class);
        AgisoftParamInitiator.initCheckBox(excludeTiePoints, AlignImages.EXCLUDE_STAT_TIE_POINTS);

        guidedMatching = ItemSearcher.getItemById(prefix + "guidedimagematching", pane, CheckBox.class);
        AgisoftParamInitiator.initCheckBox(guidedMatching, AlignImages.GUIDED_MATCHING);

        adaptiveFitting = ItemSearcher.getItemById(prefix + "adaptivecameramodelfitting", pane, CheckBox.class);
        AgisoftParamInitiator.initCheckBox(adaptiveFitting, AlignImages.ADAPTIVE_MODEL_FITTING);


        // Logic
        referencePreselectionCombo.getSelectionModel().selectedItemProperty().addListener(
                (opt, oldVal, newVal) -> {
            if (newVal.equalsIgnoreCase("Estimated")) {
                referencePreselection.setSelected(false);
                referencePreselection.setDisable(true);
            } else {
                if (referencePreselection.isDisabled()){
                    referencePreselection.setSelected(true);
                    referencePreselection.setDisable(false);
                }
            }
        });

        guidedMatching.selectedProperty().addListener(
                (opt, oldVal, newVal) -> {
            if (!newVal){
                keyPointLimitLabel.setText("Key point limit");
                keyPointLimit.setText(keyPointLimitSave + "");
            }else{
                keyPointLimitLabel.setText("Key point limit per Mpx");
                keyPointLimit.setText(keyPointLimitPerMpxSave + "");
            }
        });

        keyPointLimit.textProperty().addListener(
                (opt, oldVal, newVal) -> {
            if(guidedMatching.isSelected()){
                keyPointLimitPerMpxSave = Integer.parseInt(newVal);
            }else{
                keyPointLimitSave = Integer.parseInt(newVal);
            }
        });

        setupResultConverter(dialog);

    }

    @Override
    public void setupResultConverter(Dialog<String> dialog) {
        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                Gson gson = new Gson();

                HashMap<String, String> parameterMap = new HashMap<>(){{
                    put("accuracy", accuracy.getSelectionModel().getSelectedItem());
                    put("genericpreselection", genericPreselection.isSelected() ? "True" : "False");
                    put("referencepreselection", referencePreselection.isSelected() ? "True" : "False");
                    put("referencepreselectioncombo", referencePreselectionCombo.getSelectionModel().getSelectedItem());
                    put("keypointlimit", keyPointLimitSave + "");
                    put("keypointlimitpermpx", keyPointLimitPerMpxSave + "");
                    put("tiepointlimit", tiePointLimit.getText());
                    put("excludestationarytiepoints", excludeTiePoints.isSelected() ? "True" : "False");
                    put("guidedimagematching", guidedMatching.isSelected() ? "True" : "False");
                    put("adaptivecameramodelfitting", adaptiveFitting.isSelected() ? "True" : "False");
                }};

                return gson.toJson(parameterMap, GsonTypeTokens.hashmapToken);
            }
            return null;
        });
    }
}
