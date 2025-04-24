package models;

import com.google.gson.Gson;
import enums.Sensor;
import enums.UAV;
import exception.UMASException;

import java.io.*;
import java.util.Date;
import java.util.HashMap;

public class Flight implements Serializable {

    @Serial
    private static final long serialVersionUID = 6529685098267757691L;

    private Date date;
    private String location;
    private String aoi;
    private String pilot;
    private String coPilot;
    private UAV uav;
    private HashMap<String, ImageCollection> imagePaths;

    public Flight(Date date, String location, String aoi, String pilot, String coPilot, UAV uav, HashMap<String, ImageCollection> imagePaths) {
        this.date = date;
        this.location = location;
        this.aoi = aoi;
        this.pilot = pilot;
        this.coPilot = coPilot;
        this.uav = uav;
        this.imagePaths = imagePaths;
    }

    public void save(String path, String filename) throws IOException {
        FileOutputStream fout = new FileOutputStream(filename.concat(".umasflight"));
        ObjectOutputStream oos = new ObjectOutputStream(fout);

        oos.writeObject(this);
        oos.flush();
        oos.close();
    }

    public static String toJson(Flight flight) {
        Gson gson = new Gson();
        return gson.toJson(flight);
    }

    public static Flight factoryFromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, Flight.class);
    }

}
