package wue.eorc.umas.exception;

import javafx.scene.control.Alert;
import wue.eorc.umas.enums.ErrorType;

public class UMASException extends Exception {

    public ErrorType errorType;
    public String message;

    public UMASException(ErrorType errorType, String message) {
        super("[[[" + errorType.toString() + "]]]\n" + message);
    }

    public static void throwWindow(ErrorType errorType, String message) {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setHeaderText(errorType.toString());
        errorAlert.setContentText(message);
        errorAlert.showAndWait();
    }


}
