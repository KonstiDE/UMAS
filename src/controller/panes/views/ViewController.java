package controller.panes.views;

import controller.panes.mains.DisplayController;
import enums.ErrorType;
import exception.UMASException;
import javafx.collections.ObservableList;
import javafx.scene.layout.Pane;

import java.util.Optional;
import java.util.function.Function;

public interface ViewController {

    void init(Pane pane, DisplayController display) throws UMASException;


}
