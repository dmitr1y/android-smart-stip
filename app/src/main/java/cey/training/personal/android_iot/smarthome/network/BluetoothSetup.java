package cey.training.personal.android_iot.smarthome.network;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import java.util.Set;
import java.util.Vector;

import cey.training.personal.android_iot.smarthome.SmartHome;
import cey.training.personal.android_iot.smarthome.SmartHomeState;

import static android.app.Activity.RESULT_OK;

/**
 * Created by ceyler on 13.12.2016.
 *
 */
//Turn on bluetooth and find devices, add to list and handle choise

/**
 * Service for establishing Bluetooth connection
 *
 * @author dmitriy
 * @version 1
 */
public class BluetoothSetup extends AsyncTask<Void, String[], Void> {
    private static final int MY_REQUEST_CODE = 3525;

    private SmartHome parent;
    @SuppressLint("StaticFieldLeak")
    private Activity activity;

    private SmartHomeState state;
    private BluetoothAdapter adapter;
    private Vector<BluetoothDevice> foundDevices;
    private BroadcastReceiver receiver;

    //--------------------------Constructors--------------------------------

    /**
     * Default constructor for service.
     * @param parent Parent for service.
     */
    public BluetoothSetup(@NonNull SmartHome parent) {
        this.parent = parent;
        this.activity = parent.getActivity();

        foundDevices = new Vector<>();
    }
    //--------------------------End of constructors-------------------------

    //--------------------------getters and setters-------------------------

    /**
     * Getter of state
     * @return State
     */
    public SmartHomeState getState() {
        return state;
    }

    /**
     * Sette state.
     * @param currentState state for setting
     */
    public void setState(SmartHomeState currentState) {
        this.state = currentState;
        publishProgress();
    }
    //--------------------------end of getters and setters------------------

    //--------------------------AsynkTask methods---------------------------
    /**
     * Thread that executed in background.
     * @param params input params from constructor
     * @return null
     */
    @Override
    protected Void doInBackground(Void... params) {
        log("Background started");

        if (adapter == null) {//if it called first time
            adapter = BluetoothAdapter.getDefaultAdapter();
            log("Trying to get adapter");
        }

        if (adapter != null) {
            if (adapter.isEnabled()) {//if bluetooth is turned on
                log("adapter enabled");
                connectReceiver();
                addBondedDevices();
                startDeviceDiscovery();
            } else {
                log("adapter not enabled");
                Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                activity.startActivityForResult(enableBluetoothIntent, MY_REQUEST_CODE);
            }
        } else { // if adapter is null
            setState(SmartHomeState.NO_BLUETOOTH);
        }
        return null;
    }

    /**
     * Callback method.
     * @param result Result of Thread.
     */
    @Override
    protected void onPostExecute(Void result) {
        log("Setup in background ended");
    }

    /**
     * Method fir publishing progress.
     * @param result result array for parent.
     */
    @Override
    protected void onProgressUpdate(String[]... result) {
        log("Progress update");
        switch (result.length) {
            case 0:
                changeParentState();
                break;
            case 1:
                addDevice(result[0]);
                break;
            default:
                log("Unknown progress update");
        }
    }
    //--------------------------End of AsynkTask methods--------------------

    //--------------------------wrappers for parent method------------------

    /**
     * Logger
     * @param msg message for log
     */
    private void log(String msg){
        parent.log("Setup " + msg);
    }

    /**
     * Adding device name
     * @param deviceInfo Name of device
     */
    private void addDevice(String[] deviceInfo){
        parent.addDevice(deviceInfo);
    }

    /**
     * Method for setup parent state from this
     */
    private void changeParentState(){
        parent.setState(getState());
    }
    //--------------------------end of wrappers for parent method------------

    /**
     * Connect receiver
     */
    private void connectReceiver() {
        log("connectReceiver");
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                log("onReceive:\n" +
                        "action: " + action);

                switch (action) {
                    case BluetoothDevice.ACTION_FOUND:
                        BluetoothDevice newDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        log("found " + newDevice.getName() + "\n" + newDevice.getAddress());
                        addDevice(newDevice, false);
                        break;
                    case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                        activity.unregisterReceiver(receiver);
                        setState(SmartHomeState.FINISHED);
                        break;
                    case BluetoothAdapter.ACTION_STATE_CHANGED:
                        final int state = intent.getIntExtra(
                                BluetoothAdapter.EXTRA_STATE,
                                BluetoothAdapter.ERROR);
                        switch (state) {
                            case BluetoothAdapter.STATE_OFF:
                            case BluetoothAdapter.STATE_TURNING_OFF:
                                log("Bluetooth off");
                                setState(SmartHomeState.BLUETOOTH_OFF);
                                break;
                            case BluetoothAdapter.STATE_ON:
                            case BluetoothAdapter.STATE_TURNING_ON:
                                log("Bluetooth on");
                                parent.executeSetup();
                                break;
                        }
                        break;
                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        activity.registerReceiver(receiver, filter);
    }

    /**
     * Adding bonded devices
     */
    private void addBondedDevices() {
        log("addBondedDevices");
        Set<BluetoothDevice> bondedDevices = adapter.getBondedDevices();
        if (bondedDevices.size() > 0) {
            for (BluetoothDevice bondedDevice : bondedDevices) {
                addDevice(bondedDevice, true);
            }
        }
    }

    /**
     * Start device discovering
     */
    private void startDeviceDiscovery() {
        log("stareDeviceDiscovery");
        if (!adapter.startDiscovery())
            setState(SmartHomeState.BLUETOOTH_OFF);
    }

    /**
     * Choose device bi ID
     * @param number ID of device
     * @return BluetoothDevice
     */
    public BluetoothDevice chooseDevice(int number){
        if(number < foundDevices.size())
            return foundDevices.elementAt(number);
        else
            return null;
    }

    /**
     * Sopping search
     */
    public void cancelSearch(){
        log("Searching canceled");
        adapter.cancelDiscovery();
        activity.unregisterReceiver(receiver);
        cancel(true);
    }

    /**
     *
     * @param requestCode
     * @param resultCode
     */
    public void onActivityResult(int requestCode, int resultCode){
        if (requestCode == MY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {//if bluetooth is turned on
                parent.executeSetup();
            } else {
                setState(SmartHomeState.BLUETOOTH_OFF);
            }
        }
    }

    /**
     * Adding new device
     * @param device Device
     * @param isBonded Bonded state device
     */
    public void addDevice(BluetoothDevice device, boolean isBonded){
        if(!foundDevices.contains(device)) {
            foundDevices.add(device);
            String deviceName = device.getName();
            String deviceState="unknow";
            String deviceAddress=device.getAddress().toString();
            if (deviceName == null)
                deviceName = "Unknown device";
            if(isBonded)
                deviceState="BONDED";
            else
                deviceState="NEW";

//                deviceName = deviceName.concat(" [bonded]");
            String[] result=new String[3];
            result[0]=deviceName;
            result[1]=deviceState;
            result[2]=deviceAddress;
            publishProgress(result);
        }
    }
}
