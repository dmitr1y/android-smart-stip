package cey.training.personal.android_iot.database;

import android.util.Log;

import com.orm.SugarRecord;
import com.orm.dsl.Unique;

/**
 * Created by dmitriy on 22.12.16.
 */

/**
 * Variable of MK
 *
 * Class is used for describe variable received from MK
 *
 * @author dmitriy
 * @version 1
 */
public class VarOfMK extends SugarRecord {
    @Unique
    String name;
    String value;
    String type;

    /**
     * Default constructor. Need for ORM Sugar.
     */
    public VarOfMK(){}

    /**
     * Full constructor with name, valut ant type of variable.
     *
     * @param name Name of variable
     * @param value Value of variable
     * @param type Type of variable
     */
    public VarOfMK(String name,String value, String type){
        this.name=name;
        this.value=value;
        this.type=type;
    }

    /**
     * Method for compiling string from name, value and type of variable.
     * @return
     */
    @Override
    public String toString() {
        return "VarOfMK{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                ", type='" + type + '\'' +
                '}';
    }

    /**
     * Typical constructor for an variable of MK
     * @param name Name of variable.
     * @param value Value of variable.
     */
    public VarOfMK(String name, String value){
        this.name=name;
        this.value=value;
        this.type="none";
    }

    /**
     * Write all fields in log.
     */
    public void logData(){
        Log.w("VarOfMK",toString());
    }
}
