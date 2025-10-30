package wue.eorc.umas.models.status;

import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import wue.eorc.umas.enums.TaskType;

import java.util.Objects;

public class QueueItem {

    private final StackPane stackPane;

    private final String description;

    public QueueItem(TaskType taskType, String description) {
        this.stackPane = new StackPane();
        this.stackPane.setPrefSize(20, 20);
        this.stackPane.setMaxSize(20, 20);

        this.description = description;

        ImageView imageView = new ImageView();
        imageView.setImage(new Image(Objects.requireNonNull(
                getClass().getClassLoader().getResourceAsStream("assets/ext/metashape.png"))
        ));
        imageView.setFitHeight(12);
        imageView.setFitWidth(12);

        stackPane.setBackground(Background.fill(Color.rgb(255, 255, 255)));
        stackPane.getChildren().add(imageView);

        Tooltip toolTipTxt = new Tooltip(description);
        toolTipTxt.setShowDelay(Duration.ZERO);
        Tooltip.install(stackPane, toolTipTxt);

        stackPane.hoverProperty().addListener((ov, oldV, newV) -> {
            if(newV){
                stackPane.setBackground(Background.fill(Color.rgb(163, 221, 255)));
            }else{
                stackPane.setBackground(Background.fill(Color.rgb(255, 255, 255)));
            }
        });
    }

    public StackPane getNode() {
        return this.stackPane;
    }

    public String getDescription() {
        return this.description;
    }

}
