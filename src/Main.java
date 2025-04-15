import controller.RootController;
import enums.SplitPanePosition;
import exception.UMASException;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import loader.SceneLoader;

import java.awt.*;
import java.io.IOException;
import java.util.Objects;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException, UMASException {
        primaryStage.setTitle("UAS Mission Application");

        SceneLoader sceneLoader = new SceneLoader(this.getClass(), "scenes/");

        VBox root = (VBox) sceneLoader.loadSceneFromFXML("main.fxml");
        RootController rootController = new RootController(root);


        Pane newMission = sceneLoader.loadSceneFromFXML("new_mission.fxml");
        rootController.getSplitPaneController().switchSceneTo(SplitPanePosition.LEFT, newMission);

        Scene scene = new Scene(root);

        scene.getStylesheets().add("resources/dark-mode.css");

        primaryStage.setScene(scene);
        primaryStage.show();

        //leftAnchorPane.getChildren().addAll(newMission.getChildrenUnmodifiable());
    }
}