package wue.eorc.umas.controller.scenes.views.panes;

import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.Pane;
import wue.eorc.umas.controller.scenes.main.DisplayController;
import wue.eorc.umas.exception.UMASException;
import wue.eorc.umas.loader.ProjectCache;
import wue.eorc.umas.models.Project;
import wue.eorc.umas.utils.ItemSearcher;

import java.io.IOException;

public class ShowProjectController implements ViewController {

    private Project project;

    public ShowProjectController(Project project) throws IOException, ClassNotFoundException {
        this.project = project;

        ProjectCache.addToCache(project.getFile());
    }

    @Override
    public void init(Pane pane, DisplayController display) throws UMASException {
        TextField projectName = ItemSearcher.getItemById("showproject.name", pane, TextField.class);
        TextField projectPilot = ItemSearcher.getItemById("showproject.pilot", pane, TextField.class);
        TextField projectLocation = ItemSearcher.getItemById("showproject.location", pane, TextField.class);

        ToggleButton editor = ItemSearcher.getItemById("showproject.editor", pane, ToggleButton.class);
        editor.setSelected(false);

        projectName.setText(project.getName());
        projectPilot.setText(project.getPilot());
        projectLocation.setText(projectLocation.getText());

        projectName.setEditable(false);
        projectPilot.setEditable(false);
        projectLocation.setEditable(false);

        editor.setOnAction(_ignored -> {
            projectName.setEditable(editor.isSelected());
            projectPilot.setEditable(editor.isSelected());
            projectLocation.setEditable(editor.isSelected());
        });

    }

}
