package wue.eorc.umas.controller.panes.mains;

import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;

public class StatusController {

    public HBox hBox;

    private ProgressBar progressBar;
    private Label progressLabel;

    public StatusController(HBox hBox){
        this.hBox = hBox;
        this.progressBar = (ProgressBar) hBox.getChildren().get(hBox.getChildren().size()- 1);
        this.progressLabel = (Label) hBox.getChildren().get(hBox.getChildren().size() - 2);
    }

    public void updateStatus(float progress){
        this.progressLabel.setText(Math.round(progress * 100) / 100 + "%  ");
        this.progressBar.setProgress(progress / 100);
    }

}
