package wue.eorc.umas.utils.threed;

import java.util.List;

// This bad bois AI generated, I would probably spend one entire day with it
public class WGS84Transformer {

    private static final double A = 6378137.0;
    private static final double F = 1.0 / 298.257223563;
    private static final double E_SQ = F * (2.0 - F);

    private final double lat_rad0, lon_rad0;
    private final double x0, y0, z0;

    /**
     * Initializes the converter by calculating the centroid of the WGS84 data
     * and setting it as the local (0, 0, 0) origin.
     * @param pointCloud A list of WGS84Point objects.
     */
    public WGS84Transformer(List<WGS84Point> pointCloud) {
        if (pointCloud == null || pointCloud.isEmpty()) {
            throw new IllegalArgumentException("Point cloud cannot be empty.");
        }

        // 1. Calculate the Centroid (Average Lat/Lon/Alt)
        double sumLat = 0, sumLon = 0, sumAlt = 0;
        for (WGS84Point p : pointCloud) {
            sumLat += p.latitudeDeg();
            sumLon += p.longitudeDeg();
            sumAlt += p.altitudeMeters();
        }

        double refLatDeg = sumLat / pointCloud.size();
        double refLonDeg = sumLon / pointCloud.size();
        double refAlt = sumAlt / pointCloud.size();

        this.lat_rad0 = Math.toRadians(refLatDeg);
        this.lon_rad0 = Math.toRadians(refLonDeg);

        double[] ecef0 = geodeticToEcef(refLatDeg, refLonDeg, refAlt);
        this.x0 = ecef0[0];
        this.y0 = ecef0[1];
        this.z0 = ecef0[2];
    }

    /**
     * Transforms a single WGS84 point to a Local Cartesian (ENU) point.
     * The origin of the local system is the centroid defined in the constructor.
     * @param wgsPoint The WGS84Point to transform.
     * @return LocalPoint {X_East, Y_North, Z_Up}
     */
    public LocalPoint transformPoint(WGS84Point wgsPoint) {
        double[] ecef = geodeticToEcef(wgsPoint.latitudeDeg(), wgsPoint.longitudeDeg(), wgsPoint.altitudeMeters());

        return ecefToEnu(ecef[0], ecef[1], ecef[2]);
    }

    private double[] geodeticToEcef(double latDeg, double lonDeg, double h) {
        double lat = Math.toRadians(latDeg);
        double lon = Math.toRadians(lonDeg);

        double sin_lat = Math.sin(lat);
        double cos_lat = Math.cos(lat);
        double sin_lon = Math.sin(lon);
        double cos_lon = Math.cos(lon);

        double N = A / Math.sqrt(1.0 - E_SQ * sin_lat * sin_lat); // Prime vertical radius of curvature

        double x = (N + h) * cos_lat * cos_lon;
        double y = (N + h) * cos_lat * sin_lon;
        double z = (N * (1.0 - E_SQ) + h) * sin_lat;

        return new double[]{x, y, z};
    }

    private LocalPoint ecefToEnu(double x, double y, double z) {

        double dx = x - x0;
        double dy = y - y0;
        double dz = z - z0;

        double sin_lat0 = Math.sin(lat_rad0);
        double cos_lat0 = Math.cos(lat_rad0);
        double sin_lon0 = Math.sin(lon_rad0);
        double cos_lon0 = Math.cos(lon_rad0);

        double east = -sin_lon0 * dx + cos_lon0 * dy;

        double north = -cos_lon0 * sin_lat0 * dx
                -sin_lat0 * sin_lon0 * dy
                +cos_lat0 * dz;

        double up = cos_lat0 * cos_lon0 * dx
                + cos_lat0 * sin_lon0 * dy
                + sin_lat0 * dz;

        return new LocalPoint(east, north, up);
    }
}
