package wue.eorc.umas.controller.panes.views.panes;

import javafx.scene.layout.Pane;
import wue.eorc.umas.controller.panes.mains.DisplayController;
import wue.eorc.umas.exception.UMASException;

public interface ViewController {

    void init(Pane pane, DisplayController display) throws UMASException;


}
