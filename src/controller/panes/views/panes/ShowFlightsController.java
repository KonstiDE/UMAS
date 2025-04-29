package controller.panes.views.panes;

import controller.panes.mains.DisplayController;
import controller.panes.views.dialogs.AddFlightController;
import exception.UMASException;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import loader.ProjectCache;
import loader.SceneLoader;
import models.Flight;
import utils.ItemSearcher;

public class ShowFlightsController implements ViewController {

    @Override
    public void init(Pane pane, DisplayController display) throws UMASException {

        TableView<Flight> tableView = ItemSearcher.getGenericControlById("showflights.table", pane, TableView.class, Flight.class);
        tableView.setEditable(false);
        initTableViewCellFactories(tableView);

        for(Flight flight : ProjectCache.currentlyOpenedProject.getFlights()){
            tableView.getItems().add(flight);
        }

        Button add = ItemSearcher.getItemById("showflights.add", pane, Button.class);

        add.setOnAction(_ -> {
            Flight flight = display.openDialog(
                    (DialogPane) SceneLoader.getAvailableScenes().get("add_flight"),
                    new AddFlightController()
            );
            tableView.getItems().add(flight);
        });

    }

    @SuppressWarnings("unchecked")
    private void initTableViewCellFactories(TableView<Flight> tableView) {
        TableColumn<Flight, String> dateCol = (TableColumn<Flight, String>) tableView.getColumns().getFirst();
        dateCol.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getDate()));

        TableColumn<Flight, String> locationCol = (TableColumn<Flight, String>) tableView.getColumns().get(1);
        locationCol.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getLocation()));

        TableColumn<Flight, String> aoiCol = (TableColumn<Flight, String>) tableView.getColumns().get(2);
        aoiCol.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getAoi()));

        TableColumn<Flight, String> UAVCol = (TableColumn<Flight, String>) tableView.getColumns().get(3);
        UAVCol.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getUav().getName()));

        TableColumn<Flight, String> sensorCol = (TableColumn<Flight, String>) tableView.getColumns().get(4);
        sensorCol.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getSensor().getName()));

        TableColumn<Flight, String> pilotCol = (TableColumn<Flight, String>) tableView.getColumns().get(5);
        pilotCol.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getPilot()));

        TableColumn<Flight, String> coPilotCol = (TableColumn<Flight, String>) tableView.getColumns().get(6);
        coPilotCol.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getCoPilot()));

        TableColumn<Flight, String> imageCol = (TableColumn<Flight, String>) tableView.getColumns().get(7);
        imageCol.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>("200"));

        TableColumn<Flight, String> heightCol = (TableColumn<Flight, String>) tableView.getColumns().get(8);
        heightCol.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getHeight()));

        TableColumn<Flight, String> processedCol = (TableColumn<Flight, String>) tableView.getColumns().get(9);
        processedCol.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>("No"));

    }

}
