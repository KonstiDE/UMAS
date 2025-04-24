package controller.panes.views;

import controller.panes.mains.DisplayController;
import exception.UMASException;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;
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

        });

    }

}
