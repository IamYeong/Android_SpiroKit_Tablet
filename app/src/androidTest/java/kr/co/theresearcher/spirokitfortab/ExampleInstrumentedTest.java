package kr.co.theresearcher.spirokitfortab;

import android.content.Context;
import android.content.res.Resources;
import android.os.Looper;
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
import java.util.List;

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

        File root = appContext.getExternalFilesDir("data/"
                + SharedPreferencesManager.getOfficeID(appContext) + "/"
                + SharedPreferencesManager.getPatientId(appContext) + "/"
                + "20220619_15594114");

        File[] tests = root.listFiles();
        for (File f : tests) System.out.println(f.getName());


        if (tests == null) return;

        for (int i = 0; i < tests.length; i++) {

            //N번째 검사 directory 집합
            int n = Integer.parseInt(tests[i].getName());
            System.out.println("ORDER : " + n);
            File jsonFile = new File(tests[i], n + ".json");
            File csvFile = new File(tests[i], n + ".csv");

            try {

                BufferedReader bufferedReader = new BufferedReader(
                        new FileReader(jsonFile)
                );

                StringBuilder stringBuilder = new StringBuilder();
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {

                    stringBuilder.append(line);

                }

                JSONObject jsonObject = new JSONObject(stringBuilder.toString());

                System.out.println(jsonObject.toString());

                bufferedReader = new BufferedReader(
                        new FileReader(csvFile)
                );
                stringBuilder = new StringBuilder();
                line = "";
                List<Integer> pulseWidth = new ArrayList<>();

                while ((line = bufferedReader.readLine()) != null) {
                    pulseWidth.add(Integer.parseInt(line));
                }


                for (int e : pulseWidth) {
                    System.out.print(e + " ");
                }



            } catch (IOException e) {

            } catch (JSONException e) {

            }




            /*
            try {

                BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(appContext.openFileInput(jsonFile.getName()))
                );

                StringBuilder stringBuffer = new StringBuilder();
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuffer.append(line);
                }
                JSONObject jsonObject = new JSONObject(stringBuffer.toString());

                System.out.println(jsonObject.toString());

                //Log.d(getClass().getSimpleName(), jsonObject.toString());

                int pid = jsonObject.getInt("pid");
                int order = jsonObject.getInt("order");
                boolean isPost = jsonObject.getBoolean("ispost");
                long ts = jsonObject.getLong("ts");
                String measGroup = jsonObject.getString("meas");


                List<Integer> pulseWidth = new ArrayList<>();
                FileReader fileReader = new FileReader(csvFile);
                bufferedReader = new BufferedReader(fileReader);

                while ((line = bufferedReader.readLine()) != null) {
                    pulseWidth.add(Integer.parseInt(line));
                    //System.out.println(Integer.parseInt(line));
                }

                for (int e : pulseWidth) System.out.print(e  + " ");

                /*
                System.out.println(pulseWidth.toArray());


                        CalcSpiroKitE calc = new CalcSpiroKitE(pulseWidth);
                        pulseWidth.clear();
                        calc.measure();

                        double fvc = calc.getFVC();
                        double fev1 = calc.getFev1();
                        double pef = calc.getPef();

                        Log.d(getClass().getSimpleName(), fvc + ", " + fev1 + ", " + pef);

                        ResultFVC resultFVC = new ResultFVC();
                        resultFVC.setFvc(fvc);
                        resultFVC.setFev1(fev1);
                        resultFVC.setSelected(false);
                        resultFVC.setPef(pef);
                        //resultFVC.setPrePost(ispost);

                        fvcResults.add(resultFVC);
                        volumeFlowResultViews.add(createVolumeFlowGraph(calc.getVolumeFlowGraph()));
                        volumeTimeResultViews.add(createVolumeTimeGraph(calc.getVolumeTimeGraph()));






            } catch (JSONException e) {

            } catch (IOException e) {

            }

             */


        }



    }
}