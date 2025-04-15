import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        primaryStage.setTitle("UAS Mission Application");

        VBox root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("scenes/main.fxml")));
        SplitPane splitpane = (SplitPane) root.getChildren().get(1);

        AnchorPane newMission = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("scenes/new_mission.fxml")));

        AnchorPane leftAnchorPane = (AnchorPane) splitpane.getItems().getFirst();
        leftAnchorPane.getChildren().add(newMission);

        Scene scene = new Scene(root);

        primaryStage.setScene(scene);
        primaryStage.show();

        //leftAnchorPane.getChildren().addAll(newMission.getChildrenUnmodifiable());
    }
}