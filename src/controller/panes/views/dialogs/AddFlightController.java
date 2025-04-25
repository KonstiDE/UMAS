package controller.panes.views.dialogs;

import controller.panes.mains.DisplayController;
import enums.ImageType;
import enums.Sensor;
import enums.UAV;
import exception.UMASException;
import javafx.collections.ListChangeListener;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import models.Flight;
import models.ImageCollection;
import org.controlsfx.control.CheckComboBox;
import utils.ItemSearcher;

import java.io.File;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

public class AddFlightController implements DialogController {

    private Date date;
    private String location;
    private String aoi;
    private String pilot;
    private String coPilot;
    private UAV uav;
    private Sensor sensor;
    private List<ImageType> imageTypes;
    private File baseDirectory;
    private String notes;

    @Override
    public void init(Pane pane, DisplayController display) throws UMASException {
        DatePicker datePicker = ItemSearcher.getItemById("addflight.date", pane, DatePicker.class);
        TextField location = ItemSearcher.getItemById("addflight.location", pane, TextField.class);
        TextField aoi = ItemSearcher.getItemById("addflight.aoi", pane, TextField.class);
        TextField pilot = ItemSearcher.getItemById("addflight.pilot", pane, TextField.class);
        TextField coPilot = ItemSearcher.getItemById("addflight.copilot", pane, TextField.class);

        ComboBox<String> selectUAV = ItemSearcher.getGenericControlById("addflight.uav", pane, ComboBox.class, String.class);
        ComboBox<String> selectSensor = ItemSearcher.getGenericControlById("addflight.sensor", pane, ComboBox.class, String.class);
        CheckComboBox<String> selectImageTypes = ItemSearcher.getGenericControlById("addflight.imagetypes", pane, CheckComboBox.class, String.class);

        Button browse = ItemSearcher.getItemById("addflight.browse", pane, Button.class);
        Button browseCalib = ItemSearcher.getItemById("addflight.browsecalib", pane, Button.class);

        TreeView<String> flightDirs = ItemSearcher.getGenericControlById("addflight.flightdirs", pane, TreeView.class, String.class);
        TreeView<String> calibDirs = ItemSearcher.getGenericControlById("addflight.calibdirs", pane, TreeView.class, String.class);

        TextArea notes =  ItemSearcher.getItemById("addflight.notes", pane, TextArea.class);

        selectUAV.getItems().addAll(Stream.of(UAV.values()).map(UAV::getName).toList());

        selectUAV.setOnAction(_ -> {
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

        selectSensor.setOnAction(_ -> {
            clearCheckComboBoxes(selectImageTypes);

            List<ImageType> imageTypes = switch (Sensor.fromName(selectSensor.getValue())){
                case FIXEDM2 -> List.of(ImageType.RGB, ImageType.PANORAMA);
                case FIXEDM3M -> List.of(ImageType.RGB, ImageType.MULTISPECTRAL);
                case FIXEDM3T, FIXEDM4T, H20T -> List.of(ImageType.RGB, ImageType.IR);
                case FIXEDMPHANTOM -> List.of(ImageType.RGB);
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

        selectImageTypes.getCheckModel().getCheckedIndices().addListener((ListChangeListener<Integer>) change -> {
            this.imageTypes = selectImageTypes.getCheckModel().getCheckedItems()
                    .stream()
                    .map(ImageType::valueOf)
                    .toList();
        });

        browse.setOnAction(_ -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Choose an image directory");
            File path = directoryChooser.showDialog(display.rootControl.getScene().getWindow());

            flightDirs.setRoot(new TreeItem<>(path.getParent()));
            flightDirs.getRoot().getChildren().add(new TreeItem<>(path.getName()));

            this.baseDirectory = path;
        });

        browseCalib.setOnAction(_ -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Choose an calibration directory");
            File path = directoryChooser.showDialog(display.rootControl.getScene().getWindow());

            calibDirs.setRoot(new TreeItem<>(path.getParent()));
            calibDirs.getRoot().getChildren().add(new TreeItem<>(path.getName()));

            this.baseDirectory = path;
        });

        datePicker.setOnAction(_ -> {
            this.date = Date.from(Instant.parse(datePicker.getEditor().getCharacters()));
        });

        location.textProperty().addListener(_ -> {this.location = location.getText();});
        aoi.textProperty().addListener(_ -> {this.aoi = aoi.getText();});
        pilot.textProperty().addListener(_ -> {this.pilot = pilot.getText();});
        coPilot.textProperty().addListener(_ -> {this.coPilot = coPilot.getText();});
        notes.textProperty().addListener(_ -> {this.notes = notes.textProperty().get();});

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

    public boolean validate(){
        return true;
    }

    @Override
    public String jsonCallback(ButtonType buttonType) {
        if (buttonType == ButtonType.FINISH) {
            return Flight.toJson(new Flight(this.date, this.location, this.aoi, this.pilot, this.coPilot,
                    this.uav, this.sensor, this.imageTypes, this.baseDirectory, this.notes));
        }else{
            return null;
        }
    }


}
