package kr.co.theresearcher.spirokitfortab.measurement.fvc;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.bluetooth.BluetoothProfile;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.Buffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import kr.co.theresearcher.spirokitfortab.Fluid;
import kr.co.theresearcher.spirokitfortab.R;
import kr.co.theresearcher.spirokitfortab.SharedPreferencesManager;
import kr.co.theresearcher.spirokitfortab.bluetooth.SpiroKitBluetoothLeService;
import kr.co.theresearcher.spirokitfortab.db.RoomNames;
import kr.co.theresearcher.spirokitfortab.db.meas_group.MeasGroup;
import kr.co.theresearcher.spirokitfortab.db.measurement.Measurement;
import kr.co.theresearcher.spirokitfortab.db.measurement.MeasurementDao;
import kr.co.theresearcher.spirokitfortab.db.measurement.MeasurementDatabase;
import kr.co.theresearcher.spirokitfortab.dialog.ConfirmDialog;
import kr.co.theresearcher.spirokitfortab.graph.TimerProgressView;
import kr.co.theresearcher.spirokitfortab.graph.VolumeFlowResultView;
import kr.co.theresearcher.spirokitfortab.graph.VolumeFlowRunView;
import kr.co.theresearcher.spirokitfortab.graph.VolumeTimeResultView;
import kr.co.theresearcher.spirokitfortab.graph.VolumeTimeRunView;
import kr.co.theresearcher.spirokitfortab.graph.WeakFlowProgressView;

public class MeasurementFvcActivity extends AppCompatActivity {

    private SpiroKitBluetoothLeService mService;
    private FrameLayout realTimeVolumeFlowGraphLayout, realTimeVolumeTimeGraphLayout;
    private FrameLayout resultVolumeFlowGraphLayout, resultVolumeTimeGraphLayout;
    private FrameLayout timerFrameLayout, weakFlowFrameLayout;
    private TimerProgressView timerProgressView;
    private WeakFlowProgressView weakFlowProgressView;
    private VolumeFlowRunView volumeFlowRunView;
    private VolumeTimeRunView volumeTimeRunView;
    private List<VolumeFlowRunView> volumeFlowResultViewList = new ArrayList<>();
    private List<VolumeTimeRunView> volumeTimeResultViewList = new ArrayList<>();
    private List<Integer> pulseWidthList = new ArrayList<>();
    private RecyclerView rv;
    private Button retestButton, completeButton, startStopButton, preSaveButton, postSaveButton;
    private ProgressBar timerProgressBar, weakFlowProgressBar;

    private boolean isStart = false;
    private int dataReceivedCount = 0;
    private boolean flowToggle = false;
    private long startTimestamp;

    private int testOrder = 1;

    private Timer timer;
    private TimerTask timerTask;
    private int timerCount = 0;

    private Handler handler = new Handler(Looper.getMainLooper());

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_hhmmssSS", Locale.getDefault());

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {

            mService = ((SpiroKitBluetoothLeService.LocalBinder)iBinder).getService();

            if (mService.isConnect()) {

            } else {

            }

            mService.setBluetoothLeCallback(new SpiroKitBluetoothLeService.BluetoothLeCallback() {
                @Override
                public void onReadCharacteristic(byte[] data) {
                    //testTitleText.setText("READ CHARACTERISTIC");

                    if (!isStart) return;
                    if (pulseWidthList.size() > 1000) return;

                    dataReceivedCount++;

                    int value = conversionIntegerFromByteArray(data);
                    if (value > 10) {

                        pulseWidthList.add(value);
                        handleData(value);

                    } else {


                    }

                }

                @Override
                public void onWriteCharacteristic() {
                    //testTitleText.setText("WRITE CHARACTERISTIC");
                }

                @Override
                public void onDescriptorWrite() {
                    //testTitleText.setText("DESCRIPTOR WRITE");

                }

                @Override
                public void onDiscoverServices() {
                    //testTitleText.setText("DISCOVERED SERVICES");
                }

                @Override
                public void onConnectStateChanged(int status) {
                    if (status == BluetoothProfile.STATE_CONNECTED) {
                        //testTitleText.setText(String.valueOf(status));
                    }
                }
            });

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {



        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measurement_fvc);

        realTimeVolumeFlowGraphLayout = findViewById(R.id.frame_volume_flow_graph_meas);
        realTimeVolumeTimeGraphLayout = findViewById(R.id.frame_volume_time_graph_meas);

        resultVolumeFlowGraphLayout = findViewById(R.id.frame_volume_flow_graph_result_fvc);
        resultVolumeTimeGraphLayout = findViewById(R.id.frame_volume_time_graph_result_fvc);

        timerFrameLayout = findViewById(R.id.frame_expiratory_timer_marking);
        weakFlowFrameLayout = findViewById(R.id.frame_weak_expiratory_marking);

        preSaveButton = findViewById(R.id.btn_pre_save_fvc_meas);
        postSaveButton = findViewById(R.id.btn_post_save_fvc_meas);

        rv = findViewById(R.id.rv_meas);
        retestButton = findViewById(R.id.btn_retest_meas);
        startStopButton = findViewById(R.id.btn_start_stop_meas);
        completeButton = findViewById(R.id.btn_complete_fvc_meas);

        timerProgressBar = findViewById(R.id.progressbar_expiratory_timer);
        weakFlowProgressBar = findViewById(R.id.progressbar_weak_expiratory);

        realTimeVolumeFlowGraphLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                realTimeVolumeFlowGraphLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                int width = realTimeVolumeFlowGraphLayout.getWidth();
                int height = realTimeVolumeFlowGraphLayout.getHeight();

                volumeFlowRunView = new VolumeFlowRunView(MeasurementFvcActivity.this);
                volumeFlowRunView.setId(View.generateViewId());
                volumeFlowRunView.setCanvasSize(width, height);
                volumeFlowRunView.setX(2f, 0f);
                volumeFlowRunView.setY(1.25f, -1.25f);
                volumeFlowRunView.setxStartPosition(0.5f);
                volumeFlowRunView.setMarkingCount(8, 10);

                volumeFlowRunView.commit();

                realTimeVolumeFlowGraphLayout.addView(volumeFlowRunView);

            }
        });

        realTimeVolumeTimeGraphLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                realTimeVolumeTimeGraphLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                int width = realTimeVolumeTimeGraphLayout.getWidth();
                int height = realTimeVolumeTimeGraphLayout.getHeight();

                volumeTimeRunView = new VolumeTimeRunView(MeasurementFvcActivity.this);
                volumeTimeRunView.setId(View.generateViewId());

                volumeTimeRunView.setCanvasSize(width, height);
                volumeTimeRunView.setX(0.8f, 0f);
                volumeTimeRunView.setY(0.4f, 0f);
                volumeTimeRunView.setMarkingCount(8, 8);
                volumeTimeRunView.setxStartPosition(0f);

                volumeTimeRunView.commit();

                realTimeVolumeTimeGraphLayout.addView(volumeTimeRunView);

            }
        });

        timerFrameLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                timerFrameLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                int width = timerFrameLayout.getWidth();
                int height = timerFrameLayout.getHeight();

                timerProgressView = new TimerProgressView(MeasurementFvcActivity.this);
                timerProgressView.setId(View.generateViewId());
                timerProgressView.setSize(width, height);
                timerProgressView.setMax(16f);
                timerProgressView.setNumber(16);
                timerProgressView.setInvisibleLabels(0,1,2,3,4,5,7,8,9,10,11,12,13,14);
                timerProgressView.setInvisibleLines();
                timerProgressView.commit();

                timerFrameLayout.addView(timerProgressView);

            }
        });

        weakFlowFrameLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                weakFlowFrameLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                int width = weakFlowFrameLayout.getWidth();
                int height = weakFlowFrameLayout.getHeight();

                weakFlowProgressView = new WeakFlowProgressView(MeasurementFvcActivity.this);
                weakFlowProgressView.setId(View.generateViewId());
                weakFlowProgressView.setSize(width, height);
                weakFlowProgressView.setMax(1000f);
                weakFlowProgressView.setNumber(10);
                weakFlowProgressView.setInvisibleLabels(0,1,2,3,4,6,7,8,9);
                weakFlowProgressView.setInvisibleLines(0,1,2,3,4,6,7,8,9);
                weakFlowProgressView.commit();

                weakFlowFrameLayout.addView(weakFlowProgressView);

            }
        });

        preSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveData(false);

            }
        });

        postSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveData(true);

            }
        });

        completeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Thread thread = new Thread() {

                    @Override
                    public void run() {
                        super.run();
                        Looper.prepare();

                        MeasurementDatabase database = Room.databaseBuilder(MeasurementFvcActivity.this, MeasurementDatabase.class, RoomNames.ROOM_MEASUREMENT_DB_NAME).build();
                        MeasurementDao measurementDao = database.measurementDao();
                        Measurement measurement = new Measurement(SharedPreferencesManager.getPatientId(MeasurementFvcActivity.this),
                                MeasGroup.fvc.ordinal(), startTimestamp, simpleDateFormat.format(startTimestamp));
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
        });


        retestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pulseWidthList.clear();
                dataReceivedCount = 0;
                //프로그레스 바 초기화

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        volumeFlowRunView.clear();
                        volumeTimeRunView.clear();

                        volumeFlowRunView.postInvalidate();
                        volumeTimeRunView.postInvalidate();


                    }
                });
            }
        });

        startStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isStart) {

                    startStopButton.setText(getString(R.string.stop));

                } else {

                    startStopButton.setText(getString(R.string.start));

                }

                isStart = !isStart;

            }
        });

        startTimestamp = Calendar.getInstance().getTime().getTime();

    }

    @Override
    protected void onPause() {
        super.onPause();

        unbindService(serviceConnection);

    }

    @Override
    protected void onResume() {
        super.onResume();

        bindService(new Intent(getApplicationContext(), SpiroKitBluetoothLeService.class), serviceConnection, Context.BIND_AUTO_CREATE);
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
            lps *= -1f;

            if (flowToggle) {
                flowToggle = false;

                timer.cancel();
                timerCount = 0;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        timerProgressBar.setProgress(0);
                        weakFlowProgressBar.setProgress(0);
                        volumeTimeRunView.clear();
                    }
                });

            }

        } else if ((value > 0) && (value < 100_000_000)) {
            //호기

            if (!flowToggle) {

                flowToggle = true;

                timerCount = 0;
                timer = new Timer();
                //timerTask.cancel();
                timerTask = new TimerTask() {
                    @Override
                    public void run() {

                        if (timerCount > 160) {

                            //timerCount = 0;
                            //timer.cancel();

                        } else if (timerCount >= 60) {


                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //timerProgressBar.setProgressTintList(getResources().getColorStateList(R.color.theme_bar_change, getTheme()));
                                    timerProgressBar.setProgress(timerCount++);
                                }
                            });




                        } else {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    //timerProgressBar.setProgressTintList(getResources().getColorStateList(R.color.white, getTheme()));
                                    timerProgressBar.setProgress(timerCount++);
                                }
                            });

                        }

                    }
                };

                timer.scheduleAtFixedRate(timerTask, 0, 100);

            }

            time = (float)Fluid.getTimeFromPulseWidthForE(value);
            rps = (float)Fluid.calcRevolutionPerSecond(time);
            lps = (float)Fluid.conversionLiterPerSecond(rps);
            volume = (float)Fluid.calcVolume(time, lps);

        } else {
            //100,000,000 or 0

            /*
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    initializingTimeProgressBar();
                    initializingFlowProgressBar();
                }
            });

             */

        }

        weakFlowProgressBar.setProgress((int)(lps * 1000f));
        volumeFlowRunView.setValue(volume, lps);
        volumeTimeRunView.setValue(time, volume, lps);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                volumeFlowRunView.postInvalidate();
                volumeTimeRunView.postInvalidate();
            }
        });

        //updateFlowProgressBar((float)lps);

    }

    private void saveData(boolean isPost) {

        isStart = false;

        Thread thread = new Thread() {

            @Override
            public void run() {
                super.run();
                Looper.prepare();

                try {

                    long date = Calendar.getInstance().getTime().getTime();

                    File csvFile = new File(getExternalFilesDir("data/" + SharedPreferencesManager.getOfficeID(MeasurementFvcActivity.this)
                            + "/" + SharedPreferencesManager.getPatientId(MeasurementFvcActivity.this) + "/" + simpleDateFormat.format(startTimestamp)
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
                    jsonObject.put("pid", SharedPreferencesManager.getPatientId(MeasurementFvcActivity.this));
                    jsonObject.put("isPost", isPost);
                    jsonObject.put("order", testOrder);
                    jsonObject.put("ts", date);
                    jsonObject.put("meas", MeasGroup.fvc);


                    File jsonFile = new File(getExternalFilesDir("data/" + SharedPreferencesManager.getOfficeID(MeasurementFvcActivity.this)
                            + "/" + SharedPreferencesManager.getPatientId(MeasurementFvcActivity.this) + "/" + simpleDateFormat.format(startTimestamp)
                            + "/" + testOrder), testOrder + ".json");

                    FileOutputStream fileOutputStream = new FileOutputStream(jsonFile);
                    fileOutputStream.write(jsonObject.toString().getBytes());

                    fileOutputStream.close();
                    //Json 파일 저장===================

                    jsonObject = null;

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            ConfirmDialog confirmDialog = new ConfirmDialog(MeasurementFvcActivity.this);
                            confirmDialog.setTitle(getString(R.string.success_data_save));
                            confirmDialog.show();

                            isStart = true;

                            volumeFlowRunView.clear();
                            volumeTimeRunView.clear();
                            volumeFlowRunView.postInvalidate();
                            volumeTimeRunView.postInvalidate();


                        }
                    });


                } catch (JSONException e) {

                } catch (IOException e) {

                }

                testOrder++;
                Looper.loop();
            }
        };
        thread.start();

    }

    private void addResult() {

    }

}