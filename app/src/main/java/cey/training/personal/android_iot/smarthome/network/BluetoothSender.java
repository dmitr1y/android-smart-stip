package cey.training.personal.android_iot.smarthome.network;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;

import cey.training.personal.android_iot.smarthome.SmartHome;
import cey.training.personal.android_iot.smarthome.SmartHomeState;

/**
 * Created by ceyler on 13.12.2016.
 *
 */

/**
 * Service for sending messages via Bluetooth.
 *
 * @author dmitriy
 * @version 1
 */
public class BluetoothSender extends AsyncTask<String, Void, Void> {

    private OutputStream outStream;
    private SmartHome parent;

    private SmartHomeState state;

    //--------------------------Constructors--------------------------------
    /**
     * Default constructor
     * @param parent parent for service
     * @param streamToWrite steam for writing
     */
    public BluetoothSender(@NonNull SmartHome parent,
                           @NonNull OutputStream streamToWrite) {
        this.outStream = streamToWrite;
        this.parent = parent;
    }
    //--------------------------End of constructors-------------------------


    //--------------------------AsyncTask implementation--------------------
    /**
     * Thread that executed in background.
     * @param messages input params from constructor
     * @return null
     */
    @Override
    public Void doInBackground(String ...messages){
        if(messages.length == 1) {
            setState(SmartHomeState.IN_PROGRESS);
            String message = messages[0];
            byte[] msgBuffer = message.getBytes();
            log("Send data: " + message);
            try {
                outStream.write(msgBuffer);
                log("Data sent");
                setState(SmartHomeState.CONNECTED);
                outStream.flush();
            } catch (IOException ex) {
                log("Fatal Error\n" +
                        "Can't send data");
                setState(SmartHomeState.BLUETOOTH_OFF);
            }
        }
        return null;
    }

    /**
     * Method fir publishing progress.
     * @param states states array for parent.
     */
    @Override
    public void onProgressUpdate(Void ... states){
        changeParentState();
    }
    //--------------------------End of AsyncTask implementation-------------

    /**
     * Method for establishing state of progress.
     * @param state State for establishing.
     */
    public void setState(SmartHomeState state){
        this.state = state;
        publishProgress();
    }

    //--------------------------wrappers for parent method------------------
    /**
     * Method fot logging.
     * @param msg message for logging.
     */
    private void log(String msg) {
        parent.log("Sender " + msg);
    }

    /**
     * Method for establishing parent state of progress.
     */
    private void changeParentState() {
        parent.setState(state);
    }
    //--------------------------end of wrappers for parent method------------
}
