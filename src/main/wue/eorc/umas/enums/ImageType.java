package wue.eorc.umas.enums;

public enum ImageType {

    RGB("RGB"),
    IR("Thermal"),
    HYPERSPECTRAL("Hyperspectral"),
    LIDAR("Lidar"),
    MULTISPECTRAL("Multispectral"),

    CALIBRATION("Calibration");

    private final String name;

    ImageType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
