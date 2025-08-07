package wue.eorc.umas.enums;

import java.util.List;

public enum Sensor {

    FIXEDM2("FixedM3", List.of(ImageType.RGB)),
    FIXEDM3M("FixedM3M", List.of(ImageType.RGB, ImageType.MULTISPECTRAL)),
    FIXEDM3T("FixedM3T", List.of(ImageType.RGB, ImageType.IR)),
    FIXEDM4T("FixedM4T", List.of(ImageType.RGB, ImageType.IR)),
    FIXEDMPHANTOM("FixedPhantom", List.of(ImageType.RGB, ImageType.IR)),
    H20T("H20T", List.of(ImageType.RGB, ImageType.IR)),
    MXDUAL("MXDUAL", List.of(ImageType.MULTISPECTRAL)),
    D2M("D2M", List.of(ImageType.RGB)),
    ALTUM("ALTUM", List.of(ImageType.RGB, ImageType.MULTISPECTRAL, ImageType.IR)),
    ALTUMPT("ALTUMPT", List.of(ImageType.MULTISPECTRAL)),
    L1("L1", List.of(ImageType.LIDAR)),
    NIKONRGB("NikonRGB", List.of(ImageType.RGB)),
    NANOHP("NanoHP", List.of(ImageType.HYPERSPECTRAL, ImageType.LIDAR)),
    LIAIRV("LiAirV", List.of(ImageType.HYPERSPECTRAL, ImageType.LIDAR)),
    Q2("Qube240", List.of(ImageType.LIDAR));

    private final String name;
    private final List<ImageType> imageTypes;

    Sensor(String name, List<ImageType> imageTypes) {
        this.name = name;
        this.imageTypes = imageTypes;
    }

    public String getName(){
        return name;
    }

    public List<ImageType> getImageTypes() {
        return imageTypes;
    }

    public static Sensor fromName(String name){
        for(Sensor sensor : Sensor.values()){
            if(sensor.name.equals(name)){
                return sensor;
            }
        }
        throw new IllegalArgumentException("Invalid Sensor name: " + name);
    }

}
