package enums;

import java.util.List;

public enum UAV {

    MAVICM2("MavicM2", List.of(Sensor.FIXEDM2)),
    MAVICM3M("MavicM3M", List.of(Sensor.FIXEDM3M)),
    MAVICM3T("MavicM3T", List.of(Sensor.FIXEDM3T)),
    MAVICM4T("MavicM4T", List.of(Sensor.FIXEDM4T)),
    PHAMTOM("Phantom", List.of(Sensor.FIXEDMPHANTOM)),
    M300("M300", List.of(Sensor.ALTUM, Sensor.L1, Sensor.H20T, Sensor.MXDUAL)),
    M600("M600", List.of(Sensor.ALTUM, Sensor.MXDUAL, Sensor.NANOHP, Sensor.LIAIRV)),
    WINGTRA("Wingtra", List.of(Sensor.NIKONRGB, Sensor.ALTUM)),
    TRINITY("Trinity", List.of(Sensor.D2M, Sensor.ALTUMPT, Sensor.Q2));

    private final String name;
    private final List<Sensor> sensors;

    UAV(String name, List<Sensor> sensors){
        this.name = name;
        this.sensors = sensors;
    }

    public List<Sensor> getSensors(){
        return sensors;
    }

    public String getName() {
        return name;
    }

    public static UAV fromName(String name){
        for(UAV uav : UAV.values()){
            if(uav.name.equals(name)){
                return uav;
            }
        }
        throw new IllegalArgumentException("Invalid UAV name: " + name);
    }

}
