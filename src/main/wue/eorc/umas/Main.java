package wue.eorc.umas;

import javafx.geometry.Rectangle2D;
import javafx.scene.layout.AnchorPane;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import wue.eorc.umas.controller.RootController;
import wue.eorc.umas.enums.Setting;
import wue.eorc.umas.exception.UMASException;
import wue.eorc.umas.loader.ProjectCache;
import wue.eorc.umas.loader.SceneLoader;
import wue.eorc.umas.loader.Settings;

import java.io.IOException;
import java.net.URISyntaxException;

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
        new RootController(root);

        Scene scene = new Scene(root, 1024, 720);

        // Check settings
        if(Settings.getSetting(Setting.UITHEME).equals("Dark")){
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

        primaryStage.setScene(scene);
        primaryStage.show();

    }
}