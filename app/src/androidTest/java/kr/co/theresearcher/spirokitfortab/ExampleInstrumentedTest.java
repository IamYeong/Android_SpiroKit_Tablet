package kr.co.theresearcher.spirokitfortab;

import android.content.Context;
import android.content.res.Resources;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

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

        long nanoStamp = System.nanoTime();
        long timestamp = Calendar.getInstance().getTime().getTime();

        Calendar.getInstance().getTime().getTime();

        System.out.println("nano : " + nanoStamp);
        System.out.println("time : " + timestamp);

        long timeUnit = TimeUnit.SECONDS.convert(System.currentTimeMillis(), TimeUnit.NANOSECONDS);

        for (int i = 0; i < 1000; i++) {
            System.out.println("Elapsed nano time : " + SystemClock.elapsedRealtimeNanos());
        }


        System.out.println(timeUnit);



    }
}