package cey.training.personal.android_iot;

/**
 * Created by ceyler on 11.12.2016.
 *
 */

enum LedState {
    OFF("0"),
    ON("1");

    private String value;

    LedState(String value){
        this.value = value;
    }

    @Override
    public String toString(){
        return value;
    }
}
