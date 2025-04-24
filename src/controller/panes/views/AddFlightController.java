package controller.panes.views;

import com.google.gson.Gson;
import controller.panes.mains.DisplayController;
import enums.UAV;
import exception.UMASException;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Pane;
import models.Flight;
import models.ImageCollection;

import java.util.Date;
import java.util.HashMap;

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

    }

    @Override
    public String jsonCallback(ButtonType buttonType) {
        return Flight.toJson(new Flight(
                this.date,
                this.location,
                this.aoi,
                this.pilot,
                this.coPilot,
                this.uav,
                this.imagePaths
        ));
    }


}
