package cey.training.personal.android_iot;

/**
 * Created by Ника on 23.08.2017.
 */

public class ModeView {
    String name;
    String index;
    String state;
    int image;

    ModeView(int _index, String _name){
        name=_name;
        index=Integer.toString(_index);
//        state=_state;
        image=R.drawable.ic_bonded_device;

//        if (state.equals("selected")){
//            image=R.drawable.ic_connected_device;
//        }
    }
}
