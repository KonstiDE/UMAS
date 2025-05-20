package wue.eorc.umas.controller.panes.views.panes;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;
import javafx.util.Callback;
import wue.eorc.umas.controller.panes.mains.DisplayController;
import wue.eorc.umas.controller.panes.mains.MapController;
import wue.eorc.umas.controller.panes.views.dialogs.AddFlightController;
import wue.eorc.umas.enums.ErrorType;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalField;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

public class ShowFlightsController implements ViewController {

    @Override
    public void init(Pane pane, DisplayController display) throws UMASException {

        TableView<Flight> tableView = ItemSearcher.getGenericControlById("showflights.table", pane, TableView.class, Flight.class);
        tableView.setEditable(false);
        initTableViewCellFactories(tableView, display);

        tableView.setRowFactory(tableView1 -> {
            final TableRow<Flight> row = new TableRow<>();
            final ContextMenu contextMenu = new ContextMenu();
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
            try {
                flight = display.openFlightDialog(
                        (DialogPane) SceneLoader.getDialogSceneReset("add_flight"),
                        new AddFlightController()
                );
            } catch (UMASException e) {
                throw new RuntimeException(e);
            }
            tableView.getItems().add(flight);

            if(flight.getFlightParameters() != null){
                display.getMapController().showFlightArea(flight.getFlightParameters().getCoordinates(), flight.getFlightParameters().getWaypoints());
            }
        });

        tableView.getSelectionModel().selectedItemProperty().addListener(
                (_ignored, oldT, newT) -> {
                    display.getMapController().showFlightArea(newT.getFlightParameters().getCoordinates(), newT.getFlightParameters().getWaypoints());
                });

    }

    @SuppressWarnings("unchecked")
    private void initTableViewCellFactories(TableView<Flight> tableView, DisplayController display) {
        TableColumn<Flight, String> dateCol = (TableColumn<Flight, String>) tableView.getColumns().get(0);
        dateCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Flight, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Flight, String> flightStringCellDataFeatures) {
                return new ReadOnlyObjectWrapper<>(flightStringCellDataFeatures.getValue().getDate());
            }
        });

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

                imageView.setImage(new Image("wue/eorc/umas/assets/icons8-gear-144.png"));

                imageView.setOnMouseClicked(_ignored -> {
                    File flightDir = new File(flight.getFlightDirectory());

                    if(flightDir.exists()) {
                        display.switchSceneTo(
                                SplitPanePosition.RIGHT,
                                SceneLoader.getAvailableScenes().get("show_processing"),
                                new ShowProcessingController(flight)
                        );
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
