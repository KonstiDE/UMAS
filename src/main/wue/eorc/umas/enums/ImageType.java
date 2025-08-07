package wue.eorc.umas.enums;

public enum ImageType {

    RGB("RGB"),
    IR("IR"),
    HYPERSPECTRAL("Hyperspectral"),
    LIDAR("Lidar"),
    MULTISPECTRAL("Multispectral");

    private final String name;

    ImageType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
