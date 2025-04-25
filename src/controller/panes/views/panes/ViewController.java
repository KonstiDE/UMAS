package controller.panes.views.panes;

import controller.panes.mains.DisplayController;
import exception.UMASException;
import javafx.scene.layout.Pane;

public interface ViewController {

    void init(Pane pane, DisplayController display) throws UMASException;


}
