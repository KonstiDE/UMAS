package wue.eorc.umas;

import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Rectangle2D;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.DialogPane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import wue.eorc.umas.agisoft.AgisoftCaller;
import wue.eorc.umas.controller.RootController;
import wue.eorc.umas.controller.customs.UMASDialog;
import wue.eorc.umas.controller.scenes.views.dialogs.ClosingController;
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
        new RootController(root, loader);

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

        primaryStage.setScene(scene);
        primaryStage.show();

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                if (AgisoftCaller.isRunning){
                    DialogPane dialogPane = (DialogPane) loader.getScene("decision_dor_closing");
                    ClosingController closingController = new ClosingController();

                    UMASDialog closingDialog = new UMASDialog(dialogPane, "Over and out!", true, true);
                    closingDialog.setResultConverter(closingController::jsonCallback);

                    Optional<String> close = closingDialog.showAndWait();
                    closingDialog.hide();
                    closingDialog.close();

                    if (close != null){
                        // Kill all tasks in queue

                        primaryStage.hide();
                        primaryStage.close();
                    }

                }
            }
        });

    }
}