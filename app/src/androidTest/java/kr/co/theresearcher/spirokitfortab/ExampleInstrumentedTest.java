package kr.co.theresearcher.spirokitfortab;

import android.content.Context;
import android.content.res.Resources;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        try {
            FileReader fileReader = new FileReader(
                    appContext.getExternalFilesDir("data/0/1/20220617_09234148/1/1.csv")
            );

            BufferedReader bufferedReader = new BufferedReader(fileReader);

            int count = 0;
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {

                count++;

            }
            System.out.println(count);

        } catch (FileNotFoundException e) {

        } catch (IOException e) {

        }




    }
}