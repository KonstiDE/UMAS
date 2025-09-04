package wue.eorc.umas.controller.scenes.views.panes;

import javafx.beans.binding.Bindings;
import javafx.scene.control.MenuItem;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import wue.eorc.umas.controller.scenes.main.DisplayController;
import wue.eorc.umas.controller.scenes.views.dialogs.AddFlightController;
import wue.eorc.umas.enums.ErrorType;
import wue.eorc.umas.enums.ImageType;
import wue.eorc.umas.enums.SplitPanePosition;
import wue.eorc.umas.exception.UMASException;
import wue.eorc.umas.loader.ProjectCache;
import wue.eorc.umas.loader.SceneLoader;
import wue.eorc.umas.models.Flight;
import wue.eorc.umas.utils.ItemSearcher;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

public class ShowFlightsController implements ViewController {

    @Override
    public void init(Pane pane, DisplayController display) throws UMASException {
        TableView<Flight> tableView = ItemSearcher.getGenericControlById("showflights.table", pane, TableView.class, Flight.class);
        tableView.getItems().clear();
        tableView.setEditable(false);
        initTableViewCellFactories(tableView, display);

        Button button = ItemSearcher.getItemById("showflights.refresh", pane, Button.class);
        button.setOnAction(_ignored -> {
            try {
                tableView.getItems().clear();
                init(pane, display);
            } catch (UMASException e) {
                UMASException.throwWindow(ErrorType.INTERNAL, "Could not refresh tableview. Please restart the application.");
            }
        });

        tableView.setRowFactory(tableView1 -> {
            final TableRow<Flight> row = new TableRow<>();
            final ContextMenu contextMenu = new ContextMenu();

            final MenuItem separator = new SeparatorMenuItem();

            final MenuItem buildRGBOrtho = new MenuItem("Build Quick Look");
            buildRGBOrtho.setOnAction(event -> {

            });

            final MenuItem removeMenuItem = new MenuItem("Delete flight");
            removeMenuItem.setOnAction(event -> {
                ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
                ButtonType no = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
                Alert alert = new Alert(Alert.AlertType.WARNING,
                        "Do you really want to delete this flight?", yes, no);

                alert.setTitle("Date format warning");
                Optional<ButtonType> result = alert.showAndWait();

                if (result.orElse(no) == yes) {
                    try {
                        boolean success = ProjectCache.currentlyOpenedProject.removeFlight(row.getItem());
                        if(success){
                            ProjectCache.currentlyOpenedProject.save();
                            tableView1.getItems().remove(row.getItem());
                        }else{
                            UMASException.throwWindow(ErrorType.INTERNAL, "Could not remove flight. Please remove it manually!");
                        }
                    } catch (IOException e) {
                        UMASException.throwWindow(ErrorType.INTERNAL, "Could not remove flight. Please remove it manually!");
                    }
                    initTableViewCellFactories(tableView, display);
                }
            });
            contextMenu.getItems().add(buildRGBOrtho);
            contextMenu.getItems().add(separator);
            contextMenu.getItems().add(removeMenuItem);
            row.contextMenuProperty().bind(
                    Bindings.when(row.emptyProperty())
                            .then((ContextMenu)null)
                            .otherwise(contextMenu)
            );

            return row;
        });

        for(Flight flight : ProjectCache.currentlyOpenedProject.getFlights()){
            tableView.getItems().add(flight);
        }

        Button add = ItemSearcher.getItemById("showflights.add", pane, Button.class);

        add.setOnAction(_ignored -> {
            Flight flight;
            flight = display.openFlightDialog(
                    (DialogPane) display.getSceneLoader().getScene("add_flight"),
                    new AddFlightController()
            );

            if(flight != null){
                tableView.getItems().add(flight);

                if(flight.getFlightParameters() != null){
                    display.getMapController().showFlightArea(flight.getFlightParameters().getCoordinates(), flight.getFlightParameters().getWaypoints());
                }

                ProjectCache.currentlyOpenedProject.addFlight(flight);
                try {
                    ProjectCache.currentlyOpenedProject.save();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }else{
                UMASException.throwWindow(ErrorType.INTERNAL, "Flight now added.");
            }

        });

        tableView.getSelectionModel().selectedItemProperty().addListener(
                (_ignored, oldT, newT) -> {
                    try {
                        display.getMapController().showFlightArea(newT.getFlightParameters().getCoordinates(), newT.getFlightParameters().getWaypoints());
                    } catch (NullPointerException ignored) {  }
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
                    String iconPath = "assets/imgicons/" + switch(imageType) {
                        case RGB -> "rgb.png";
                        case MULTISPECTRAL -> "ms.png";
                        case HYPERSPECTRAL -> "hyper.png";
                        case IR -> "thermal.png";
                        case LIDAR -> "lidar.png";
                        case CALIBRATION -> null;
                    };

                    if(imageType != ImageType.CALIBRATION){
                        ImageView icon = new ImageView(new Image(iconPath));
                        icon.setFitHeight(16);
                        icon.setFitWidth(16);
                        imageViewList.add(icon);
                    }
                }
                return imageViewList;
            }
        });

        TableColumn<Flight, String> heightCol = (TableColumn<Flight, String>) tableView.getColumns().get(8);
        heightCol.setCellValueFactory(cellData -> {
            try {
                return new ReadOnlyObjectWrapper<>(String.valueOf(cellData.getValue().getFlightParameters().getHeight()));
            } catch (NullPointerException e){
                return new ReadOnlyObjectWrapper<>("");
            }
        });

        TableColumn<Flight, String> speedCol = (TableColumn<Flight, String>) tableView.getColumns().get(9);
        speedCol.setCellValueFactory(cellData -> {
            try {
                return new ReadOnlyObjectWrapper<>(String.valueOf(cellData.getValue().getFlightParameters().getSpeed()));
            } catch (NullPointerException e){
                return new ReadOnlyObjectWrapper<>("");
            }
        });

        TableColumn<Flight, String> fontOvCol = (TableColumn<Flight, String>) tableView.getColumns().get(10);
        fontOvCol.setCellValueFactory(cellData -> {
            try {
                return new ReadOnlyObjectWrapper<>(String.valueOf(cellData.getValue().getFlightParameters().getFrontOverlap()));
            } catch (NullPointerException e){
                return new ReadOnlyObjectWrapper<>("");
            }
        });

        TableColumn<Flight, String> sideOvCol = (TableColumn<Flight, String>) tableView.getColumns().get(11);
        sideOvCol.setCellValueFactory(cellData -> {
            try {
                return new ReadOnlyObjectWrapper<>(String.valueOf(cellData.getValue().getFlightParameters().getSideOverlap()));
            } catch (NullPointerException e){
                return new ReadOnlyObjectWrapper<>("");
            }
        });

        TableColumn<Flight, String> startTime = (TableColumn<Flight, String>) tableView.getColumns().get(12);
        startTime.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getStartTime()));

        TableColumn<Flight, String> endTime = (TableColumn<Flight, String>) tableView.getColumns().get(13);
        endTime.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getEndTime()));

        TableColumn<Flight, Void> folderCol = (TableColumn<Flight, Void>) tableView.getColumns().get(14);
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

                URL iconPath = flightDir.exists()
                        ? Objects.requireNonNull(getClass().getClassLoader().getResource("assets/flighttable/explorer_folder.png"))
                        : Objects.requireNonNull(getClass().getClassLoader().getResource("assets/flighttable/explorer_warning.png"));
                imageView.setImage(new Image(iconPath.toString()));

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

        TableColumn<Flight, Void> processedCol = (TableColumn<Flight, Void>) tableView.getColumns().get(15);
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

                imageView.setImage(new Image(String.valueOf(getClass().getClassLoader().getResource("assets/flighttable/gear.png"))));

                imageView.setOnMouseClicked(_ignored -> {
                    File flightDir = new File(flight.getFlightDirectory());

                    if(flightDir.exists()) {
                        try {
                            display.switchSceneTo(
                                    SplitPanePosition.RIGHT,
                                    display.getSceneLoader().getScene("show_processing"),
                                    new ShowProcessingController(flight, display)
                            );
                        } catch (URISyntaxException e) {
                            UMASException.throwWindow(ErrorType.USER, "Could not find the path to Agisoft. Please update this in the settings.");
                        }
                    }else{
                        UMASException.throwWindow(ErrorType.USER, "Please fix all explorer issues before start processing.");
                    }
                });
                imageView.setOnMouseEntered(_ignored -> imageView.setCursor(Cursor.HAND));
                imageView.setOnMouseExited(_ignored -> imageView.setCursor(Cursor.DEFAULT));
                setGraphic(container);
            }

        });

    }

}
