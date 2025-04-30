package wue.eorc.umas.models;

import com.google.gson.Gson;
import wue.eorc.umas.enums.*;
import wue.eorc.umas.exception.UMASException;
import wue.eorc.umas.utils.DirectoryUtils;

import java.io.*;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

public class Flight implements Serializable {

    @Serial
    private static final long serialVersionUID = 6529685098267757691L;

    private final String date;
    private final String location;
    private final String aoi;
    private final String pilot;
    private final String coPilot;
    private final String height;
    private final UAV uav;
    private final Sensor sensor;
    private final List<ImageType> imageTypes;
    private final ProcessingChain processingChain;
    private final String baseDirectory;
    private final List<String> originFlightDirs;
    private final List<String> originCalibDirs;
    private final String notes;

    public Flight(String date, String location, String aoi, String pilot, String coPilot, String height, UAV uav, Sensor sensor, List<ImageType> imageTypes, ProcessingChain processingChain, String baseDirectory, List<String> originFlightDirs, List<String> originCalibDirs, String notes) throws UMASException {
        this.date = date;
        this.location = location;
        this.aoi = aoi;
        this.pilot = pilot;
        this.coPilot = coPilot;
        this.height = height;
        this.uav = uav;
        this.sensor = sensor;
        this.imageTypes = imageTypes;
        this.processingChain = processingChain;
        this.baseDirectory = baseDirectory;
        this.originFlightDirs = originFlightDirs;
        this.originCalibDirs = originCalibDirs;
        this.notes = notes;

        boolean created = DirectoryUtils.createFolderStructure(this);
        if (!created){
            UMASException.throwWindow(ErrorType.USER, "Could not create the folder structure 0_RGB (and so on...). " +
                    "Please create it manually!");
        }

    }

    public String getFlightDirectory() {
        DateTimeFormatter dtf_old = DateTimeFormatter.ofPattern("M/d/yyyy");
        DateTimeFormatter dtf_new = DateTimeFormatter.ofPattern("ddMMyyyy");

        return Paths.get(baseDirectory, "0_Flights", dtf_new.format(dtf_old.parse(date)) + "_" + this.location + "_" + this.aoi + "_" + this.uav + this.sensor).toFile().getAbsolutePath();
    }

    private HashMap<ImageType, Integer> countImages(List<ImageType> imageTypes) {
        switch (uav) {
            case MAVICM3M -> new HashMap<>();
        }

        return null;
    }

    public static String toJson(Flight flight) {
        Gson gson = new Gson();
        return gson.toJson(flight);
    }

    public static Flight factoryFromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, Flight.class);
    }

    public String getDate() {
        return date;
    }

    public String getLocation() {
        return location;
    }

    public String getAoi() {
        return aoi;
    }

    public String getPilot() {
        return pilot;
    }

    public String getCoPilot() {
        return coPilot;
    }

    public String getHeight(){
        return height;
    }

    public UAV getUav() {
        return uav;
    }

    public Sensor getSensor() {
        return sensor;
    }

    public List<ImageType> getImageTypes() {
        return imageTypes;
    }

    public String getBaseDirectory() {
        return baseDirectory;
    }

    public List<String> getOriginFlightDirs() {
        return originFlightDirs;
    }

    public List<String> getOriginCalibDirs() {
        return originCalibDirs;
    }

    public String getNotes() {
        return notes;
    }

    public ProcessingChain getProcessingChain() {
        return processingChain;
    }
}
