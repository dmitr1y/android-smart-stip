package cey.training.personal.android_iot;

/**
 * Created by dmitriy on 27.12.16.
 */

import android.util.Log;

/**
 * Item of ListView
 *
 * @author dmitriy
 * @version 1
 */
public class DeviceView {

    String name;
    String state;
    String address;
    int image;

    /**
     * Default constructor
     *
     * @param _describe device name
     * @param _state device connection state
     * @param _address device MAC address     */
    DeviceView(String _describe, String _state, String _address) {

        name = _describe;
        state=_state;
        //default
        image=R.drawable.ic_new_device;
        if (state.equals("BONDED")){
            //// TODO: 27.12.16 image bonded
            image=R.drawable.ic_bonded_device;
        }
        if (state.equals("CONNECTED")){
            //// TODO: 27.12.16 image connected
            image=R.drawable.ic_connected_device;
        }
        if (state.equals("NEW")||state.equals("unknown")){
            //// TODO: 27.12.16 image New
            image=R.drawable.ic_new_device;
        }
       // image = _image;
        address=_address;
        //Log.d("DeviceView","created item: "+ name+", "+address+ ", "+state);//TODO rewrite with listener
    }
}
