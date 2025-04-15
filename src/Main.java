import controller.RootController;
import enums.SplitPanePosition;
import exception.UMASException;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import loader.SceneLoader;

import java.io.IOException;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException, UMASException {
        primaryStage.setTitle("UAS Mission Application");

        SceneLoader loader = new SceneLoader(this.getClass(), "scenes/");

        VBox root = (VBox) loader.loadSceneFromFXML("main.fxml");
        new RootController(root);

        Scene scene = new Scene(root);

        scene.getStylesheets().add("resources/dark-mode.css");

        primaryStage.setScene(scene);
        primaryStage.show();

        //leftAnchorPane.getChildren().addAll(newMission.getChildrenUnmodifiable());
    }
}