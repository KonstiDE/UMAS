package wue.eorc.umas.controller.scenes.views.dialogs.agisoft;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import wue.eorc.umas.controller.scenes.main.DisplayController;
import wue.eorc.umas.controller.scenes.views.dialogs.StaticDialogController;
import wue.eorc.umas.enums.agisoft.AlignImages;
import wue.eorc.umas.exception.UMASException;
import wue.eorc.umas.utils.AgisoftParamInitiator;
import wue.eorc.umas.utils.ItemSearcher;

public class AlignImagesController implements StaticDialogController {

    private final String prefix = "agisoft.alignimages.";

    private int keyPointLimitSave = 1;
    private int keyPointLimitPerMpxSave = 4000;

    @Override
    public void init(Pane pane, DisplayController display, Dialog<String> dialog) throws UMASException {
        ComboBox<String> accuracy = ItemSearcher.getGenericControlById(prefix + "accuracy", pane, ComboBox.class, String.class);
        AgisoftParamInitiator.initComboBox(accuracy, AlignImages.ACCURACY);

        CheckBox genericPreselection = ItemSearcher.getItemById(prefix + "genericpreselection", pane, CheckBox.class);
        AgisoftParamInitiator.initCheckBox(genericPreselection, AlignImages.GENERIC_PRESELECTION);

        CheckBox referencePreselection = ItemSearcher.getItemById(prefix + "referencepreselection", pane, CheckBox.class);
        AgisoftParamInitiator.initCheckBox(referencePreselection, AlignImages.REFERENCE_PRESELECTION);

        ComboBox<String> referencePreselectionCombo = ItemSearcher.getGenericControlById(prefix + "referencepreselection.combo", pane, ComboBox.class, String.class);
        AgisoftParamInitiator.initComboBox(referencePreselectionCombo, AlignImages.REFERENCE_PRESELECTION_COMBO);

        TextField keyPointLimit = ItemSearcher.getItemById(prefix + "keypointlimit", pane, TextField.class);
        AgisoftParamInitiator.initTextField(keyPointLimit, AlignImages.KEY_POINT_LIMIT_PER_MPX);

        Label keyPointLimitLabel = ItemSearcher.getItemById(prefix + "keypointlimit.label", pane, Label.class);


        TextField tiePointLimit = ItemSearcher.getItemById(prefix + "tiepointlimit", pane, TextField.class);
        AgisoftParamInitiator.initTextField(tiePointLimit, AlignImages.TIE_POINT_LIMIT);

        CheckBox excludeTiePoints = ItemSearcher.getItemById(prefix + "excludestationarytiepoints", pane, CheckBox.class);
        AgisoftParamInitiator.initCheckBox(excludeTiePoints, AlignImages.EXCLUDE_STAT_TIE_POINTS);

        CheckBox guidedMatching = ItemSearcher.getItemById(prefix + "guidedimagematching", pane, CheckBox.class);
        AgisoftParamInitiator.initCheckBox(guidedMatching, AlignImages.GUIDED_MATCHING);

        CheckBox adaptiveFitting = ItemSearcher.getItemById(prefix + "adaptivecameramodelfitting", pane, CheckBox.class);
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

    }

    @Override
    public String jsonCallback(ButtonType buttonType) {
        if (buttonType == ButtonType.OK) {
            return "";
        } else if (buttonType == ButtonType.CANCEL) {
            return null;
        }

        return null;
    }
}
