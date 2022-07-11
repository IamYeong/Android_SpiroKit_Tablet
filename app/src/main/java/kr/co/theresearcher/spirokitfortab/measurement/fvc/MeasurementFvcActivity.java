package kr.co.theresearcher.spirokitfortab.measurement.fvc;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

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
import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import kr.co.theresearcher.spirokitfortab.Fluid;
import kr.co.theresearcher.spirokitfortab.HashConverter;
import kr.co.theresearcher.spirokitfortab.R;
import kr.co.theresearcher.spirokitfortab.SharedPreferencesManager;
import kr.co.theresearcher.spirokitfortab.bluetooth.SpiroKitBluetoothLeService;
import kr.co.theresearcher.spirokitfortab.calc.CalcSpiroKitE;

import kr.co.theresearcher.spirokitfortab.db.SpiroKitDatabase;
import kr.co.theresearcher.spirokitfortab.db.cal_history.CalHistory;
import kr.co.theresearcher.spirokitfortab.db.cal_history_raw_data.CalHistoryRawData;
import kr.co.theresearcher.spirokitfortab.dialog.ConfirmDialog;
import kr.co.theresearcher.spirokitfortab.dialog.LoadingDialog;
import kr.co.theresearcher.spirokitfortab.graph.ResultCoordinate;
import kr.co.theresearcher.spirokitfortab.graph.TimerProgressView;
import kr.co.theresearcher.spirokitfortab.graph.VolumeFlowResultView;
import kr.co.theresearcher.spirokitfortab.graph.VolumeFlowRunView;
import kr.co.theresearcher.spirokitfortab.graph.VolumeTimeResultView;
import kr.co.theresearcher.spirokitfortab.graph.VolumeTimeRunView;
import kr.co.theresearcher.spirokitfortab.graph.WeakFlowProgressView;
import kr.co.theresearcher.spirokitfortab.main.result.OnOrderSelectedListener;

public class MeasurementFvcActivity extends AppCompatActivity {

    private SpiroKitBluetoothLeService mService;
    private FrameLayout realTimeVolumeFlowGraphLayout, realTimeVolumeTimeGraphLayout;
    private FrameLayout resultVolumeFlowGraphLayout, resultVolumeTimeGraphLayout;
    private FrameLayout timerFrameLayout, weakFlowFrameLayout;
    private TimerProgressView timerProgressView;
    private WeakFlowProgressView weakFlowProgressView;
    private VolumeFlowRunView volumeFlowRunView;
    private VolumeTimeRunView volumeTimeRunView;
    private List<VolumeFlowResultView> volumeFlowResultViewList = new ArrayList<>();
    private List<VolumeTimeResultView> volumeTimeResultViewList = new ArrayList<>();
    private List<String> pulseWidthList = new ArrayList<>();
    private RecyclerView rv;
    private Button completeButton, preSaveButton, postSaveButton;
    private ConstraintLayout connectButton;
    private ImageView connectStateImage;
    private TextView connectStateText;
    private MaterialButton retestButton, startStopButton;
    private ProgressBar timerProgressBar, weakFlowProgressBar, connectingProgressBar;
    private ImageButton backButton;
    private FvcResultAdapter resultAdapter;
    private TextView patientNameText;
    private TextView emptyText;
    private LoadingDialog loadingDialog;

    private boolean isStart = false;
    private int dataReceivedCount = 0;
    private boolean flowToggle = false;
    private long startTimestamp;
    private boolean saveSomething = false;

    private int testOrder = 1;

    private Timer timer;
    private TimerTask timerTask;
    private int timerCount = 0;

    private Handler handler = new Handler(Looper.getMainLooper());

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmssSS", Locale.getDefault());

    private Runnable initializeEndRunnable = new Runnable() {
        @Override
        public void run() {
            if (loadingDialog.isShowing()) loadingDialog.dismiss();
        }
    };

    private Runnable terminateEndRunnable = new Runnable() {
        @Override
        public void run() {
            if (loadingDialog.isShowing()) loadingDialog.dismiss();
        }
    };

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {

            mService = ((SpiroKitBluetoothLeService.LocalBinder)iBinder).getService();

            mService.setBluetoothLeCallback(new SpiroKitBluetoothLeService.BluetoothLeCallback() {
                @Override
                public void onReadCharacteristic(byte[] data) {
                    //testTitleText.setText("READ CHARACTERISTIC");

                    int value = conversionIntegerFromByteArray(data);

                    //Log.d(getClass().getSimpleName(), "***********VALUE : " + value);

                    if (value > 10) {

                        if (!isStart) return;
                        if (pulseWidthList.size() > 1000) return;

                        dataReceivedCount++;

                        String d = "";
                        for (byte b : data) d += (char)b;

                        pulseWidthList.add(d);
                        handleData(value);

                    } else {

                        if (value == 2) {

                            handler.postDelayed(initializeEndRunnable, 3000);

                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (!loadingDialog.isShowing()) {
                                        loadingDialog = new LoadingDialog(MeasurementFvcActivity.this);
                                        loadingDialog.setTitle(getString(R.string.spirokit_initializing));
                                        loadingDialog.show();
                                    }
                                }
                            });


                        } else if (value == 3) {

                            handler.postDelayed(terminateEndRunnable, 3000);

                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (!loadingDialog.isShowing()) {
                                        loadingDialog = new LoadingDialog(MeasurementFvcActivity.this);
                                        loadingDialog.setTitle(getString(R.string.spirokit_terminating));
                                        loadingDialog.show();
                                    }
                                }
                            });



                        }

                    }

                }

                @Override
                public void onWriteCharacteristic() {
                    //testTitleText.setText("WRITE CHARACTERISTIC");


                }

                @Override
                public void onDescriptorWrite() {
                    //testTitleText.setText("DESCRIPTOR WRITE");

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            connectStateImage.setImageDrawable(AppCompatResources.getDrawable(MeasurementFvcActivity.this, R.drawable.ic_connect_spirokit));
                            connectStateImage.setVisibility(View.VISIBLE);
                            connectStateText.setText(getString(R.string.state_connect));
                            connectingProgressBar.setVisibility(View.INVISIBLE);
                        }
                    });

                }

                @Override
                public void onDiscoverServices() {
                    //testTitleText.setText("DISCOVERED SERVICES");
                }

                @Override
                public void onConnectStateChanged(int status) {
                    if (status == BluetoothProfile.STATE_CONNECTED) {
                        //testTitleText.setText(String.valueOf(status));
                    } else {

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                connectStateImage.setImageDrawable(AppCompatResources.getDrawable(MeasurementFvcActivity.this, R.drawable.ic_disconnect_spirokit));
                                connectStateImage.setVisibility(View.VISIBLE);
                                connectStateText.setText(getString(R.string.state_disconnect));
                                connectingProgressBar.setVisibility(View.INVISIBLE);
                            }
                        });

                    }
                }
            });

            if (mService.isConnect()) {

                connectStateImage.setImageDrawable(AppCompatResources.getDrawable(MeasurementFvcActivity.this, R.drawable.ic_connect_spirokit));
                connectStateImage.setVisibility(View.VISIBLE);
                connectStateText.setText(getString(R.string.state_connect));
                connectingProgressBar.setVisibility(View.INVISIBLE);
            } else {

                connectStateImage.setImageDrawable(AppCompatResources.getDrawable(MeasurementFvcActivity.this, R.drawable.ic_disconnect_spirokit));
                connectStateImage.setVisibility(View.VISIBLE);
                connectStateText.setText(getString(R.string.state_disconnect));
                connectingProgressBar.setVisibility(View.INVISIBLE);


                mService.connect(SharedPreferencesManager.getDeviceMacAddress(MeasurementFvcActivity.this));

            }

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {



        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measurement_fvc);

        patientNameText = findViewById(R.id.tv_patient_name_meas);
        backButton = findViewById(R.id.img_btn_back_meas);
        realTimeVolumeFlowGraphLayout = findViewById(R.id.frame_volume_flow_graph_meas);
        realTimeVolumeTimeGraphLayout = findViewById(R.id.frame_volume_time_graph_meas);

        resultVolumeFlowGraphLayout = findViewById(R.id.frame_volume_flow_graph_result_fvc);
        resultVolumeTimeGraphLayout = findViewById(R.id.frame_volume_time_graph_result_fvc);

        timerFrameLayout = findViewById(R.id.frame_expiratory_timer_marking);
        weakFlowFrameLayout = findViewById(R.id.frame_weak_expiratory_marking);

        preSaveButton = findViewById(R.id.btn_pre_save_fvc_meas);
        postSaveButton = findViewById(R.id.btn_post_save_fvc_meas);

        emptyText = findViewById(R.id.tv_empty_list_fvc_meas);

        rv = findViewById(R.id.rv_meas);
        retestButton = findViewById(R.id.btn_retest_meas);
        startStopButton = findViewById(R.id.btn_start_stop_meas);
        completeButton = findViewById(R.id.btn_complete_fvc_meas);

        timerProgressBar = findViewById(R.id.progressbar_expiratory_timer);
        weakFlowProgressBar = findViewById(R.id.progressbar_weak_expiratory);

        connectButton = findViewById(R.id.constraint_connect_button_fvc_meas);
        connectStateImage = findViewById(R.id.img_connect_state_fvc_meas);
        connectStateText = findViewById(R.id.tv_connect_title_fvc_meas);
        connectingProgressBar = findViewById(R.id.progress_connecting_fvc_meas);

        resultAdapter = new FvcResultAdapter(MeasurementFvcActivity.this);
        resultAdapter.setOnOrderSelectedListener(new OnOrderSelectedListener() {
            @Override
            public void onOrderSelected(int order) {

                selectData(order);

            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        resultAdapter.addEmptyObject(new ResultFVC());
        rv.setLayoutManager(linearLayoutManager);
        rv.setAdapter(resultAdapter);

        patientNameText.setText(SharedPreferencesManager.getPatientName(this));

        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mService.isConnect()) {
                    ConfirmDialog confirmDialog = new ConfirmDialog(MeasurementFvcActivity.this);
                    confirmDialog.setTitle("이미 연결돼있습니다, 검사를 시작하세요");
                    confirmDialog.show();

                    return;
                }

                mService.connect(SharedPreferencesManager.getDeviceMacAddress(MeasurementFvcActivity.this));
                connectStateImage.setVisibility(View.INVISIBLE);
                connectingProgressBar.setVisibility(View.VISIBLE);
                connectStateText.setText(getString(R.string.connecting));

            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (saveSomething) {
                    //removeThisData();

                    Thread thread = new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            Looper.prepare();

                            SpiroKitDatabase database = SpiroKitDatabase.getInstance(MeasurementFvcActivity.this);
                            database.calHistoryRawDataDao().deleteNotCompleteData();
                            SpiroKitDatabase.removeInstance();

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

                } else {

                    finish();
                }

            }
        });

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

        resultVolumeFlowGraphLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                resultVolumeFlowGraphLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                int width = resultVolumeFlowGraphLayout.getWidth();
                int height = resultVolumeFlowGraphLayout.getHeight();

                volumeFlowRunView = new VolumeFlowRunView(MeasurementFvcActivity.this);
                volumeFlowRunView.setId(View.generateViewId());
                volumeFlowRunView.setCanvasSize(width, height);
                volumeFlowRunView.setX(2f, 0f);
                volumeFlowRunView.setY(1.25f, -1.25f);
                volumeFlowRunView.setxStartPosition(0.5f);
                volumeFlowRunView.setMarkingCount(6, 10);

                volumeFlowRunView.commit();

                resultVolumeFlowGraphLayout.addView(volumeFlowRunView);

            }
        });

        resultVolumeTimeGraphLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                resultVolumeTimeGraphLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                int width = resultVolumeTimeGraphLayout.getWidth();
                int height = resultVolumeTimeGraphLayout.getHeight();

                volumeTimeRunView = new VolumeTimeRunView(MeasurementFvcActivity.this);
                volumeTimeRunView.setId(View.generateViewId());

                volumeTimeRunView.setCanvasSize(width, height);
                volumeTimeRunView.setX(0.8f, 0f);
                volumeTimeRunView.setY(0.4f, 0f);
                volumeTimeRunView.setMarkingCount(8, 6);
                volumeTimeRunView.setxStartPosition(0f);

                volumeTimeRunView.commit();

                resultVolumeTimeGraphLayout.addView(volumeTimeRunView);

            }
        });

        preSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveSomething = true;
                saveData(0);

            }
        });

        postSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveData(1);

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

                        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS")
                                .withZone(ZoneId.systemDefault());

                        Instant instant = Instant.now().truncatedTo(ChronoUnit.MICROS);

                        String historyHash = "";

                        try {
                            historyHash = HashConverter.hashingFromString(dateTimeFormatter.format(instant));
                        } catch (NoSuchAlgorithmException e) {

                        }

                        CalHistory calHistory = new CalHistory(
                                historyHash,
                                SharedPreferencesManager.getOfficeHash(MeasurementFvcActivity.this),
                                SharedPreferencesManager.getOperatorHash(MeasurementFvcActivity.this),
                                SharedPreferencesManager.getPatientHashed(MeasurementFvcActivity.this),
                                dateTimeFormatter.format(instant),
                                "f",
                                "e",
                                0);

                        SpiroKitDatabase database = SpiroKitDatabase.getInstance(MeasurementFvcActivity.this);
                        database.calHistoryDao().insertHistory(calHistory);

                        database.calHistoryRawDataDao().fillHistoryHash(historyHash);

                        SharedPreferencesManager.setHistoryHash(MeasurementFvcActivity.this, historyHash);

                        SpiroKitDatabase.removeInstance();

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

                isStart = !isStart;

                if (isStart) {

                    startStopButton.setText("일시정지");
                    startStopButton.setIcon(AppCompatResources.getDrawable(MeasurementFvcActivity.this, R.drawable.ic_baseline_pause_30_white));

                } else {

                    startStopButton.setText("측정하기");
                    startStopButton.setIcon(AppCompatResources.getDrawable(MeasurementFvcActivity.this, R.drawable.ic_baseline_play_arrow_30_white));

                }



            }
        });

        loadingDialog = new LoadingDialog(MeasurementFvcActivity.this);
        startTimestamp = Calendar.getInstance().getTime().getTime();

    }

    @Override
    protected void onPause() {
        super.onPause();

        unbindService(serviceConnection);

        handler.removeCallbacks(initializeEndRunnable);
        handler.removeCallbacks(terminateEndRunnable);



    }

    @Override
    protected void onResume() {
        super.onResume();

        bindService(new Intent(getApplicationContext(), SpiroKitBluetoothLeService.class), serviceConnection, Context.BIND_AUTO_CREATE);

        if (loadingDialog.isShowing()) loadingDialog.dismiss();
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

    private void saveData(int isPost) {

        isStart = false;

        Thread thread = new Thread() {

            @Override
            public void run() {
                super.run();
                Looper.prepare();

                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS")
                        .withZone(ZoneId.systemDefault());

                Instant instant = Instant.now().truncatedTo(ChronoUnit.MICROS);

                String hashed = "";

                try {
                    hashed = HashConverter.hashingFromString(dateTimeFormatter.format(instant));
                } catch (NoSuchAlgorithmException e) {

                }

                StringBuilder stringBuilder = new StringBuilder();

                for (int i = 0; i < pulseWidthList.size(); i++) {

                    String value = pulseWidthList.get(i);
                    if (pulseWidthList.size() - 1 == i) {
                        byte[] data = value.getBytes();
                        stringBuilder.append(Integer.toString(conversionIntegerFromByteArray(data)));
                        break;
                    }
                    stringBuilder.append(value);


                }

                CalHistoryRawData rawData = new CalHistoryRawData(
                        hashed,
                        null,
                        Integer.toString(testOrder),
                        stringBuilder.toString(),
                        dateTimeFormatter.format(instant),
                        isPost
                );

                SpiroKitDatabase database = SpiroKitDatabase.getInstance(MeasurementFvcActivity.this);
                database.calHistoryRawDataDao().insertRawData(rawData);
                SpiroKitDatabase.removeInstance();

                //CalHistoryRawDataDatabase.removeInstance();

                addResult(testOrder, instant, isPost);

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ConfirmDialog confirmDialog = new ConfirmDialog(MeasurementFvcActivity.this);
                        confirmDialog.setTitle(getString(R.string.save_success));
                        confirmDialog.show();

                        isStart = true;

                        volumeFlowRunView.clear();
                        volumeTimeRunView.clear();
                        volumeFlowRunView.postInvalidate();
                        volumeTimeRunView.postInvalidate();

                        resultAdapter.notifyDataSetChanged();
                        resultVolumeFlowGraphLayout.removeAllViews();
                        resultVolumeTimeGraphLayout.removeAllViews();
                        resultVolumeFlowGraphLayout.addView(volumeFlowResultViewList.get(testOrder - 1));
                        resultVolumeTimeGraphLayout.addView(volumeTimeResultViewList.get(testOrder - 1));

                        emptyText.setVisibility(View.GONE);

                        testOrder++;

                    }
                });


                Looper.loop();
            }
        };
        thread.start();

    }

    private void removeThisData() {

        String dirName = simpleDateFormat.format(startTimestamp);
        File dir = getExternalFilesDir("data/"
        + SharedPreferencesManager.getOfficeID(MeasurementFvcActivity.this) + "/"
        + SharedPreferencesManager.getPatientId(MeasurementFvcActivity.this) + "/"
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

    private void addResult(int order, Instant timestamp, int isPost) {

        //여기서는 어댑터에 추가랑 뷰배열에 추가만 해두고
        //핸들러에서 notify 수행, addVIew 하면 될 듯.

        List<Integer> pulseWidths = new ArrayList<>();

        for (int i = 0; i < pulseWidthList.size(); i++) {
            pulseWidths.add(conversionIntegerFromByteArray(pulseWidthList.get(i).getBytes()));
        }

        CalcSpiroKitE calc = new CalcSpiroKitE(pulseWidths);
        calc.measure();
        pulseWidthList.clear();

        double fvc = calc.getFVC();
        double fev1 = calc.getFev1();
        double pef = calc.getPef();

        double fvcP = calc.getFVCp(
                0,
                SharedPreferencesManager.getPatientHeight(this),
                SharedPreferencesManager.getPatientWeight(this),
                SharedPreferencesManager.getPatientGender(this)
        );

        double fev1P = calc.getFEV1p(
                0,
                SharedPreferencesManager.getPatientHeight(this),
                SharedPreferencesManager.getPatientWeight(this),
                SharedPreferencesManager.getPatientGender(this)
        );

        volumeFlowResultViewList.add(createVolumeFlowGraph(calc.getVolumeFlowGraph()));
        volumeTimeResultViewList.add(createVolumeTimeGraph(calc.getForcedVolumeTimeGraph()));

        ResultFVC resultFVC = new ResultFVC();
        resultFVC.setFvc(calc.getFVC());

        resultFVC.setFvc(fvc);
        resultFVC.setFvcPredict(fvcP);

        resultFVC.setFev1(fev1);
        resultFVC.setFev1Predict(fev1P);

        resultFVC.setFev1percent((fev1 / fvc) * 100f);
        resultFVC.setFev1PercentPredict((fev1P / fvcP) * 100f);

        resultFVC.setPef(pef);

        resultFVC.setTimestamp(timestamp.toEpochMilli());

        resultFVC.setPost(isPost);

        resultAdapter.addFvcResult(resultFVC);

    }

    private VolumeTimeResultView createVolumeTimeGraph(List<ResultCoordinate> coordinates) {

        VolumeTimeResultView volumeTimeResultView = new VolumeTimeResultView(this);
        volumeTimeResultView.setId(View.generateViewId());
        volumeTimeResultView.setCanvasSize(resultVolumeTimeGraphLayout.getWidth(), resultVolumeTimeGraphLayout.getHeight());
        volumeTimeResultView.setX(0.8f, 0f);
        volumeTimeResultView.setY(0.4f, 0f);
        volumeTimeResultView.setMarkingCount(6, 8);
        volumeTimeResultView.commit();

        for (int i = 0; i < coordinates.size(); i++) {

            double x = coordinates.get(i).getX();
            double y = coordinates.get(i).getY();

            //여기서 flow 는 호기일 때만 그려지기 때문에 판단용이라서 없애도 되지만
            //다른 그래프에 쓰일 가능성도 있기 때문에 메서드를 수정하진 않았음.
            volumeTimeResultView.setValue((float)x, (float)y, 1f);

        }

        return volumeTimeResultView;
    }

    private VolumeFlowResultView createVolumeFlowGraph(List<ResultCoordinate> coordinates) {

        VolumeFlowResultView volumeFlowResultView = new VolumeFlowResultView(this);
        volumeFlowResultView.setId(View.generateViewId());
        volumeFlowResultView.setCanvasSize(resultVolumeFlowGraphLayout.getWidth(), resultVolumeFlowGraphLayout.getHeight());
        volumeFlowResultView.setX(2f, 0f);
        volumeFlowResultView.setY(1.25f, -1.25f);
        //startPosition 없어도 됨.
        volumeFlowResultView.setMarkingCount(6, 8);
        volumeFlowResultView.commit();

        for (int i = 0; i < coordinates.size(); i++) {

            double x = coordinates.get(i).getX();
            double y = coordinates.get(i).getY();

            volumeFlowResultView.setValue((float)x, (float)y);

        }

        return volumeFlowResultView;

    }


    private void selectData(int order) {

        resultVolumeFlowGraphLayout.removeAllViews();
        resultVolumeTimeGraphLayout.removeAllViews();

        resultVolumeFlowGraphLayout.addView(volumeFlowResultViewList.get(order));
        resultVolumeTimeGraphLayout.addView(volumeTimeResultViewList.get(order));

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (saveSomething) {
            //removeThisData();

            Thread thread = new Thread() {
                @Override
                public void run() {
                    super.run();
                    Looper.prepare();

                    SpiroKitDatabase database = SpiroKitDatabase.getInstance(MeasurementFvcActivity.this);
                    database.calHistoryRawDataDao().deleteNotCompleteData();
                    SpiroKitDatabase.removeInstance();

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

        } else {

            finish();
        }

    }
}