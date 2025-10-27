package wue.eorc.umas.models;

import com.google.gson.Gson;
import wue.eorc.umas.enums.*;
import wue.eorc.umas.exception.UMASException;
import wue.eorc.umas.utils.system.DirectoryUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
    private String startTime = ""; // format HH:mm
    private String endTime = "";
    private final FlightParameters flightParameters;
    private final UAV uav;
    private final Sensor sensor;
    private final Map<ImageType, String> imageTypes;
    private final ProcessingChain processingChain;
    private final String baseDirectory;
    private final List<String> originFlightDirs;
    private final List<String> originCalibDirs;
    private final String notes;

    public Flight(String date, String location, String aoi, String pilot, String coPilot,
                  FlightParameters flightParameters, UAV uav, Sensor sensor,
                  Map<ImageType, String> imageTypes, ProcessingChain processingChain, String baseDirectory,
                  List<String> originFlightDirs, List<String> originCalibDirs, String notes) throws UMASException {

        this.date = date;
        this.location = location;
        this.aoi = aoi;
        this.pilot = pilot;
        this.coPilot = coPilot;
        this.flightParameters = flightParameters;
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
                    "Please restart the application. Do not created it manually.");
        }else{
            DirectoryUtils.fillImageTypeDirs(this);
        }

    }

    public String getFlightDirectory() {
        DateTimeFormatter dtf_old = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter dtf_new = DateTimeFormatter.ofPattern("ddMMyyyy");

        return Paths.get(baseDirectory, "0_Flights", dtf_new.format(dtf_old.parse(date)) + "_" + this.location + "_" + this.aoi + "_" + this.uav + this.sensor).toFile().getAbsolutePath();
    }

    public String getProjectFileNameAgisoft(){
        DateTimeFormatter dtf_old = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter dtf_new = DateTimeFormatter.ofPattern("ddMMyyyy");

        return dtf_new.format(dtf_old.parse(date)) + "_" + this.location + "_" + this.aoi + "_" + this.uav + this.sensor + "_Agisoft.psx";
    }

    public String getProjectFileNameTerra(){
        DateTimeFormatter dtf_old = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter dtf_new = DateTimeFormatter.ofPattern("ddMMyyyy");

        return dtf_new.format(dtf_old.parse(date)) + "_" + this.location + "_" + this.aoi + "_" + this.uav + this.sensor + "_Terra.symlink";
    }

    public String getExportDemName(){
        DateTimeFormatter dtf_old = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter dtf_new = DateTimeFormatter.ofPattern("ddMMyyyy");

        return dtf_new.format(dtf_old.parse(date)) + "_" + this.location + "_" + this.aoi + "_" + this.uav + this.sensor + "_DSM.tif";
    }

    public String getExportOrthomosaicName(){
        DateTimeFormatter dtf_old = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter dtf_new = DateTimeFormatter.ofPattern("ddMMyyyy");

        return dtf_new.format(dtf_old.parse(date)) + "_" + this.location + "_" + this.aoi + "_" + this.uav + this.sensor + "_OM.tif";
    }

    public String getGenerateReportName(){
        DateTimeFormatter dtf_old = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter dtf_new = DateTimeFormatter.ofPattern("ddMMyyyy");

        return dtf_new.format(dtf_old.parse(date)) + "_" + this.location + "_" + this.aoi + "_" + this.uav + this.sensor + ".pdf";
    }

    private HashMap<ImageType, Integer> countImages(List<ImageType> imageTypes) {
        switch (uav) {
            case MAVICM3M -> new HashMap<>();
        }

        return null;
    }

    public LocalDateTime[] computeFlightStartAndEnd(){
        File[] files = new File(this.originFlightDirs.get(0)).listFiles();

        if(files != null && files.length > 0) {
            Arrays.sort(files, (f1, f2) -> {
                try {
                    Path path1 = f1.toPath();
                    Path path2 = f2.toPath();
                    BasicFileAttributes attr1 = Files.readAttributes(path1, BasicFileAttributes.class);
                    BasicFileAttributes attr2 = Files.readAttributes(path2, BasicFileAttributes.class);
                    return attr1.creationTime().compareTo(attr2.creationTime());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            try {
                FileTime startTime = Files.readAttributes(files[0].toPath(), BasicFileAttributes.class).creationTime();
                FileTime endTime = Files.readAttributes(files[files.length - 1].toPath(), BasicFileAttributes.class).creationTime();

                // # TODO check for time zone accordingly!
                LocalDateTime localStartTime = LocalDateTime.ofInstant(startTime.toInstant(), ZoneId.systemDefault());
                LocalDateTime localEndTime = LocalDateTime.ofInstant(endTime.toInstant(), ZoneId.systemDefault());

                return new LocalDateTime[]{localStartTime, localEndTime};
            } catch (IOException e) {
                return null;
            }
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

    public FlightParameters getFlightParameters(){
        return flightParameters;
    }

    public UAV getUav() {
        return uav;
    }

    public Sensor getSensor() {
        return sensor;
    }

    public Map<ImageType, String> getImageTypes() {
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

    public String getStartTime(){
        return startTime;
    }

    public String getEndTime(){
        return endTime;
    }

    public void setStartTime(String startTime){
        this.startTime = startTime;
    }
    public void setEndTime(String endTime){
        this.endTime = endTime;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Flight flight)) return false;
        return Objects.equals(date, flight.date) &&
                Objects.equals(location, flight.location) &&
                Objects.equals(aoi, flight.aoi) &&
                Objects.equals(pilot, flight.pilot) &&
                Objects.equals(coPilot, flight.coPilot) &&
                Objects.equals(startTime, flight.startTime) &&
                Objects.equals(endTime, flight.endTime) &&
                uav == flight.uav &&
                sensor == flight.sensor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, location, aoi, pilot, coPilot, startTime, endTime, flightParameters, uav, sensor, imageTypes, processingChain, baseDirectory, originFlightDirs, originCalibDirs, notes);
    }

}
