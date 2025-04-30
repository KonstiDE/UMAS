package wue.eorc.umas.controller.panes.views.panes;

import wue.eorc.umas.controller.panes.mains.DisplayController;
import wue.eorc.umas.exception.UMASException;
import javafx.scene.layout.Pane;
import wue.eorc.umas.models.Flight;

public class ShowProcessingController implements ViewController {

    private final Flight flight;

    public ShowProcessingController(Flight flight) {
        this.flight = flight;
    }

    @Override
    public void init(Pane pane, DisplayController display) throws UMASException {

    }

}
