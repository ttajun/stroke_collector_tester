package ksb.strokesos.tester.bean;

public enum SensorType {
    ECG("ecg"),
    ACCELEROMETER("accelerometer"),
    PEDOMETER("pedometer"),
    DRIVE("seat")
    ;

    private String name;

    SensorType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
