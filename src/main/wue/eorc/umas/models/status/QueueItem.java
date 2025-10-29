package wue.eorc.umas.models.status;

import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import wue.eorc.umas.enums.TaskType;
import wue.eorc.umas.utils.system.Colors;

import java.util.Objects;

public class QueueItem {

    private final TaskType taskType;
    private final String description;

    private final StackPane stackPane;

    public QueueItem(TaskType taskType, String description) {
        this.taskType = taskType;
        this.description = description;

        this.stackPane = new StackPane();
        this.stackPane.setPrefSize(20, 20);
        this.stackPane.setMaxSize(20, 20);

        ImageView imageView = new ImageView();
        imageView.setImage(new Image(Objects.requireNonNull(
                getClass().getClassLoader().getResourceAsStream("assets/ext/metashape.png"))
        ));
        imageView.setFitHeight(10);
        imageView.setFitWidth(10);

        stackPane.setBackground(Background.fill(Color.rgb(74, 144, 226)));
        stackPane.getChildren().add(imageView);
    }

    public StackPane getNode() {
        return this.stackPane;
    }

}
