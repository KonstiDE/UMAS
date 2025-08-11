package wue.eorc.umas.controller.scenes.views.dialogs;

import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import org.controlsfx.control.CheckComboBox;
import wue.eorc.umas.controller.listeners.CopyProgressListener;
import wue.eorc.umas.controller.scenes.main.DisplayController;
import wue.eorc.umas.enums.*;
import wue.eorc.umas.exception.UMASException;
import wue.eorc.umas.futures.FileCopier;
import wue.eorc.umas.loader.ProjectCache;
import wue.eorc.umas.loader.Settings;
import wue.eorc.umas.models.Flight;
import wue.eorc.umas.models.FlightParameters;
import wue.eorc.umas.utils.ItemSearcher;
import wue.eorc.umas.utils.KMZProcessor;

import java.io.File;
import java.nio.file.*;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class AddFlightController implements DialogController, CopyProgressListener {

    private String date;
    private String location;
    private String aoi;
    private String pilot;
    private String coPilot;
    private FlightParameters flightParameters;
    private UAV uav;
    private Sensor sensor;
    private final Map<ImageType, String> imageTypes = new HashMap<>();
    private ProcessingChain processingChain;
    private final List<String> flightsOrigins = new ArrayList<>();
    private final List<String> calibOrigins = new ArrayList<>();
    private String notes;

    private ProgressBar progressBar;
    private Label progressLabel;

    private String flightJson;

    private CompletableFuture<Void> copyJob;

    @Override
    public void init(Pane pane, DisplayController display, Dialog<String> dialog) throws UMASException {
        if(Settings.useDarkLayout()){
            pane.getScene().getStylesheets().add(Settings.darkMode);
        }

        DatePicker datePicker = ItemSearcher.getItemById("addflight.date", pane, DatePicker.class);
        TextField location = ItemSearcher.getItemById("addflight.location", pane, TextField.class);
        TextField aoi = ItemSearcher.getItemById("addflight.aoi", pane, TextField.class);
        TextField pilot = ItemSearcher.getItemById("addflight.pilot", pane, TextField.class);
        TextField coPilot = ItemSearcher.getItemById("addflight.copilot", pane, TextField.class);

        StackPane flightFileDrop = ItemSearcher.getItemById("addflight.flightfile", pane, StackPane.class);
        Label flightFileLabel = ItemSearcher.getItemById("addflight.flightfilelabel", pane, Label.class);

        ComboBox<String> selectUAV = ItemSearcher.getGenericControlById("addflight.uav", pane, ComboBox.class, String.class);
        ComboBox<String> selectSensor = ItemSearcher.getGenericControlById("addflight.sensor", pane, ComboBox.class, String.class);
        ComboBox<String> selectChain =  ItemSearcher.getGenericControlById("addflight.chain", pane, ComboBox.class, String.class);
        CheckComboBox<String> selectImageTypes = ItemSearcher.getGenericControlById("addflight.imagetypes", pane, CheckComboBox.class, String.class);

        Button browse = ItemSearcher.getItemById("addflight.browse", pane, Button.class);
        Button browseCalib = ItemSearcher.getItemById("addflight.browsecalib", pane, Button.class);
        browseCalib.setDisable(true);

        TreeView<String> flightDirs = ItemSearcher.getGenericControlById("addflight.flightdirs", pane, TreeView.class, String.class);
        TreeView<String> calibDirs = ItemSearcher.getGenericControlById("addflight.calibdirs", pane, TreeView.class, String.class);
        calibDirs.setDisable(true);

        TextArea notes =  ItemSearcher.getItemById("addflight.notes", pane, TextArea.class);

        this.progressBar = ItemSearcher.getItemById("addflight.progress", pane, ProgressBar.class);
        this.progressLabel = ItemSearcher.getItemById("addflight.progressLabel", pane, Label.class);

        Button finish = ItemSearcher.getItemById("addflight.finish", pane, Button.class);

        selectUAV.getItems().addAll(Stream.of(UAV.values()).map(UAV::getName).toList());

        selectUAV.setOnAction(__ignored -> {
            clearComboBoxes(selectSensor);
            clearCheckComboBoxes(selectImageTypes);

            List<Sensor> sensors = switch (UAV.fromName(selectUAV.getValue())) {
                case MAVICM2 -> List.of(Sensor.FIXEDM2);
                case MAVICM3M -> List.of(Sensor.FIXEDM3M);
                case MAVICM3T ->  List.of(Sensor.FIXEDM3T);
                case MAVICM4T -> List.of(Sensor.FIXEDM4T);
                case PHAMTOM -> List.of(Sensor.FIXEDMPHANTOM);
                case M300 -> List.of(Sensor.ALTUM, Sensor.L1, Sensor.H20T, Sensor.MXDUAL);
                case M600 -> List.of(Sensor.ALTUM, Sensor.LIAIRV, Sensor.NANOHP, Sensor.MXDUAL);
                case WINGTRA -> List.of(Sensor.NIKONRGB, Sensor.ALTUM);
                case TRINITY -> List.of(Sensor.D2M, Sensor.ALTUMPT, Sensor.Q2);
            };
            selectSensor.getItems().addAll(sensors.stream().map(Sensor::getName).toList());

            this.uav = UAV.fromName(selectUAV.getValue());
        });

        selectSensor.setOnAction(_ignored -> {
            clearCheckComboBoxes(selectImageTypes);

            List<ImageType> imageTypes = switch (Sensor.fromName(selectSensor.getValue())){
                case FIXEDM2, FIXEDMPHANTOM -> List.of(ImageType.RGB);
                case FIXEDM3M -> List.of(ImageType.RGB, ImageType.MULTISPECTRAL);
                case FIXEDM3T, FIXEDM4T, H20T -> List.of(ImageType.RGB, ImageType.IR);
                case MXDUAL -> List.of(ImageType.MULTISPECTRAL);
                case D2M -> List.of(ImageType.MULTISPECTRAL);
                case ALTUM -> List.of(ImageType.RGB, ImageType.MULTISPECTRAL);
                case ALTUMPT -> List.of(ImageType.MULTISPECTRAL);
                case L1 -> List.of(ImageType.LIDAR);
                case NIKONRGB -> List.of(ImageType.RGB);
                case NANOHP -> List.of(ImageType.HYPERSPECTRAL);
                case LIAIRV -> List.of(ImageType.LIDAR);
                case Q2 -> List.of(ImageType.LIDAR);
            };
            selectImageTypes.getItems().addAll(imageTypes.stream().map(ImageType::name).toList());


            this.sensor = Sensor.fromName(selectSensor.getValue());
        });

        selectChain.getItems().addAll(Stream.of(ProcessingChain.values()).map(ProcessingChain::getName).toList());

        selectChain.setOnAction(_ignored -> {
            this.processingChain = ProcessingChain.fromName(selectChain.getValue());
        });

        selectImageTypes.getCheckModel().getCheckedIndices().addListener((ListChangeListener<Integer>) _ignored -> {
            for(ImageType imageType : selectImageTypes.getCheckModel().getCheckedItems()
                    .stream()
                    .map(ImageType::valueOf)
                    .toList()){

                this.imageTypes.put(imageType, null);

            }

            if(this.imageTypes.containsKey(ImageType.MULTISPECTRAL)){
                browseCalib.setDisable(false);
                calibDirs.setDisable(false);
            }else{
                browseCalib.setDisable(true);
                calibDirs.setDisable(true);
            }
        });

        browse.setOnAction(_ignored -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Choose an image directory");
            File path = directoryChooser.showDialog(display.rootControl.getScene().getWindow());

            if(path != null){
                addPath(flightDirs, path.getAbsolutePath());
                this.flightsOrigins.add(path.getAbsolutePath());

                addTreeViewDeleteBehavior(flightDirs, this.flightsOrigins);
            }
        });

        browseCalib.setOnAction(_ignored -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Choose an calibration directory");
            File path = directoryChooser.showDialog(display.rootControl.getScene().getWindow());

            if(path != null){
                addPath(calibDirs, path.getAbsolutePath());
                this.calibOrigins.add(path.getAbsolutePath());

                addTreeViewDeleteBehavior(calibDirs, this.calibOrigins);
            }
        });

        datePicker.setOnAction(_ignored -> this.date = datePicker.getEditor().getCharacters().toString());

        location.textProperty().addListener(_ignored -> this.location = location.getText());
        this.location = location.getText();
        aoi.textProperty().addListener(_ignored -> this.aoi = aoi.getText());
        this.aoi = aoi.getText();
        pilot.textProperty().addListener(_ignored -> this.pilot = pilot.getText());
        this.pilot = pilot.getText();
        coPilot.textProperty().addListener(_ignored -> this.coPilot = coPilot.getText());
        this.coPilot = coPilot.getText();
        notes.textProperty().addListener(_ignored -> this.notes = notes.textProperty().get());
        this.notes = notes.textProperty().get();

        finish.setOnAction(_ignored -> {
            if(validate()){
                Path baseDirectory = Paths.get(ProjectCache.currentlyOpenedProject.getFile().getParent());

                try {
                    Flight flight = new Flight(this.date, this.location, this.aoi, this.pilot, this.coPilot,
                            this.flightParameters, this.uav, this.sensor, this.imageTypes, this.processingChain,
                            baseDirectory.toFile().getAbsolutePath(), this.flightsOrigins, this.calibOrigins, this.notes);

                    this.flightJson = Flight.toJson(flight);

                    FileCopier fileCopier = new FileCopier(this, flight);
                    this.copyJob = fileCopier.getCopyTask();

                    fileCopier.getCopyTask().thenRun(() -> {
                        FileTime[] times = fileCopier.getTimes();
                        LocalDateTime start = OffsetDateTime.parse(times[0].toString()).toLocalDateTime();
                        LocalDateTime end = OffsetDateTime.parse(times[1].toString()).toLocalDateTime();

                        DateTimeFormatter minuteFormatter = DateTimeFormatter.ofPattern("HH:mm");

                        flight.setStartTime(minuteFormatter.format(start));
                        flight.setEndTime(minuteFormatter.format(end));

                        Platform.runLater(() -> {
                            dialog.setResult(this.flightJson);
                            dialog.hide();
                            dialog.close();
                        });

                    });
                }catch (UMASException e){
                    UMASException.throwWindow(ErrorType.USER, "Could not create the folder structure for flights.");
                }

            }
        });

        dialog.getDialogPane().getScene().getWindow().setOnCloseRequest(windowEvent -> {
            if(this.copyJob != null && !this.copyJob.isDone()){
                Alert alert = new Alert(Alert.AlertType.WARNING,
                                "Files are still being copied. Do you want to cancel this operation?",
                                ButtonType.YES,
                                ButtonType.NO);
                alert.setTitle("Date format warning");
                Optional<ButtonType> result = alert.showAndWait();

                if(result.isPresent() && result.get() == ButtonType.YES){
                    this.copyJob.cancel(true);
                    dialog.setResult(null);
                    dialog.close();
                }else{
                    windowEvent.consume();
                }
            }else{
                dialog.close();
            }
        });

        flightFileDrop.setStyle("-fx-border-color: #999; -fx-border-width: 3; -fx-border-style: dashed;" +
                "-fx-background-color: transparent;");
        flightFileDrop.setPrefSize(400, 300);

        // Drag over
        flightFileDrop.setOnDragOver(event -> {
            if (event.getGestureSource() != flightFileDrop &&
                    event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY);
            }
            event.consume();
        });

        flightFileDrop.setOnDragEntered(event -> {
            if (event.getGestureSource() != flightFileDrop &&
                    event.getDragboard().hasFiles()) {
                flightFileDrop.setStyle("-fx-border-color: #666; -fx-border-width: 3; -fx-border-style: solid;" +
                        "-fx-background-color: transparent;");
            }
            event.consume();
        });

        flightFileDrop.setOnDragExited(event -> {
            flightFileDrop.setStyle("-fx-border-color: #999; -fx-border-width: 3; -fx-border-style: dashed;" +
                    "-fx-background-color: transparent;");
            event.consume();
        });

        flightFileDrop.setOnDragDropped((DragEvent event) -> {
            Dragboard db = event.getDragboard();
            boolean success = false;

            if (db.hasFiles()) {
                List<File> files = db.getFiles();
                for (File file : files) {
                    flightFileLabel.setText(file.getName());
                    this.flightParameters = KMZProcessor.processKmz(file);
                }
                success = true;
            }

            event.setDropCompleted(success);
            event.consume();
        });

    }

    private void addPath(TreeView<String> treeView, String pathStr) {
        if (pathStr == null || pathStr.isEmpty()) return;

        Path newPath = Paths.get(pathStr);

        if (treeView.getRoot() == null) {
            TreeItem<String> rootItem = new TreeItem<>(newPath.getRoot() != null ? newPath.getRoot().toString() : "/");
            rootItem.setExpanded(true);
            treeView.setRoot(rootItem);
            insertPath(rootItem, newPath);
        } else {
            TreeItem<String> root = treeView.getRoot();

            Path treeRootPath = Paths.get(root.getValue());

            if (!newPath.startsWith(treeRootPath)) {
                Path newCommonRoot = findCommonRoot(treeRootPath, newPath);
                if (newCommonRoot != null) {
                    TreeItem<String> newRoot = new TreeItem<>(newCommonRoot.toString());
                    newRoot.setExpanded(true);

                    root = rebaseTree(root, newCommonRoot.relativize(treeRootPath));
                    newRoot.getChildren().add(root);

                    treeView.setRoot(newRoot);
                    root = newRoot;
                }
            }
            insertPath(root, newPath);
        }
    }

    private TreeItem<String> rebaseTree(TreeItem<String> oldRoot, Path relativePath) {
        TreeItem<String> current = oldRoot;
        for (int i = relativePath.getNameCount() - 1; i >= 0; i--) {
            TreeItem<String> newItem = new TreeItem<>(relativePath.getName(i).toString());
            newItem.getChildren().add(current);
            current = newItem;
        }
        return current;
    }

    private Path findCommonRoot(Path p1, Path p2) {
        int minCount = Math.min(p1.getNameCount(), p2.getNameCount());
        int i = 0;
        while (i < minCount && p1.getName(i).equals(p2.getName(i))) {
            i++;
        }
        if (i == 0) return null;
        return p1.subpath(0, i);
    }

    private void insertPath(TreeItem<String> root, Path fullPath) {
        TreeItem<String> current = root;

        Path relativePath = fullPath;
        Path rootPath = Paths.get(root.getValue());
        if (fullPath.startsWith(rootPath)) {
            relativePath = rootPath.relativize(fullPath);
        }

        for (Path part : relativePath) {
            TreeItem<String> child = findChild(current, part.toString());
            if (child == null) {
                child = new TreeItem<>(part.toString());
                child.setExpanded(true);
                current.getChildren().add(child);
            }
            current = child;
        }
    }

    private TreeItem<String> findChild(TreeItem<String> parent, String value) {
        for (TreeItem<String> child : parent.getChildren()) {
            if (child.getValue().equals(value)) {
                return child;
            }
        }
        return null;
    }

    private void clearComboBoxes(ComboBox<?>... comboBoxes){
        for (ComboBox<?> comboBox : comboBoxes) {
            comboBox.getItems().clear();
        }
    }

    private void clearCheckComboBoxes(CheckComboBox<?>... choiceBoxes){
        for (CheckComboBox<?> checkComboBox : choiceBoxes) {
            checkComboBox.getItems().clear();
        }
    }

    public boolean validate(Control... controls){
        for (Control control : controls) {
            if(control instanceof DatePicker){
                if(((DatePicker) control).getEditor().getCharacters().toString().trim().isEmpty()){
                    UMASException.throwWindow(ErrorType.USER, "Please enter a valid date!");
                    return false;
                }
            } else if(control instanceof TextField){
                if(((TextField) control).getText().isEmpty()){
                    UMASException.throwWindow(ErrorType.USER, "Please fill out the textfield \"" + control.getAccessibleText() + "\"");
                    return false;
                }
            }else if(control instanceof ComboBox){
                if(((ComboBox<?>) control).getSelectionModel().getSelectedItem() == null){
                    UMASException.throwWindow(ErrorType.USER, "Please select a valid \"" +  control.getAccessibleText() + "\"");
                    return false;
                }
            }else if(control instanceof CheckComboBox){
                if(((CheckComboBox<?>) control).getCheckModel().getCheckedItems().isEmpty()){
                    UMASException.throwWindow(ErrorType.USER, "Please select at least one valid option of \"" +  control.getAccessibleText() + "\"");
                    return false;
                }
            }else if(control instanceof TreeView<?>){
                if(control.getId().contains("flightdirs") && ((TreeView<?>) control).getRoot() != null && !((TreeView<?>) control).getRoot().getChildren().isEmpty()){
                    UMASException.throwWindow(ErrorType.USER, "Please select at least one flight directory at \"" +  control.getAccessibleText() + "\"");
                    return false;
                }
            }
        }
        return true;
    }

    private void addTreeViewDeleteBehavior(TreeView<String> treeView, List<String> origins) {
        treeView.setCellFactory(tv -> {
            TreeCell<String> cell = new TreeCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? null : item);
                }
            };

            cell.setOnContextMenuRequested(event -> {
                if (!cell.isEmpty()) {
                    ContextMenu contextMenu = new ContextMenu();
                    MenuItem deleteItem = new MenuItem("Delete");

                    deleteItem.setOnAction(e -> {
                        TreeItem<String> selectedItem = cell.getTreeItem();
                        TreeItem<String> parent = selectedItem.getParent();

                        String fullPath = buildFullPath(selectedItem);
                        origins.removeIf(path -> path.equals(fullPath));

                        if (parent != null) {
                            parent.getChildren().remove(selectedItem);
                            if (parent.getChildren().isEmpty() && parent.getParent() == null) {
                                treeView.setRoot(null);
                            }
                        } else {
                            treeView.setRoot(null);
                        }
                    });

                    contextMenu.getItems().add(deleteItem);
                    contextMenu.show(cell, event.getScreenX(), event.getScreenY());
                }
            });

            return cell;
        });
    }

    private String buildFullPath(TreeItem<String> item) {
        List<String> parts = new ArrayList<>();
        TreeItem<String> current = item;
        while (current != null) {
            parts.add(current.getValue());
            current = current.getParent();
        }
        Collections.reverse(parts);
        return Paths.get("", parts.toArray(new String[0])).toFile().getAbsolutePath();
    }


    @Override
    public String jsonCallback(ButtonType buttonType) {
        return this.flightJson;
    }


    @Override
    public void receivedProgress(double progress) {
        this.progressBar.progressProperty().setValue(progress);
        Platform.runLater(() -> this.progressLabel.setText(String.format("%d%%", (int) (progress * 100))));
    }
}
