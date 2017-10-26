package cey.training.personal.android_iot.smarthome;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.InputStream;
import java.io.OutputStream;

import cey.training.personal.android_iot.smarthome.network.BluetoothConnector;
import cey.training.personal.android_iot.smarthome.network.BluetoothListener;
import cey.training.personal.android_iot.smarthome.network.BluetoothSender;
import cey.training.personal.android_iot.smarthome.network.BluetoothSetup;

/**
 * Created by ceyler on 12.12.2016.
 *
 */

public class SmartHome {

    private SmartHomeState state;

    private DevicesHandler listener;
    private Activity activity;
    private BluetoothSetup setup;
    private OutputStream outputStream;
    private ReportPDF report;

    //--------------------------Constructors--------------------------------
    public SmartHome(@NonNull Activity activity,
                     @Nullable DevicesHandler listener) {
        log("SmartHome constructor");
        setActivity(activity);
        setListener(listener);

        executeSetup();
    }
    //--------------------------End of constructors-------------------------

    //--------------------------getters and setters-------------------------
    public SmartHomeState getState() {
        return state;
    }

    public void setState(SmartHomeState state) {
        this.state = state;
        sendStateToListener();
    }

    public DevicesHandler getListener() {
        return listener;
    }

    public void setListener(DevicesHandler listener) {
        this.listener = listener;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    //--------------------------end of getters and setters------------------

    public void exit() {
        if (report != null)
            report.finishReport();
        setState(SmartHomeState.BLUETOOTH_OFF);
    }

    public void executeSetup(){
        setState(SmartHomeState.IN_PROGRESS);
        setup = new BluetoothSetup(this);
        setup.execute();
    }

    //inputStream and outputStream forming on BluetoothConnection and set from it
    public void setupBluetoothListener(InputStream inputStream) {
        new BluetoothListener(this, inputStream).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void setMessage(String message) {
        if (getState().equals(SmartHomeState.CONNECTED)) {
            new BluetoothSender(this, outputStream).execute(message);
        }
    }
    
    public void addDevice(String[] deviceInfo){
        if(listener != null)
            listener.addDevice(deviceInfo);
    }

    public void chooseDevice(int deviceNumber) {
        log("Chosen device number " + Integer.toString(deviceNumber));

        BluetoothDevice chosenDevice = setup.chooseDevice(deviceNumber);
        if (chosenDevice != null) {
            //if user choose device before we end searching
            if (!getState().equals(SmartHomeState.FINISHED)) {
                log("Discover ended MANUALLY");
                setup.cancelSearch();
            }

//            report = new ReportPDF(this);

            log("\nDevice:" + chosenDevice.getName() + "\n" +
                    "Address: " + chosenDevice.getAddress());

            setState(SmartHomeState.IN_PROGRESS);
            BluetoothConnector connection = new BluetoothConnector(this, chosenDevice);
            connection.execute();
            String deviceName = chosenDevice.getName();
            log("NAME: " + deviceName);
            String[] result =null;
            if (deviceName != null) {
                result=new String[3];
                result[0] = chosenDevice.getName();
                result[1] = "CONNECTED";//TODO check this (wtf?)
                result[2] = chosenDevice.getAddress();
                listener.chooseDevice(result);
            }
            else
                listener.chooseDevice(result);
        } else {
            log("Wrong device");
        }
    }

    public void handleAnswer(String message){
        listener.handleAnswer(AnswerParser.answerToString(message));
//        report.addToReport(message);
    }

    public void onActivityResult(int requestCode, int resultCode) {
        log("onActivityResult");
        setup.onActivityResult(requestCode, resultCode);
    }

    public void sendStateToListener() {
        log("sendState: " + getState().toString());
        if (listener != null)
            listener.handleSmartHomeState(getState());
    }

    public void log(String msg){
        if(listener != null)
            listener.log(msg);
    }

    public void log(String msg, boolean isError) {
        if (listener != null)
            listener.log("SH " + msg, isError);
    }
}
