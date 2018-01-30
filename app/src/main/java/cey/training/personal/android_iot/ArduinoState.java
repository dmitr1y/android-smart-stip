package cey.training.personal.android_iot;

/**
 * Created by ceyler on 11.12.2016.
 *
 */

enum ArduinoState {
    LED_MODE("0"),
    LED_BRIGHTNESS("1"),
    LED_COLOR("2"),
    SYS_VOL("3");


    private String value;

    ArduinoState(String value){
        this.value = value;
    }

    @Override
    public String toString(){
        return value;
    }
}
