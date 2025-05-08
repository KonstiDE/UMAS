package wue.eorc.umas.controller.panes.views.dialogs;

import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import org.controlsfx.control.ToggleSwitch;
import wue.eorc.umas.agisoft.AgisoftCaller;
import wue.eorc.umas.controller.panes.mains.DisplayController;
import wue.eorc.umas.enums.ErrorType;
import wue.eorc.umas.enums.Setting;
import wue.eorc.umas.exception.UMASException;
import wue.eorc.umas.loader.Settings;
import wue.eorc.umas.utils.ImageUtils;
import wue.eorc.umas.utils.ItemSearcher;

import java.io.File;
import java.io.IOException;

public class SettingsController implements DialogController {

    @Override
    public void init(Pane pane, DisplayController display, Dialog<String> dialog) throws UMASException {
        if(Settings.useDarkLayout()){
            pane.getScene().getStylesheets().add(Settings.darkMode);
        }

        ComboBox<String> uiTheme = ItemSearcher.getGenericControlById("settings.uitheme", pane, ComboBox.class, String.class);
        ToggleSwitch fullScreenAtStartup = ItemSearcher.getItemById("settings.fullscreenatstartup", pane, ToggleSwitch.class);

        Button browseAgisoftExecPath = ItemSearcher.getItemById("settings.agisoftexecpathbrowse", pane, Button.class);
        Button browseTerraExecPath = ItemSearcher.getItemById("settings.terraexecpathbrowse", pane, Button.class);

        TextField agisoftExecPath = ItemSearcher.getItemById("settings.agisoftexecpath", pane, TextField.class);
        TextField terraExecPath = ItemSearcher.getItemById("settings.terraexecpath", pane, TextField.class);

        ImageView agisoftIndicator = ItemSearcher.getItemById("settings.agisoftindicator", pane, ImageView.class);
        Label agisoftVersion = ItemSearcher.getItemById("settings.agisoftversion", pane, Label.class);
        ImageView terraIndicator = ItemSearcher.getItemById("settings.terraindicator", pane, ImageView.class);
        Label terraVersion = ItemSearcher.getItemById("settings.terraversion", pane, Label.class);

        Button save = ItemSearcher.getItemById("settings.save", pane, Button.class);
        Button cancel = ItemSearcher.getItemById("settings.cancel", pane, Button.class);

        uiTheme.getItems().clear();
        uiTheme.getItems().addAll("Light", "Dark");
        uiTheme.getSelectionModel().select(Settings.getSetting(Setting.UITHEME));
        uiTheme.setOnAction(_ignored -> {
            if (uiTheme.getSelectionModel().getSelectedItem().equals("Dark")) {
                display.rootControl.getScene().getStylesheets().clear();
                display.rootControl.getScene().getStylesheets().add(Settings.darkMode);

                dialog.getDialogPane().getScene().getStylesheets().add(Settings.darkMode);
            }else{
                display.rootControl.getScene().getStylesheets().clear();
                dialog.getDialogPane().getScene().getStylesheets().clear();
            }
        });

        fullScreenAtStartup.setSelected(Boolean.parseBoolean(Settings.getSetting(Setting.FULLSCREENATSTARTUP)));

        agisoftExecPath.setText(Settings.getSetting(Setting.AGISOFTEXECPATH));
        terraExecPath.setText(Settings.getSetting(Setting.AGISOFTEXECPATH));

        browseAgisoftExecPath.setOnAction(_ignored -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Agisoft Executable Path");

            File file = fileChooser.showOpenDialog(display.rootControl.getScene().getWindow());
            try {
                String version = AgisoftCaller.checkAgisoftVersion(file.getAbsolutePath());

                if(version != null){
                    agisoftIndicator.setImage(ImageUtils.getImage("settings/check", ".png"));
                    agisoftVersion.setText(version);
                }else{
                    agisoftIndicator.setImage(ImageUtils.getImage("settings/cross", ".png"));
                    agisoftVersion.setText("Invalid Agisoft Executable");
                }

                agisoftExecPath.setText(file.getAbsolutePath());

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        });

        save.setOnAction(_ignored -> {
            try {
                Settings.modifySettings(Setting.UITHEME, uiTheme.getValue());
                Settings.modifySettings(Setting.FULLSCREENATSTARTUP, "" + fullScreenAtStartup.isSelected());
                Settings.modifySettings(Setting.AGISOFTEXECPATH, agisoftExecPath.getText());
                Settings.modifySettings(Setting.TERRAEXECPATH, terraExecPath.getText());

                Settings.saveSettings();
            } catch (IOException e) {
                UMASException.throwWindow(ErrorType.INTERNAL, "Could not save settings. Please restart the application!");
            }

            Platform.runLater(() -> {
                dialog.setResult("");
                dialog.close();
            });
        });

        cancel.setOnAction(_ignored -> {
            if(Settings.getSetting(Setting.UITHEME).equals("Light")){
                dialog.getDialogPane().getScene().getStylesheets().clear();
                display.rootControl.getScene().getStylesheets().clear();
            }

            Platform.runLater(() -> {
                dialog.setResult("");
                dialog.close();
            });
        });

    }

    @Override
    public String jsonCallback(ButtonType buttonType) {
        return null;
    }
}
