package wue.eorc.umas.utils.threed;

/**
 * @param altitudeMeters Must be ellipsoidal height
 */
public record WGS84Point(double latitudeDeg, double longitudeDeg, double altitudeMeters) {
}
