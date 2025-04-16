package controller.panes.views;

import controller.panes.mains.DisplayController;
import exception.UMASException;
import javafx.scene.layout.Pane;

public interface ViewController {

    /** Async void that is called at startup **/
    void init(Pane pane, DisplayController display) throws UMASException;
}
