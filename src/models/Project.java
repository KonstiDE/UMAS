package models;

import enums.ErrorType;
import exception.UMASException;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class Project implements Serializable {

    @Serial
    private static final long serialVersionUID = 6529685098267757690L;

    private String name;
    private String pilot;
    private String location;
    private File directory;
    private List<Flight> flights;

    public Project(String name, String pilot, String location, File directory, List<Flight> flights) {
        this.name = name;
        this.pilot = pilot;
        this.location = location;
        this.directory = directory;
        this.flights = flights;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPilot() {
        return pilot;
    }

    public void setPilot(String pilot) {
        this.pilot = pilot;
    }

    public File getDirectory() {
        return directory;
    }

    public void setDirectory(File directory) {
        this.directory = directory;
    }

    public List<Flight> getFlights() {
        return flights;
    }

    public void addFlight(Flight... flights) {
        this.flights.addAll(Arrays.stream(flights).toList());
    }

    public File getFile(){
        return Paths.get(directory.getAbsolutePath(), name, name.concat(".umasproject")).toFile();
    }

    public void save() throws IOException {
        File projectPath = Paths.get(getDirectory().getAbsolutePath(), getName()).toFile();


        if(!projectPath.exists()) {
            boolean created = projectPath.mkdir();
            if(!created) {
                UMASException.throwWindow(ErrorType.USER, "Could not create project directory under \"" + projectPath.getAbsolutePath() + "\" please create it manually.");
            }

            createDirs(projectPath, "0_Flights", "1_Analysis", "2_Results", "3_Media");

        }

        Path path = Paths.get(projectPath.getAbsolutePath(), getName().concat(".umasproject")).toAbsolutePath();

        FileOutputStream fout = new FileOutputStream(path.toFile());
        ObjectOutputStream oos = new ObjectOutputStream(fout);

        oos.writeObject(this);
        oos.flush();
        oos.close();
    }

    private void createDirs(File projectPath, String... dirName){
        for(String dir : dirName){
            File subDir = Paths.get(projectPath.getAbsolutePath(), dir).toFile();

            if(!subDir.exists()){
                boolean created = subDir.mkdir();
                if(!created) {
                    UMASException.throwWindow(ErrorType.USER, "Could not create project directory \"" + dir + "\" under \"" + projectPath.getAbsolutePath() + "\" please create it manually.");
                }
            }
        }
    }

    public static Project read(String path) throws IOException, ClassNotFoundException {
        FileInputStream fis = new FileInputStream(path);
        ObjectInputStream ois = new ObjectInputStream(fis);

        return (Project) ois.readObject();
    }

}
