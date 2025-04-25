package controller.panes.views.panes;

import controller.panes.mains.DisplayController;
import enums.ErrorType;
import enums.SplitPanePosition;
import exception.UMASException;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import loader.SceneLoader;
import models.Project;
import utils.FormValidator;
import utils.ItemSearcher;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class CreateProjectController implements ViewController {

    public String currentDirectory;

    @Override
    public void init(Pane pane, DisplayController display) throws UMASException {
        TextField projectName = ItemSearcher.getItemById("newproject.name", pane, TextField.class);
        TextField projectPilot = ItemSearcher.getItemById("newproject.pilot", pane, TextField.class);
        TextField projectLocation = ItemSearcher.getItemById("newproject.location", pane, TextField.class);

        Button button = ItemSearcher.getItemById("newproject.dirbrowse", pane, Button.class);
        Label browsedDir = ItemSearcher.getItemById("newproject.dirlabel", pane, Label.class);

        Button create = ItemSearcher.getItemById("newproject.create", pane, Button.class);

        button.setOnAction(_ -> {
            DirectoryChooser chooser = new DirectoryChooser();
            chooser.setTitle("Select a new folder for a new mission");
            File file = chooser.showDialog(pane.getScene().getWindow());

            if(file != null) {
                browsedDir.setText(file.getAbsolutePath());
                currentDirectory = file.getAbsolutePath();
            }
        });

        create.setOnAction(_ -> {
            List<String> texts = FormValidator.validateTextFields(projectName, projectPilot);

            if (texts != null) {
                Project project = new Project(
                        projectName.getText(),
                        projectPilot.getText(),
                        projectLocation.getText(),
                        new File(currentDirectory),
                        List.of()
                );

                try {
                    project.save();
                    project = Project.read(project.getAbsoluteFilePath());

                    display.switchSceneTo(
                            SplitPanePosition.LEFT,
                            SceneLoader.getAvailableScenes().get("show_mission"),
                            new ShowProjectController(project)
                    );
                    display.switchSceneTo(
                            SplitPanePosition.CENTER,
                            SceneLoader.getAvailableScenes().get("show_flights"),
                            new ShowFlightsController()
                    );

                } catch (IOException | ClassNotFoundException e) {
                    UMASException.throwWindow(ErrorType.USER, "Could not open project. The project file is corrupt.");
                }
            }
        });

    }

}
