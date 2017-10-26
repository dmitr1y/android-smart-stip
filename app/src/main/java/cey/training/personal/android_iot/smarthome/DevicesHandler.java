package cey.training.personal.android_iot.smarthome;

import android.content.Intent;

/**
 * Created by ceyler on 12.12.2016.
 *
 */

public interface DevicesHandler {
    void handleSmartHomeState(SmartHomeState state);
    void addDevice(String[] deviceInfo);
    void chooseDevice(String[] deviceInfo);
    void log(String msg);
    void log(String msg, boolean isError);
    void onActivityResult(int requestCode, int resultCode, Intent data);
    void handleAnswer(String answer);
}
