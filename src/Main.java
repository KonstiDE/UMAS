import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
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

        Parent parent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("scenes/main.fxml")));

        Parent newMission = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("scenes/new_mission.fxml")));

        Scene root = new Scene(parent);

        AnchorPane leftAnchorPane = (AnchorPane) root.lookup("left_anchor");
        leftAnchorPane.getChildren().addAll(newMission.getChildrenUnmodifiable());

        primaryStage.setScene(root);
        primaryStage.show();
    }
}

