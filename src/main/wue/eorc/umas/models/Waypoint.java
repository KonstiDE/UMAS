package wue.eorc.umas.models;

public class Waypoint {

    double latitude;
    double longitude;
    double altitude;

    public Waypoint(double lat, double lon, double alt) {
        this.latitude = lat;
        this.longitude = lon;
        this.altitude = alt;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getAltitude() {
        return altitude;
    }

    @Override
    public String toString() {
        return String.format("Lat: %.6f, Lon: %.6f, Alt: %.2f", latitude, longitude, altitude);
    }

}
