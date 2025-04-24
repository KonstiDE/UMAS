package enums;

import java.util.List;

public enum UAV {

    MAVICM2(List.of(Sensor.FIXEDM2)),
    MAVICM3M(List.of(Sensor.FIXEDM3M)),
    MAVICM3T(List.of(Sensor.FIXEDM3T)),
    MAVICM4T(List.of(Sensor.FIXEDM4T)),
    PHAMTOM(List.of(Sensor.FIXEDMPHANTOM)),
    M300(List.of(Sensor.ALTUM, Sensor.L1, Sensor.H20T, Sensor.MXDUAL)),
    M600(List.of(Sensor.ALTUM, Sensor.MXDUAL, Sensor.NANOHP, Sensor.LIAIRV)),
    WINGTRA(List.of(Sensor.NIKONRGB, Sensor.ALTUM)),
    TRINITY(List.of(Sensor.D2M, Sensor.ALTUMPT, Sensor.Q2));

    private final List<Sensor> sensors;

    UAV(List<Sensor> sensors){
        this.sensors = sensors;
    }

    public List<Sensor> getSensors(){
        return sensors;
    }

}
