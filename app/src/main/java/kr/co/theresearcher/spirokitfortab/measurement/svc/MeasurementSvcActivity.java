package kr.co.theresearcher.spirokitfortab.measurement.svc;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.bluetooth.BluetoothGattService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import kr.co.theresearcher.spirokitfortab.Fluid;
import kr.co.theresearcher.spirokitfortab.R;
import kr.co.theresearcher.spirokitfortab.SharedPreferencesManager;
import kr.co.theresearcher.spirokitfortab.bluetooth.SpiroKitBluetoothLeService;
import kr.co.theresearcher.spirokitfortab.calc.CalcSpiroKitE;
import kr.co.theresearcher.spirokitfortab.calc.CalcSvcSpiroKitE;
import kr.co.theresearcher.spirokitfortab.db.RoomNames;
import kr.co.theresearcher.spirokitfortab.db.meas_group.MeasGroup;
import kr.co.theresearcher.spirokitfortab.db.measurement.Measurement;
import kr.co.theresearcher.spirokitfortab.db.measurement.MeasurementDao;
import kr.co.theresearcher.spirokitfortab.db.measurement.MeasurementDatabase;
import kr.co.theresearcher.spirokitfortab.dialog.ConfirmDialog;
import kr.co.theresearcher.spirokitfortab.graph.ResultCoordinate;
import kr.co.theresearcher.spirokitfortab.graph.SlowVolumeTimeRunView;
import kr.co.theresearcher.spirokitfortab.main.result.OnOrderSelectedListener;
import kr.co.theresearcher.spirokitfortab.measurement.fvc.MeasurementFvcActivity;
import kr.co.theresearcher.spirokitfortab.measurement.fvc.ResultFVC;

public class MeasurementSvcActivity extends AppCompatActivity {

    private RecyclerView resultRV;
    private FrameLayout volumeTimeGraphLayout, resultGraphLayout;
    private Button startButton, saveButton, retestButton, completeButton;
    private ImageButton backButton;
    private TextView titleText;

    private SvcResultAdapter adapter;
    private SpiroKitBluetoothLeService mService;

    private long startTimestamp;
    private List<SlowVolumeTimeRunView> volumeTimeRunViews = new ArrayList<>();
    private List<Integer> pulseWidthList = new ArrayList<>();
    private SlowVolumeTimeRunView graphView;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmssSS", Locale.getDefault());

    private boolean isStart = false;
    private double timerCount = 0d;
    private int testOrder = 1;

    private Handler handler = new Handler(Looper.getMainLooper());

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            mService = ((SpiroKitBluetoothLeService.LocalBinder)service).getService();

            if (mService.isConnect()) {

            } else {

            }

            mService.setBluetoothLeCallback(bluetoothLeCallback);

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private SpiroKitBluetoothLeService.BluetoothLeCallback bluetoothLeCallback = new SpiroKitBluetoothLeService.BluetoothLeCallback() {
        @Override
        public void onReadCharacteristic(byte[] data) {

            if (!isStart) return;
            if (pulseWidthList.size() > 1000) return;

            int value = conversionIntegerFromByteArray(data);
            if (value > 10) {

                pulseWidthList.add(value);
                handleData(value);

            } else {


            }

        }

        @Override
        public void onWriteCharacteristic() {

        }

        @Override
        public void onDescriptorWrite() {

        }

        @Override
        public void onDiscoverServices() {

        }

        @Override
        public void onConnectStateChanged(int status) {

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measurement_svc);

        startTimestamp = Calendar.getInstance().getTime().getTime();

        resultRV = findViewById(R.id.rv_svc_meas);
        volumeTimeGraphLayout = findViewById(R.id.frame_volume_time_graph_svc_meas);
        resultGraphLayout = findViewById(R.id.frame_volume_time_graph_result_svc);
        startButton = findViewById(R.id.btn_start_stop_svc_meas);
        saveButton = findViewById(R.id.btn_save_svc_meas);
        completeButton = findViewById(R.id.btn_complete_svc_meas);

        backButton = findViewById(R.id.img_btn_back_svc_meas);

        adapter = new SvcResultAdapter(this);
        adapter.setOrderSelectedListener(new OnOrderSelectedListener() {
            @Override
            public void onOrderSelected(int order) {

            }
        });
        adapter.addEmptyResult(new ResultSVC());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        resultRV.setLayoutManager(linearLayoutManager);
        resultRV.setAdapter(adapter);

        /*
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getRealMetrics(displayMetrics);

        float density = getResources().getDisplayMetrics().density;
        float displayWidth = displayMetrics.heightPixels / density;
        float displayHeight = displayMetrics.widthPixels / density;

        //Log.d(getClass().getSimpleName(), "Density : " + density + ", Width : " + displayWidth + ", Height : " + displayHeight);

        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, (int)(displayHeight / 2f)
        );

        resultGraphLayout.setLayoutParams(layoutParams);

         */

        completeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                complete();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveData();

            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                removeThisData();
                finish();
            }
        });

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isStart = !isStart;
            }
        });


        volumeTimeGraphLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                volumeTimeGraphLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                int width = volumeTimeGraphLayout.getWidth();
                int height = volumeTimeGraphLayout.getHeight();

                graphView = new SlowVolumeTimeRunView(MeasurementSvcActivity.this);
                graphView.setId(View.generateViewId());
                graphView.setCanvasSize(width, height);
                graphView.setMarkingCount(10, 8);
                graphView.setX(60f, 0f);
                graphView.setY(0.1f, -0.1f);

                graphView.commit();

                volumeTimeGraphLayout.addView(graphView);

            }
        });

        resultGraphLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                resultGraphLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                int width = resultGraphLayout.getWidth();
                int height = resultGraphLayout.getHeight();

                SlowVolumeTimeRunView graphView = new SlowVolumeTimeRunView(MeasurementSvcActivity.this);
                graphView.setId(View.generateViewId());
                graphView.setCanvasSize(width, height);
                graphView.setMarkingCount(10, 8);
                graphView.setX(60f, 0f);
                graphView.setY(0.1f, -0.1f);

                graphView.commit();

               resultGraphLayout.addView(graphView);


            }
        });


    }

    @Override
    protected void onPause() {
        super.onPause();

        unbindService(serviceConnection);
    }

    @Override
    protected void onResume() {
        super.onResume();

        bindService(new Intent(MeasurementSvcActivity.this, SpiroKitBluetoothLeService.class), serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private int conversionIntegerFromByteArray(byte[] data) {

        int value = 0;

        for (byte b : data) {

            if ((b >= 0x30) && (b <= 0x39)) {
                value *= 10;
                value += b - 0x30;
            }

        }

        return value;

    }

    private void handleData(int value) {

        float time = 0f;
        float rps = 0f;
        float lps = 0f;
        float volume = 0f;

        if ((value > 100_000_000) && (value < 200_000_000)) {
            //흡기
            value -= 100_000_000;

            time = (float) Fluid.getTimeFromPulseWidthForE(value);
            rps = (float)Fluid.calcRevolutionPerSecond(time);
            lps = (float)Fluid.conversionLiterPerSecond(rps);
            volume = (float)Fluid.calcVolume(time, lps);


        } else if ((value > 0) && (value < 100_000_000)) {
            //호기

            time = (float) Fluid.getTimeFromPulseWidthForE(value);
            rps = (float)Fluid.calcRevolutionPerSecond(time);
            lps = (float)Fluid.conversionLiterPerSecond(rps);
            volume = (float)Fluid.calcVolume(time, lps);
            volume *= -1f;

        } else {



        }

        timerCount += time;
        graphView.setValue(time, volume);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                graphView.postInvalidate();

            }
        });


    }


    private void saveData() {

        Thread thread = new Thread() {

            @Override
            public void run() {
                super.run();
                Looper.prepare();


                try {

                    long date = Calendar.getInstance().getTime().getTime();

                    File csvFile = new File(getExternalFilesDir("data/" + SharedPreferencesManager.getOfficeID(MeasurementSvcActivity.this)
                            + "/" + SharedPreferencesManager.getPatientId(MeasurementSvcActivity.this) + "/" + simpleDateFormat.format(startTimestamp)
                            + "/" + testOrder), testOrder + ".csv");

                    FileWriter fileWriter = new FileWriter(csvFile);
                    BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

                    for (int i = 0; i < pulseWidthList.size(); i++) {

                        bufferedWriter.write(Integer.toString(pulseWidthList.get(i)));
                        bufferedWriter.write("\n");

                    }
                    pulseWidthList.clear();
                    bufferedWriter.close();
                    //CSV 파일 저장================

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("pid", SharedPreferencesManager.getPatientId(MeasurementSvcActivity.this));
                    jsonObject.put("isPost", false);
                    jsonObject.put("order", testOrder);
                    jsonObject.put("ts", date);
                    jsonObject.put("meas", MeasGroup.svc);


                    File jsonFile = new File(getExternalFilesDir("data/" + SharedPreferencesManager.getOfficeID(MeasurementSvcActivity.this)
                            + "/" + SharedPreferencesManager.getPatientId(MeasurementSvcActivity.this) + "/" + simpleDateFormat.format(startTimestamp)
                            + "/" + testOrder), testOrder + ".json");

                    FileOutputStream fileOutputStream = new FileOutputStream(jsonFile);
                    fileOutputStream.write(jsonObject.toString().getBytes());

                    fileOutputStream.close();
                    //Json 파일 저장===================

                    jsonObject = null;

                    addResult(testOrder, date, false);

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            ConfirmDialog confirmDialog = new ConfirmDialog(MeasurementSvcActivity.this);
                            confirmDialog.setTitle(getString(R.string.success_data_save));
                            confirmDialog.show();

                            isStart = true;

                            graphView.clear();
                            graphView.postInvalidate();

                            adapter.notifyDataSetChanged();

                            resultGraphLayout.removeAllViews();
                            resultGraphLayout.addView(volumeTimeRunViews.get(testOrder - 1));

                            //emptyImage.setVisibility(View.GONE);

                            testOrder++;

                        }
                    });


                } catch (JSONException e) {

                } catch (IOException e) {

                }


                Looper.loop();
            }
        };

        thread.start();

    }

    private void addResult(int order, long timestamp, boolean isPost) {

        //여기서는 어댑터에 추가랑 뷰배열에 추가만 해두고
        //핸들러에서 notify 수행, addVIew 하면 될 듯.

        List<Integer> pulseWidths = new ArrayList<>();
        try {
            FileReader fileReader = new FileReader(
                    getExternalFilesDir("data/" +
                            SharedPreferencesManager.getOfficeID(this) + "/"
                            + SharedPreferencesManager.getPatientId(this) + "/"
                            + simpleDateFormat.format(startTimestamp)
                            + "/" + testOrder + "/" + testOrder + ".csv")
            );

            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String line = "";
            while ((line = bufferedReader.readLine()) != null) {

                pulseWidths.add(Integer.parseInt(line));

            }

        } catch (FileNotFoundException e) {

        } catch (IOException e) {

        }

        CalcSvcSpiroKitE calc = new CalcSvcSpiroKitE(pulseWidths);
        calc.measure();

        volumeTimeRunViews.add(createVolumeTimeGraph(calc.getVolumeTimeGraph()));

        ResultSVC resultSVC = new ResultSVC();
        resultSVC.setVc(calc.getVC());

        adapter.addResult(resultSVC);

    }

    private void updateResult() {





    }

    private void removeThisData() {

        String dirName = simpleDateFormat.format(startTimestamp);
        File dir = getExternalFilesDir("data/"
                + SharedPreferencesManager.getOfficeID(MeasurementSvcActivity.this) + "/"
                + SharedPreferencesManager.getPatientId(MeasurementSvcActivity.this) + "/"
                + dirName);

        deleteFileWithChildren(dir);

    }

    private void deleteFileWithChildren(File fileOrDirectory) {

        if (fileOrDirectory.isDirectory()) {
            File[] files = fileOrDirectory.listFiles();
            for (File file : files) {
                deleteFileWithChildren(file);
            }
        }

        fileOrDirectory.delete();

    }

    private SlowVolumeTimeRunView createVolumeTimeGraph(List<ResultCoordinate> coordinates) {

        SlowVolumeTimeRunView graphView = new SlowVolumeTimeRunView(MeasurementSvcActivity.this);
        graphView.setId(View.generateViewId());
        graphView.setCanvasSize(resultGraphLayout.getWidth(), resultGraphLayout.getHeight());
        graphView.setMarkingCount(10, 8);
        graphView.setX(60f, 0f);
        graphView.setY(0.1f, -0.1f);

        graphView.commit();

        for (int i = 0; i < coordinates.size(); i++) {

            double x = coordinates.get(i).getX();
            double y = coordinates.get(i).getY();

            graphView.setValue((float)x, (float)y);

        }

        return graphView;

    }



    private void deleteDirs() {


    }

    private void complete() {

        Thread thread = new Thread() {

            @Override
            public void run() {
                super.run();
                Looper.prepare();

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmssSS", Locale.getDefault());

                MeasurementDatabase database = Room.databaseBuilder(MeasurementSvcActivity.this, MeasurementDatabase.class, RoomNames.ROOM_MEASUREMENT_DB_NAME).build();
                MeasurementDao measurementDao = database.measurementDao();
                Measurement measurement = new Measurement(
                        SharedPreferencesManager.getOfficeID(MeasurementSvcActivity.this),
                        SharedPreferencesManager.getPatientId(MeasurementSvcActivity.this),
                        MeasGroup.svc.ordinal(),
                        startTimestamp,
                        simpleDateFormat.format(startTimestamp)
                );

                measurementDao.insertMeasurement(measurement);
                database.close();

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                });


                Looper.loop();
            }
        };

        thread.start();

    }

}