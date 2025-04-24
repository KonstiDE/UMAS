package utils;

import enums.ErrorType;
import exception.UMASException;
import javafx.animation.Animation;
import javafx.animation.Timeline;
import javafx.scene.control.TextField;

import java.util.Arrays;
import java.util.List;

public class FormValidator {

    public static List<String> validateTextFields(TextField... fields) {
        try {
            return Arrays.stream(fields).map(textField -> {
                if(textField.getText().trim().isEmpty()){
                    UMASException.throwWindow(ErrorType.USER, "The textfield \"" + textField.getAccessibleText() + "\" is empty although its required.");
                    textField.setStyle("-fx-text-fill: red;");
                    throw new RuntimeException("Lookup previous exception please.");
                }
                return textField.getText();
            }).toList();
        }catch (RuntimeException e){
            return null;
        }
    };

}
