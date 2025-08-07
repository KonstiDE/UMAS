package wue.eorc.umas.models;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class FlightParameters implements Serializable {

    @Serial
    private static final long serialVersionUID = 6529685098265757691L;

    private int height;
    private int frontOverlap;
    private int sideOverlap;
    private double speed;
    private List<String[]> coordinates;
    private List<String[]> waypoints;

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

    public List<String[]> getWaypoints() {
        return waypoints;
    }

    public void setWaypoints(List<String[]> waypoints) {
        this.waypoints = waypoints;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof FlightParameters that)) return false;
        return height == that.height && frontOverlap == that.frontOverlap && sideOverlap == that.sideOverlap && Double.compare(speed, that.speed) == 0 && Objects.equals(coordinates, that.coordinates);
    }

    @Override
    public int hashCode() {
        return Objects.hash(height, frontOverlap, sideOverlap, speed, coordinates);
    }
}
