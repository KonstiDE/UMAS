package models;

import com.google.gson.Gson;
import enums.ErrorType;
import enums.ImageType;
import enums.Sensor;
import enums.UAV;
import exception.UMASException;

import java.io.*;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Flight implements Serializable {

    @Serial
    private static final long serialVersionUID = 6529685098267757691L;

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

    private HashMap<ImageType, Integer> numberOfImages;

    public Flight(Date date, String location, String aoi, String pilot, String coPilot, UAV uav, Sensor sensor, List<ImageType> imageTypes, File baseDirectory, String notes) {
        this.date = date;
        this.location = location;
        this.aoi = aoi;
        this.pilot = pilot;
        this.coPilot = coPilot;
        this.uav = uav;
        this.sensor = sensor;
        this.imageTypes = imageTypes;
        this.baseDirectory = baseDirectory;
        this.notes = notes;

        createFolders();

    }

    private void createFolders(){
        for(ImageType imageType : imageTypes){
            if(imageType == ImageType.RGB) createImageFolders("0_RGB");
            if(imageType == ImageType.MULTISPECTRAL) createImageFolders("1_MS");
        }
    }

    private HashMap<ImageType, Integer> countImages(List<ImageType> imageTypes) {


        switch (uav){
            case MAVICM3M -> new HashMap<>();
        }

        return null;
    }

    public void createImageFolders(String name){
        File rgbFolder = Paths.get(baseDirectory.getAbsolutePath(), name).toFile();
        if(rgbFolder.exists()){
            boolean success = rgbFolder.mkdir();
            if(!success){
                UMASException.throwWindow(
                        ErrorType.USER,
                        "Could not create folder \"" + name + "\"." +
                                " Please create in manually or make sure permissions are set for UMAS.");
            }
        }
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
