package controller.panes.views;

import controller.panes.mains.DisplayController;
import exception.UMASException;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;
import utils.ItemSearcher;

import java.io.File;

public class NewMissionViewController implements ViewController {

    @Override
    public void init(Pane pane, DisplayController display) throws UMASException {
        Button button = ItemSearcher.getItemById("newmissiondirbrowse", pane, Button.class);
        Label browsedDir = ItemSearcher.getItemById("newmissiondirlabel", pane, Label.class);

        button.setOnAction(_ -> {
            DirectoryChooser chooser = new DirectoryChooser();
            chooser.setTitle("Select a new folder for a new mission");
            File file = chooser.showDialog(pane.getScene().getWindow());
            browsedDir.setText(file.getAbsolutePath());
        });



    }

}
