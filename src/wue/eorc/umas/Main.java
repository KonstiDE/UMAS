package wue.eorc.umas;

import wue.eorc.umas.controller.RootController;
import wue.eorc.umas.exception.UMASException;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import wue.eorc.umas.loader.ProjectCache;
import wue.eorc.umas.loader.SceneLoader;

import java.io.IOException;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException, UMASException {
        primaryStage.setTitle("UAS Mission Application");

        ProjectCache.initCache();

        SceneLoader loader = new SceneLoader(this.getClass(), "scenes/");

        VBox root = (VBox) loader.loadSceneFromFXML("main.fxml");
        new RootController(loader, root);

        Scene scene = new Scene(root, 1024, 720);

        // scene.getStylesheets().add("wue.eorc.umas.resources/dark-mode.css");

        primaryStage.setScene(scene);
        primaryStage.setWidth(1440);
        primaryStage.setHeight(900);

        primaryStage.show();

        Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
        primaryStage.setX((primScreenBounds.getWidth() - primaryStage.getWidth()) / 2);
        primaryStage.setY((primScreenBounds.getHeight() - primaryStage.getHeight()) / 2);

    }
}