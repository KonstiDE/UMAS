package controller.panes.views.panes;

import controller.panes.mains.DisplayController;
import controller.panes.views.dialogs.AddFlightController;
import enums.ImageType;
import exception.UMASException;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import loader.ProjectCache;
import loader.SceneLoader;
import models.Flight;
import utils.ItemSearcher;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

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

        TableColumn<Flight, Void> imageCol = (TableColumn<Flight, Void>) tableView.getColumns().get(7);
        imageCol.setCellFactory(col -> new TableCell<>() {
            private final HBox hbox = new HBox(2);

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || getIndex() < 0 || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                    return;
                }

                Flight flight = getTableView().getItems().get(getIndex());

                ArrayList<ImageView> imageViewList = getImageViews(flight);

                hbox.getChildren().setAll(imageViewList);
                hbox.setSpacing(2);
                setGraphic(hbox);
            }

            private static ArrayList<ImageView> getImageViews(Flight flight) {
                ArrayList<ImageView> imageViewList = new ArrayList<>();

                for(ImageType imageType : flight.getImageTypes()) {
                    String iconpath = "assets/" + switch(imageType) {
                        case RGB -> "rgb.png";
                        case MULTISPECTRAL -> "ms.png";
                        default -> "rgb.png";
                    };

                    ImageView icon = new ImageView(new Image(iconpath));
                    icon.setFitHeight(16);
                    icon.setFitWidth(16);
                    imageViewList.add(icon);
                }
                return imageViewList;
            }
        });

        TableColumn<Flight, String> heightCol = (TableColumn<Flight, String>) tableView.getColumns().get(8);
        heightCol.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getHeight()));

        TableColumn<Flight, Void> folderCol = (TableColumn<Flight, Void>) tableView.getColumns().get(9);
        folderCol.setCellFactory(col -> new TableCell<>() {
            private final ImageView imageView = new ImageView();
            private final StackPane container = new StackPane();

            {
                imageView.setFitHeight(16);
                imageView.setFitWidth(16);
                container.getChildren().add(imageView);    // optional spacing
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || getIndex() < 0 || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                    return;
                }

                Flight flight = getTableView().getItems().get(getIndex());
                File flightDir = new File(flight.getFlightDirectory());

                String iconPath = flightDir.exists()
                        ? "/assets/icons8-folder-144.png"
                        : "/assets/icons8-warning-100.png";
                imageView.setImage(new Image(iconPath));

                imageView.setOnMouseClicked(_ -> {
                    if (flightDir.exists()) {
                        new Thread(() -> {
                            try {
                                Desktop.getDesktop().open(flightDir);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }).start();
                    }
                });
                imageView.setOnMouseEntered(e -> imageView.setCursor(Cursor.HAND));
                imageView.setOnMouseExited(e -> imageView.setCursor(Cursor.DEFAULT));


                setGraphic(container);
            }
        });

        TableColumn<Flight, String> processedCol = (TableColumn<Flight, String>) tableView.getColumns().get(10);
        processedCol.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>("No"));

    }

}
