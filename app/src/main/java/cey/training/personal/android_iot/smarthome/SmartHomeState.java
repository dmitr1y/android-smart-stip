package cey.training.personal.android_iot.smarthome;

/**
 * Created by ceyler on 12.12.2016.
 *
 */

public enum SmartHomeState {
    NO_BLUETOOTH("No bluetooth"),
    BLUETOOTH_OFF("Bluetooth is off"),
    IN_PROGRESS("In progress"),
    FINISHED("Finished"),
    CONNECTED("Connected"),
    MODES("Modes");

    private String value;

    SmartHomeState(String value){
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString(){
        return this.getValue();
    }
}
