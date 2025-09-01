package wue.eorc.umas.utils;

import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import wue.eorc.umas.enums.agisoft.AgisoftParameter;

public class AgisoftParamInitiator {

    public static void initComboBox(ComboBox<String> comboBox, AgisoftParameter parameter){
        comboBox.getItems().addAll(parameter.getChoices());
        comboBox.getSelectionModel().select(parameter.getDefaultIndex());
    }

    public static void initCheckBox(CheckBox checkBox, AgisoftParameter parameter){
        checkBox.setSelected(parameter.getDefaultIndex() == 0);
    }

    public static void initTextField(TextField textField, AgisoftParameter parameter){
        textField.setText(parameter.getDefaultIndex() + "");
    }

    public static void initLabel(Label label, AgisoftParameter agisoftParameter){
        label.setText(agisoftParameter.getChoices().get(agisoftParameter.getDefaultIndex()));
    }

}
