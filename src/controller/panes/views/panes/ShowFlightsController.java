package controller.panes.views.panes;

import controller.panes.mains.DisplayController;
import controller.panes.views.dialogs.AddFlightController;
import exception.UMASException;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;
import loader.SceneLoader;
import models.Flight;
import utils.ItemSearcher;

public class ShowFlightsController implements ViewController {

    @Override
    public void init(Pane pane, DisplayController display) throws UMASException {

        @SuppressWarnings("rawtypes")
        TableView tableView = ItemSearcher.getItemById("showflights.table", pane, TableView.class);
        tableView.setEditable(false);

        Button add = ItemSearcher.getItemById("showflights.add", pane, Button.class);

        add.setOnAction(_ -> {
            Flight flight = display.openDialog(
                    (DialogPane) SceneLoader.getAvailableScenes().get("add_flight"),
                    new AddFlightController()
            );

            //Add flight to Table here

        });

    }

}
