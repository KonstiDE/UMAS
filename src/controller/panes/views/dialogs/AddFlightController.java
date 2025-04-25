package controller.panes.views.dialogs;

import controller.panes.mains.DisplayController;
import enums.ImageType;
import enums.Sensor;
import enums.UAV;
import exception.UMASException;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import models.ImageCollection;
import org.controlsfx.control.CheckComboBox;
import utils.ItemSearcher;

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
    private HashMap<String, ImageCollection> imagePaths;

    @Override
    public void init(Pane pane, DisplayController display) throws UMASException {
        DatePicker datePicker = ItemSearcher.getItemById("addflight.date", pane, DatePicker.class);
        TextField location = ItemSearcher.getItemById("addflight.location", pane, TextField.class);
        TextField aoi = ItemSearcher.getItemById("addflight.aoi", pane, TextField.class);
        TextField pilot = ItemSearcher.getItemById("addflight.pilot", pane, TextField.class);
        TextField copilot = ItemSearcher.getItemById("addflight.copilot", pane, TextField.class);

        ComboBox<String> selectUAV = ItemSearcher.getGenericControlById("addflight.uav", pane, ComboBox.class, String.class);
        ComboBox<String> selectSensor = ItemSearcher.getGenericControlById("addflight.sensor", pane, ComboBox.class, String.class);
        CheckComboBox<String> selectImageTypes = ItemSearcher.getGenericControlById("addflight.imagetypes", pane, CheckComboBox.class, String.class);

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

        });

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

    @Override
    public String jsonCallback(ButtonType buttonType) {
        if (buttonType == ButtonType.FINISH) {
            return "";
        }else{
            return null;
        }
    }


}
