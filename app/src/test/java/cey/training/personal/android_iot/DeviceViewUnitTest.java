package cey.training.personal.android_iot;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by ceyler on 29.12.2016.
 *
 */

public class DeviceViewUnitTest {

    @Test
    public void deviceView_stateCheck(){
        DeviceView testObject = new DeviceView("test", "CONNECTED", "unknown");
        assertEquals(testObject.image, R.drawable.ic_connected_device);
    }

    @Test
    public void deviceView_noStateCheck(){
        DeviceView testObject = new DeviceView("test", "", "unknown");
        assertEquals(testObject.image, R.drawable.ic_new_device);
    }
}
