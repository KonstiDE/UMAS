package wue.eorc.umas.controller.panes.views.panes;

import wue.eorc.umas.controller.panes.mains.DisplayController;
import wue.eorc.umas.controller.panes.views.dialogs.AddFlightController;
import wue.eorc.umas.enums.ImageType;
import wue.eorc.umas.enums.SplitPanePosition;
import wue.eorc.umas.exception.UMASException;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import wue.eorc.umas.loader.ProjectCache;
import wue.eorc.umas.loader.SceneLoader;
import wue.eorc.umas.models.Flight;
import wue.eorc.umas.utils.ItemSearcher;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ShowFlightsController implements ViewController {

    @Override
    public void init(Pane pane, DisplayController display) throws UMASException {

        TableView<Flight> tableView = ItemSearcher.getGenericControlById("showflights.table", pane, TableView.class, Flight.class);
        tableView.setEditable(false);
        initTableViewCellFactories(tableView, display);

        for(Flight flight : ProjectCache.currentlyOpenedProject.getFlights()){
            tableView.getItems().add(flight);
        }

        Button add = ItemSearcher.getItemById("showflights.add", pane, Button.class);

        add.setOnAction(_ignored -> {
            Flight flight;
            try {
                flight = display.openFlightDialog(
                        (DialogPane) SceneLoader.getDialogSceneReset("add_flight"),
                        new AddFlightController()
                );
            } catch (UMASException e) {
                throw new RuntimeException(e);
            }
            tableView.getItems().add(flight);
        });

    }

    @SuppressWarnings("unchecked")
    private void initTableViewCellFactories(TableView<Flight> tableView, DisplayController display) {
        TableColumn<Flight, String> dateCol = (TableColumn<Flight, String>) tableView.getColumns().get(0);
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
        imageCol.setCellFactory(_ignored -> new TableCell<>() {
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

                for(ImageType imageType : flight.getImageTypes().keySet()) {
                    String iconpath = "wue/eorc/umas/assets/imgicons/" + switch(imageType) {
                        case RGB -> "rgb.png";
                        case MULTISPECTRAL -> "ms.png";
                        case HYPERSPECTRAL -> "hyper.png";
                        case IR -> "thermal.png";
                        case LIDAR -> "lidar.png";
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
        folderCol.setCellFactory(_ignored -> new TableCell<>() {
            private final ImageView imageView = new ImageView();
            private final StackPane container = new StackPane();

            {
                imageView.setFitHeight(16);
                imageView.setFitWidth(16);
                container.getChildren().add(imageView);
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
                        ? "wue/eorc/umas/assets/icons8-folder-144.png"
                        : "wue/eorc/umas/assets/icons8-warning-100.png";
                imageView.setImage(new Image(iconPath));

                imageView.setOnMouseClicked(_ignored -> {
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
                imageView.setOnMouseEntered(_ignored -> imageView.setCursor(Cursor.HAND));
                imageView.setOnMouseExited(_ignored -> imageView.setCursor(Cursor.DEFAULT));
                setGraphic(container);
            }
        });

        TableColumn<Flight, Void> processedCol = (TableColumn<Flight, Void>) tableView.getColumns().get(10);
        processedCol.setCellFactory(_ignored -> new TableCell<>() {
            private final ImageView imageView = new ImageView();
            private final StackPane container = new StackPane();
            {
                imageView.setFitHeight(16);
                imageView.setFitWidth(16);
                container.getChildren().add(imageView);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || getIndex() < 0 || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                    return;
                }

                Flight flight = getTableView().getItems().get(getIndex());

                imageView.setImage(new Image("wue/eorc/umas/assets/icons8-gear-144.png"));

                imageView.setOnMouseClicked(_ignored -> {
                    display.switchSceneTo(
                            SplitPanePosition.RIGHT,
                            SceneLoader.getAvailableScenes().get("show_processing"),
                            new ShowProcessingController(flight)
                    );
                });
                imageView.setOnMouseEntered(_ignored -> imageView.setCursor(Cursor.HAND));
                imageView.setOnMouseExited(_ignored -> imageView.setCursor(Cursor.DEFAULT));
                setGraphic(container);
            }

        });

    }

}
