package cey.training.personal.android_iot.smarthome.network;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import cey.training.personal.android_iot.smarthome.SmartHome;
import cey.training.personal.android_iot.smarthome.SmartHomeState;

/**
 * Created by ceyler on 13.12.2016.
 *
 */

/**
 * setup connection to device and get OutputStream and/or InputStream
 *
 * @author ceyler
 * @version 1
 */
public class BluetoothConnector extends AsyncTask<Void, Void, Void> {
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private SmartHome parent;
    private BluetoothDevice deviceForConnection;

    private SmartHomeState state;

    //--------------------------Constructors--------------------------------

    /**
     * Default constructor
     * @param parent parent for service
     * @param deviceForConnection device for connection
     */
    public BluetoothConnector(@NonNull SmartHome parent,
                              @NonNull BluetoothDevice deviceForConnection) {
        this.parent = parent;
        this.deviceForConnection = deviceForConnection;
        state = parent.getState();
    }
    //--------------------------End of constructors-------------------------

    //--------------------------getters and setters-------------------------

    /**
     * Getter state
     * @return state
     */
    public SmartHomeState getState() {
        return state;
    }

    /**
     * Setter state
     * @param currentState state
     */
    public void setState(SmartHomeState currentState) {
        this.state = currentState;
        publishProgress();
    }
    //--------------------------end of getters and setters------------------

    //--------------------------AsyncTask implementation--------------------

    /**
     * Background thread
     * @param voids input params
     * @return null
     */
    @Override
    protected Void doInBackground(Void... voids) {
        if (!getState().equals(SmartHomeState.CONNECTED)) {
            log("Start device connection");
            BluetoothSocket btSocket;

            try {
                btSocket = deviceForConnection.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException ex) {
                log("Fatal Error\n" +
                        "In BluetoothConnection background socket create failed\n" +
                        ex.getMessage());
                setState(SmartHomeState.BLUETOOTH_OFF);
                return null;
            }

            // Establish the connection.  This will block until it connects.
            log("Connecting");
            try {
                btSocket.connect();
                log("Connection established and ready to data transfer");
            } catch (IOException ex) {
                try {
                    btSocket.close();
                    log("Connection closed:\n" + ex.getLocalizedMessage());
                } catch (IOException closeEx) {
                    log("Fatal Error\n" +
                            "Unable to close socket during connection failure\n" +
                            closeEx.getMessage());
                }
                setState(SmartHomeState.BLUETOOTH_OFF);
                return null;
            }

            // Create a data stream so we can talk to server.
            log("Socket creation");

            OutputStream outputStream;
            InputStream inputStream;
            try {
                outputStream = btSocket.getOutputStream();
                inputStream = btSocket.getInputStream();
            } catch (IOException ex) {
                log("Fatal Error\n" +
                        "Input and\\or output stream creation failed\n" +
                        ex.getMessage());
                setState(SmartHomeState.BLUETOOTH_OFF);
                return null;
            }

            sendStreamToParent(outputStream);
            RunListener(inputStream);
            setState(SmartHomeState.CONNECTED);
        }

        log("Device connected");
        return null;
    }

    /**
     * Publish progress
     * @param values
     */
    @Override
    protected void onProgressUpdate(Void... values) {
        parent.setState(getState());
    }
    //--------------------------End of AsyncTask implementation-------------

    //--------------------------wrappers for parent method------------------

    /**
     * log
     * @param msg message
     */
    private void log(String msg) {
        parent.log("Connector " + msg);
    }

    /**
     * Sending stream to paren
     * @param outputStream output stream
     */
    private void sendStreamToParent(OutputStream outputStream){
        parent.setOutputStream(outputStream);
    }

    /**
     * Run listener
     * @param inputStream input stream
     */
    private void RunListener(InputStream inputStream){
        parent.setupBluetoothListener(inputStream);
    }
    //--------------------------end of wrappers for parent method------------
}
