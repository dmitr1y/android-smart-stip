package cey.training.personal.android_iot;

import org.junit.Test;

import cey.training.personal.android_iot.smarthome.AnswerParser;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class AnswerParserUnitTest {
    @Test
    public void answerToString_isParseCorrect() throws Exception{
        assertEquals(AnswerParser.answerToString("#input1=5#input2=7"), "Температура1: 5\nТемпература2: 7");
    }

    @Test
    public void answerToString_checkNull() throws Exception{
        assertEquals(AnswerParser.answerToString(""), "");
    }
}