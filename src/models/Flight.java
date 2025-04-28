package models;

import com.google.gson.Gson;
import enums.ErrorType;
import enums.ImageType;
import enums.Sensor;
import enums.UAV;
import exception.UMASException;
import loader.ProjectCache;
import utils.ImageUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

public class Flight implements Serializable {

    @Serial
    private static final long serialVersionUID = 6529685098267757691L;

    private String date;
    private String location;
    private String aoi;
    private String pilot;
    private String coPilot;
    private UAV uav;
    private Sensor sensor;
    private List<ImageType> imageTypes;
    private String baseDirectory;
    private String flightDirectory;
    private List<String> originFlightDirs;
    private List<String> originCalibDirs;
    private String notes;

    private HashMap<ImageType, Integer> numberOfImages;

    public Flight(String date, String location, String aoi, String pilot, String coPilot, UAV uav, Sensor sensor, List<ImageType> imageTypes, String baseDirectory, List<String> originFlightDirs, List<String> originCalibDirs, String notes) {
        this.date = date;
        this.location = location;
        this.aoi = aoi;
        this.pilot = pilot;
        this.coPilot = coPilot;
        this.uav = uav;
        this.sensor = sensor;
        this.imageTypes = imageTypes;
        this.baseDirectory = baseDirectory;
        this.originFlightDirs = originFlightDirs;
        this.originCalibDirs = originCalibDirs;
        this.notes = notes;

        this.flightDirectory = getFlightDirectory();

        boolean created = createFolders();
        if (created) {
            try {
                copyFiles();
            } catch(Exception e) {
                UMASException.throwWindow(ErrorType.USER, "Could not copy files.");
            }

            /*try {
                saveToProject();
            }catch (Exception e){
                UMASException.throwWindow(ErrorType.USER, "Could not automatically save the flight. Please save it manually now!");
            }*/

        }else{
            UMASException.throwWindow(ErrorType.USER, "Could not create the folder structure 0_RGB (and so on...). " +
                    "Please create it manually!");
        }

    }

    public String getFlightDirectory(){
        DateTimeFormatter dtf_old = DateTimeFormatter.ofPattern("M/d/yyyy");
        DateTimeFormatter dtf_new = DateTimeFormatter.ofPattern("ddMMyyyy");

        return Paths.get(baseDirectory, "0_Flights", dtf_new.format(dtf_old.parse(date)) + "_" + this.location +  "_" + this.aoi + "_" + this.uav + this.sensor).toFile().getAbsolutePath();
    }

    private boolean createFolders(){
        File folder = new File(flightDirectory);
        boolean success = folder.exists() || folder.mkdirs();

        if(success){
            success = createFolderStructure("0_Images") &&
                        createFolderStructure("1_Agisoft") &&
                        createFolderStructure("2_Reports") &&
                        createFolderStructure("3_FlightFiles") &&
                        createFolderStructure("4_RawOutput");

                if(success){
                    for (ImageType imageType : imageTypes) {
                        if(imageType == ImageType.RGB) createImageTypeFolders("0_RGB");
                        if(imageType == ImageType.MULTISPECTRAL) {
                            createImageTypeFolders("1_MS");
                            createImageTypeFolders("2_CALIB");
                        }
                    }
                }

            return true;
        }else{
            UMASException.throwWindow(ErrorType.USER, "Could not create the flight folder \"" + flightDirectory + "\". Please create it manually!");
            return false;
        }
    }

    private void copyFiles() throws IOException {
        switch (uav){
            case MAVICM3M -> UAV.copyM3M(this.imageTypes, this.flightDirectory, this.originFlightDirs, this.originCalibDirs);
        }
    }

    private void saveToProject() throws IOException {
        ProjectCache.currentlyOpenedProject.addFlight(this);
        ProjectCache.currentlyOpenedProject.save();
    }

    private HashMap<ImageType, Integer> countImages(List<ImageType> imageTypes) {
        switch (uav){
            case MAVICM3M -> new HashMap<>();
        }

        return null;
    }

    public boolean createFolderStructure(String name){
        File rgbFolder = Paths.get(new File(flightDirectory).getAbsolutePath(), name).toFile();
        if(!rgbFolder.exists()){
            boolean success = rgbFolder.mkdir();
            if(!success){
                UMASException.throwWindow(
                        ErrorType.USER,
                        "Could not create folder \"" + name + "\"." +
                                " Please create in manually or make sure permissions are set for UMAS.");
                return false;
            }
        }
        return true;
    }

    public boolean createImageTypeFolders(String name){
        File rgbFolder = Paths.get(new File(flightDirectory).getAbsolutePath(), "0_Images", name).toFile();
        if(!rgbFolder.exists()){
            boolean success = rgbFolder.mkdir();
            if(!success){
                UMASException.throwWindow(
                        ErrorType.USER,
                        "Could not create folder \"" + name + "\"." +
                                " Please create in manually or make sure permissions are set for UMAS.");
                return false;
            }
        }
        return true;
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
