package wue.eorc.umas.models;

import javafx.scene.shape.Polygon;
import javafx.util.Pair;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class FlightParameters implements Serializable {

    @Serial
    private static final long serialVersionUID = 6529685098265757691L;

    private int height;
    private int frontOverlap;
    private int sideOverlap;
    private double speed;
    private List<String[]> coordinates;

    public FlightParameters(int height, int frontOverlap, int sideOverlap, double speed, List<String[]> coordinates) {
        this.height = height;
        this.frontOverlap = frontOverlap;
        this.sideOverlap = sideOverlap;
        this.speed = speed;
        this.coordinates = coordinates;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getFrontOverlap() {
        return frontOverlap;
    }

    public void setFrontOverlap(int frontOverlap) {
        this.frontOverlap = frontOverlap;
    }

    public int getSideOverlap() {
        return sideOverlap;
    }

    public void setSideOverlap(int sideOverlap) {
        this.sideOverlap = sideOverlap;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public List<String[]> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<String[]> coordinates) {
        this.coordinates = coordinates;
    }

}
