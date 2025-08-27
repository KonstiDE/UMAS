package wue.eorc.umas;

import javafx.geometry.Rectangle2D;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import wue.eorc.umas.controller.RootController;
import wue.eorc.umas.controller.customs.UMASDialog;
import wue.eorc.umas.controller.scenes.views.dialogs.StaticDialogController;
import wue.eorc.umas.controller.scenes.views.dialogs.agisoft.AlignImagesController;
import wue.eorc.umas.enums.ErrorType;
import wue.eorc.umas.enums.Setting;
import wue.eorc.umas.exception.UMASException;
import wue.eorc.umas.loader.ProjectCache;
import wue.eorc.umas.loader.SceneLoader;
import wue.eorc.umas.loader.Settings;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Optional;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException, UMASException, URISyntaxException {
        primaryStage.setTitle("EORC - UAS Management Application System");

        ProjectCache.createRecentProjectsFile();
        Settings.createSettingsFile();

        SceneLoader loader = new SceneLoader(this.getClass().getClassLoader());

        VBox root = (VBox) loader.getScene("main");
        RootController rootController = new RootController(root, loader);

        Scene scene = new Scene(root, 1024, 720);

        // Check settings
        if(Settings.useDarkLayout()){
            scene.getStylesheets().add(Settings.darkMode);
        }

        Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();

        if(Boolean.parseBoolean(Settings.getSetting(Setting.FULLSCREENATSTARTUP))){
            primaryStage.setMaximized(true);
        }else{
            primaryStage.setWidth(1440);
            primaryStage.setHeight(900);

            primaryStage.setX((primScreenBounds.getWidth() - primaryStage.getWidth()) / 2);
            primaryStage.setY((primScreenBounds.getHeight() - primaryStage.getHeight()) / 2);
        }

        /*primaryStage.getIcons().add(new Image(
                Objects.requireNonNull(this.getClass().getResourceAsStream("icon.ac"))
        )); */

        //primaryStage.setScene(scene);
        //primaryStage.show();

        DialogPane parameterPane = (DialogPane) loader.getScene("agisoft_align_photos");
        StaticDialogController controller = new AlignImagesController();

        Dialog<String> dialog = new UMASDialog(parameterPane, "Align Images", false, true);
        controller.init(parameterPane, rootController.getDisplayController(), dialog);
        dialog.setResultConverter(controller::jsonCallback);
        Optional<String> json = dialog.showAndWait();

    }
}