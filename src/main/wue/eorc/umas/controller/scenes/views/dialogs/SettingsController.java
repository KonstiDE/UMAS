package wue.eorc.umas.controller.scenes.views.dialogs;

import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import org.controlsfx.control.ToggleSwitch;
import wue.eorc.umas.agisoft.AgisoftCaller;
import wue.eorc.umas.controller.scenes.main.DisplayController;
import wue.eorc.umas.enums.ErrorType;
import wue.eorc.umas.enums.Setting;
import wue.eorc.umas.exception.UMASException;
import wue.eorc.umas.loader.Settings;
import wue.eorc.umas.utils.ui.ItemSearcher;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Objects;

public class SettingsController implements StaticDialogController {

    private ComboBox<String> uiTheme;
    private ToggleSwitch fullScreenAtStartup;
    private TextField agisoftExecPath;
    private TextField terraExecPath;
    private ImageView agisoftIndicator;
    private Label agisoftVersion;
    private TextField databaseHost;
    private TextField databasePort;
    private Button databaseTest;

    private DisplayController displayController;

    @Override
    public void init(DisplayController display, Dialog<String> dialog) throws UMASException {
        DialogPane pane = dialog.getDialogPane();

        this.displayController = display;

        uiTheme = ItemSearcher.getGenericControlById("settings.uitheme", pane, ComboBox.class, String.class);
        fullScreenAtStartup = ItemSearcher.getItemById("settings.fullscreenatstartup", pane, ToggleSwitch.class);

        Button browseAgisoftExecPath = ItemSearcher.getItemById("settings.agisoftexecpathbrowse", pane, Button.class);
        Button browseTerraExecPath = ItemSearcher.getItemById("settings.terraexecpathbrowse", pane, Button.class);

        agisoftExecPath = ItemSearcher.getItemById("settings.agisoftexecpath", pane, TextField.class);
        terraExecPath = ItemSearcher.getItemById("settings.terraexecpath", pane, TextField.class);

        agisoftIndicator = ItemSearcher.getItemById("settings.agisoftindicator", pane, ImageView.class);
        agisoftVersion = ItemSearcher.getItemById("settings.agisoftversion", pane, Label.class);
        ImageView terraIndicator = ItemSearcher.getItemById("settings.terraindicator", pane, ImageView.class);
        Label terraVersion = ItemSearcher.getItemById("settings.terraversion", pane, Label.class);

        databaseHost = ItemSearcher.getItemById("settings.database.host", pane, TextField.class);
        databasePort = ItemSearcher.getItemById("settings.database.port", pane, TextField.class);
        databaseTest = ItemSearcher.getItemById("settings.database.test", pane, Button.class);

        uiTheme.getItems().clear();
        uiTheme.getItems().addAll("Light", "Dark");
        uiTheme.getSelectionModel().select(Settings.getSetting(Setting.UI_THEME));
        uiTheme.setOnAction(_ignored -> {
            if (uiTheme.getSelectionModel().getSelectedItem().equals("Dark")) {
                display.rootControl.getScene().getStylesheets().clear();
                display.rootControl.getScene().getStylesheets().add(Settings.darkMode);

                dialog.getDialogPane().getScene().getStylesheets().add(Settings.darkModeDialog);
            }else{
                display.rootControl.getScene().getStylesheets().clear();
                dialog.getDialogPane().getScene().getStylesheets().clear();
            }
        });

        fullScreenAtStartup.setSelected(Boolean.parseBoolean(Settings.getSetting(Setting.FULL_SCREEN_AT_START_UP)));

        agisoftExecPath.setText(Settings.getSetting(Setting.AGISOFT_EXEC_PATH));
        terraExecPath.setText(Settings.getSetting(Setting.TERRA_EXEC_PATH));

        browseAgisoftExecPath.setOnAction(_ignored -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Agisoft Executable Path");

            File file = fileChooser.showOpenDialog(display.rootControl.getScene().getWindow());
            try {
                String version = new AgisoftCaller(null, display)
                        .checkAgisoftVersion(file.getAbsolutePath());

                if(version != null){
                    agisoftIndicator.setImage(new Image(Objects.requireNonNull(getClass().getClassLoader().getResource("assets/settings/check.png")).toString()));
                    agisoftVersion.setText(version);
                    Settings.modifySettings(Setting.AGISOFT_VERSION, version);
                    Settings.modifySettings(Setting.AGISOFT_EXEC_PATH_VALID, "true");
                }else{
                    agisoftIndicator.setImage(new Image(Objects.requireNonNull(getClass().getClassLoader().getResource("assets/settings/cross.png")).toString()));
                    agisoftVersion.setText("Invalid Agisoft Executable");
                    Settings.modifySettings(Setting.AGISOFT_VERSION, "Invalid Agisoft Executable");
                    Settings.modifySettings(Setting.AGISOFT_EXEC_PATH_VALID, "false");
                }

                agisoftExecPath.setText(file.getAbsolutePath());

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        });

        if(Boolean.parseBoolean(Settings.getSetting(Setting.AGISOFT_EXEC_PATH_VALID))){
            agisoftIndicator.setImage(new Image(Objects.requireNonNull(getClass().getClassLoader().getResource("assets/settings/check.png")).toString()));
            agisoftVersion.setText(Settings.getSetting(Setting.AGISOFT_VERSION));
        }else{
            agisoftIndicator.setImage(new Image(Objects.requireNonNull(getClass().getClassLoader().getResource("assets/settings/cross.png")).toString()));
            agisoftVersion.setText("Invalid Agisoft Executable");
        }

        setupResultConverter(dialog);

    }

    @Override
    public void setupResultConverter(Dialog<String> dialog) {
        dialog.setResultConverter(buttonType -> {
            if(buttonType == ButtonType.APPLY){
                try {
                    Settings.modifySettings(Setting.UI_THEME, uiTheme.getValue());
                    Settings.modifySettings(Setting.FULL_SCREEN_AT_START_UP, "" + fullScreenAtStartup.isSelected());
                    Settings.modifySettings(Setting.AGISOFT_EXEC_PATH, agisoftExecPath.getText());
                    Settings.modifySettings(Setting.TERRA_EXEC_PATH, terraExecPath.getText());

                    Settings.saveSettings();
                } catch (IOException e) {
                    UMASException.throwWindow(ErrorType.INTERNAL, "Could not save settings. Please restart the application!");
                }

                Platform.runLater(() -> {
                    dialog.setResult("");
                    dialog.close();
                });
            }else{
                if(Settings.getSetting(Setting.UI_THEME).equals("Light")){
                    try {
                        dialog.getDialogPane().getScene().getStylesheets().clear();
                        displayController.rootControl.getScene().getStylesheets().clear();
                    }catch (Exception ignored){ /* Exception can occur if canceling the dialog without changes */ }
                }

                Platform.runLater(() -> {
                    dialog.setResult("");
                    dialog.close();
                });
            }
            return null;
        });
    }


}
