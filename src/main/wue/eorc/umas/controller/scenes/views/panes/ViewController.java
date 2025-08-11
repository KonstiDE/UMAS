package wue.eorc.umas.controller.scenes.views.panes;

import javafx.scene.layout.Pane;
import wue.eorc.umas.controller.scenes.main.DisplayController;
import wue.eorc.umas.exception.UMASException;

public interface ViewController {

    void init(Pane pane, DisplayController display) throws UMASException;


}
