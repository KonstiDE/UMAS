package wue.eorc.umas.controller.scenes.views.panes;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import wue.eorc.umas.controller.scenes.main.DisplayController;
import wue.eorc.umas.enums.ErrorType;
import wue.eorc.umas.enums.SplitPanePosition;
import wue.eorc.umas.exception.UMASException;
import wue.eorc.umas.loader.SceneLoader;
import wue.eorc.umas.models.Project;
import wue.eorc.umas.utils.FormValidator;
import wue.eorc.umas.utils.ItemSearcher;

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

        button.setOnAction(_ignored -> {
            DirectoryChooser chooser = new DirectoryChooser();
            chooser.setTitle("Select a new folder for a new mission");
            File file = chooser.showDialog(pane.getScene().getWindow());

            if(file != null) {
                browsedDir.setText(file.getAbsolutePath());
                currentDirectory = file.getAbsolutePath();
            }
        });

        create.setOnAction(_ignored -> {
            List<String> texts = FormValidator.validateTextFields(projectName, projectPilot);

            if (texts != null) {
                Project project = new Project(
                        projectName.getText(),
                        projectPilot.getText(),
                        projectLocation.getText(),
                        new File(currentDirectory)
                );

                try {
                    project.save();
                    project = Project.read(project.getFile().getAbsolutePath());

                    display.switchSceneTo(
                            SplitPanePosition.LEFT,
                            display.getSceneLoader().getScene("show_mission"),
                            new ShowProjectController(project)
                    );
                    display.switchSceneTo(
                            SplitPanePosition.CENTER,
                            display.getSceneLoader().getScene("show_flights"),
                            new ShowFlightsController()
                    );

                } catch (IOException | ClassNotFoundException e) {
                    UMASException.throwWindow(ErrorType.USER, "Could not open project. The project file is corrupt.");
                }
            }
        });

    }

}
