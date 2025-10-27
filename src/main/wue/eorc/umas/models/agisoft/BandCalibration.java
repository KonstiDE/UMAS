package wue.eorc.umas.models.agisoft;

public class BandCalibration {

    private int id;
    private String band;
    private double reflectance;

    public BandCalibration(int id, String band, double reflectance) {
        this.id = id;
        this.band = band;
        this.reflectance = reflectance;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBand() {
        return band;
    }

    public void setBand(String band) {
        this.band = band;
    }

    public double getReflectance() {
        return reflectance;
    }

    public void setReflectance(double reflectance) {
        this.reflectance = reflectance;
    }

}
