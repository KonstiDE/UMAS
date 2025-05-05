package wue.eorc.umas;

import com.agisoft.metashape.*;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException, UMASException, InterruptedException {
        primaryStage.setTitle("UAS Mission Application");

        ProjectCache.initCache();

        Path rootPath = FileSystems.getDefault().getPath("").toAbsolutePath();
        Path pythonPath = Paths.get("/opt/metashape-pro/metashape");
        Path filePath = Paths.get(rootPath.toString(),"src", "wue", "eorc", "umas", "agisoft", "test.py");

        ProcessBuilder pb = new ProcessBuilder(pythonPath.toFile().getAbsolutePath(), "-r", filePath.toFile().getAbsolutePath());
        pb.redirectErrorStream(true);
        Process p = pb.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("[Python] " + line); // or log it
            }
        }
        int exitCode = p.waitFor();

        System.out.println(exitCode);

        /*
        System.out.println("Metashape version: " +
                Metashape.getVersion().getMajor() +
                "." +  Metashape.getVersion().getMinor() +
                "." + Metashape.getVersion().getMicro() +
                ", Build: " + Metashape.getVersion().getBuild());
        */

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