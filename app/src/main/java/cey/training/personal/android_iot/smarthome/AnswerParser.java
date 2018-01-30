package cey.training.personal.android_iot.smarthome;

import android.util.Log;

import cey.training.personal.android_iot.database.VarOfMK;

/**
 * Created by dmitriy on 20.12.16.
 *
 */

/**
 * Class for parsing answer from MK.
 *
 * @author dmitriy
 * @version 1
 */
public final class AnswerParser {
/*
# for var
@ for flag

#input1=1#input2=2#input3=3 ---> input1=ui.led1 && input1.value=ui.led1.value: var with value to ListView
 */

    /**
     * Method for parse from MK answer to String.
     * @param message input String answer.
     * @return parsed String.
     */
    public static String answerToString(String message) {
        StringBuilder result = new StringBuilder();
        if (message.length() > 0) {
            message=message.replaceAll("@","");
            String[] parsedStr = message.split("#");
            for (int i=0;i<parsedStr.length;i++){
                Log.i("PARSER", "answerToString: "+parsedStr[i]);
            }
        }
        return result.toString();
    }

    /**
     * Method for saving parsed answer in database.
     * @param string Parsed answer
     */
    public static void saveParsedAnswerInDB(String string){
        String[] splitString=string.split("\n");
        VarOfMK var=new VarOfMK(splitString[0],splitString[1]);
        var.save();
    }

    /**
     * Method for parse bool variables from MK.
     * @param message
     * @return count of parsed bool variables.
     */
    public static int parseBool(String message) {
        int countVars = 0;
        //TODO write code here
        return countVars;
    }

}
