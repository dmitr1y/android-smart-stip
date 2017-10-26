package cey.training.personal.android_iot;

import android.support.test.InstrumentationRegistry;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Created by ceyler on 29.12.2016.
 *
 */

public class BoxAdapterInstrumentedTest {
    private BoxAdapter testObject;
    public BoxAdapterInstrumentedTest(){
        ArrayList<DeviceView> testList = new ArrayList<>();
        testList.add(new DeviceView("test1", "NEW", "unknown"));
        testList.add(new DeviceView("test2", "BONDED", "unknown"));
        testList.add(new DeviceView("test3", "CONNECTED", "unknown"));
        testObject = new BoxAdapter(InstrumentationRegistry.getContext(), testList);
    }

    @Test
    public void boxAdapter_testGetCount(){
        assertEquals(testObject.getCount(), 3);
    }

    @Test
    public void boxAdapter_testGetItem(){
        assertEquals(((DeviceView)testObject.getItem(1)).name, "test2");
    }

    @Test
    public void boxAdapter_testGetItemId(){
        assertEquals(testObject.getItemId(2), 2);
    }
}
