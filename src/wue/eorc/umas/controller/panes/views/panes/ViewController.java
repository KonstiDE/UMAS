package wue.eorc.umas.controller.panes.views.panes;

import wue.eorc.umas.controller.panes.mains.DisplayController;
import wue.eorc.umas.exception.UMASException;
import javafx.scene.layout.Pane;

public interface ViewController {

    void init(Pane pane, DisplayController display) throws UMASException;


}
