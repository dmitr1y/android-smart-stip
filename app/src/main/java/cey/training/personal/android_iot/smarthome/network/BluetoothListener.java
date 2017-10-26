package cey.training.personal.android_iot.smarthome.network;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import cey.training.personal.android_iot.smarthome.SmartHome;
import cey.training.personal.android_iot.smarthome.SmartHomeState;

/**
 * Created by ceyler on 14.12.2016.
 *
 */

/**
 * Service for listening input stream of BLuetooth.
 *
 * @author dmitriy
 * @version 1
 */

public class BluetoothListener extends AsyncTask<Void, String, Void> {
    private InputStream inStream;
    private SmartHome parent;
    private SmartHomeState state;

    //--------------------------Constructors--------------------------------

    /**
     * Default constructor
     * @param parent parent for service
     * @param streamToListen input steam for listening
     */
    public BluetoothListener(@NonNull SmartHome parent,
                             @NonNull InputStream streamToListen) {
        this.parent = parent;
        this.inStream = streamToListen;
    }
    //--------------------------End of constructors-------------------------

    //--------------------------AsynkTask methods---------------------------

    /**
     * Thread that executed in background.
     * @param params input params from constructor
     * @return null
     */
    @Override
    public Void doInBackground(Void... params) {
        final int BUFFER_SIZE = 1024;
        byte[] buffer = new byte[BUFFER_SIZE];
        int bytes = 0;
        StringBuilder answerBuilder = new StringBuilder();

        while (true) {
            if (parent.getState().equals(SmartHomeState.CONNECTED)) {
                try {
                    int bytesAvailable=inStream.available();
                    if (bytesAvailable>0) {
                        bytes=inStream.read(buffer);
                        answerBuilder.append(new String(buffer, 0, bytes));
                        log("read " + bytes + " bytes");
                        log("received string from arduino: " + answerBuilder.toString());
                        publishProgress(answerBuilder.toString());
                        log("cleaning buffer");
                        answerBuilder.delete(0, answerBuilder.length());
                    }
                } catch (IOException ex) {
                    log("Error while reading:\n" + ex.getLocalizedMessage());
                    ex.printStackTrace();
                    setState(SmartHomeState.BLUETOOTH_OFF);
                    return null;
                }
            }
        }
    }

    /**
     * Method fir publishing progress.
     * @param messages input array of messages.
     */
    @Override
    public void onProgressUpdate(String ... messages){
        switch (messages.length){
            case 0:
                parent.setState(this.state);
                break;
            case 1:
                parent.handleAnswer(messages[0]);
                break;
            default:
                //err
                break;
        }
    }
    //--------------------------End of AsynkTask methods--------------------

    /**
     * Method fot logging.
     * @param msg message for logging.
     */
    public void log(String msg) {
        parent.log("Listener " + msg);
    }

    /**
     * Method for establishing state of progress.
     * @param state State for establishing.
     */
    public void setState(SmartHomeState state){
        this.state=state;
        publishProgress();
    }
}
