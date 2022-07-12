package kr.co.theresearcher.spirokitfortab.measurement.svc;

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
import android.util.Log;
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

import kr.co.theresearcher.spirokitfortab.Fluid;
import kr.co.theresearcher.spirokitfortab.HashConverter;
import kr.co.theresearcher.spirokitfortab.R;
import kr.co.theresearcher.spirokitfortab.SharedPreferencesManager;
import kr.co.theresearcher.spirokitfortab.bluetooth.SpiroKitBluetoothLeService;
import kr.co.theresearcher.spirokitfortab.calc.CalcSpiroKitE;
import kr.co.theresearcher.spirokitfortab.calc.CalcSvcSpiroKitE;

import kr.co.theresearcher.spirokitfortab.db.SpiroKitDatabase;
import kr.co.theresearcher.spirokitfortab.db.cal_history.CalHistory;
import kr.co.theresearcher.spirokitfortab.db.cal_history_raw_data.CalHistoryRawData;
import kr.co.theresearcher.spirokitfortab.dialog.ConfirmDialog;
import kr.co.theresearcher.spirokitfortab.dialog.LoadingDialog;
import kr.co.theresearcher.spirokitfortab.graph.ResultCoordinate;
import kr.co.theresearcher.spirokitfortab.graph.SlowVolumeTimeRunView;
import kr.co.theresearcher.spirokitfortab.main.result.OnOrderSelectedListener;
import kr.co.theresearcher.spirokitfortab.measurement.fvc.MeasurementFvcActivity;
import kr.co.theresearcher.spirokitfortab.measurement.fvc.ResultFVC;

public class MeasurementSvcActivity extends AppCompatActivity {

    private RecyclerView resultRV;
    private FrameLayout volumeTimeGraphLayout, resultGraphLayout;
    private Button completeButton, preSaveButton, postSaveButton;
    private MaterialButton startButton, retestButton;
    private ImageButton backButton;
    private TextView titleText, emptyText;

    private ConstraintLayout connectButton;
    private ImageView connectStateImage;
    private TextView connectStateText;
    private ProgressBar connectStateProgressBar;

    private SvcResultAdapter adapter;
    private SpiroKitBluetoothLeService mService;

    private long startTimestamp;
    private List<SlowVolumeTimeRunView> volumeTimeRunViews = new ArrayList<>();
    private List<String> pulseWidthList = new ArrayList<>();
    private SlowVolumeTimeRunView graphView;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmssSS", Locale.getDefault());

    private boolean isStart = false;
    private double timerCount = 0d;
    private int testOrder = 1;

    private LoadingDialog loadingDialog;

    private Handler handler = new Handler(Looper.getMainLooper());

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
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = ((SpiroKitBluetoothLeService.LocalBinder)service).getService();

            mService.setBluetoothLeCallback(bluetoothLeCallback);

            if (mService.isConnect()) {
                connectStateImage.setImageDrawable(AppCompatResources.getDrawable(MeasurementSvcActivity.this, R.drawable.ic_connect_spirokit));
                connectStateImage.setVisibility(View.VISIBLE);
                connectStateText.setText(getString(R.string.state_connect));
                connectStateProgressBar.setVisibility(View.INVISIBLE);
            } else {
                connectStateImage.setImageDrawable(AppCompatResources.getDrawable(MeasurementSvcActivity.this, R.drawable.ic_disconnect_spirokit));
                connectStateImage.setVisibility(View.VISIBLE);
                connectStateText.setText(getString(R.string.state_disconnect));
                connectStateProgressBar.setVisibility(View.INVISIBLE);


                mService.connect(SharedPreferencesManager.getDeviceMacAddress(MeasurementSvcActivity.this));
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private SpiroKitBluetoothLeService.BluetoothLeCallback bluetoothLeCallback = new SpiroKitBluetoothLeService.BluetoothLeCallback() {
        @Override
        public void onReadCharacteristic(byte[] data) {

            int value = conversionIntegerFromByteArray(data);
            if (value > 10) {

                if (!isStart) return;
                if (timerCount >= 60d) return;

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
                                loadingDialog = new LoadingDialog(MeasurementSvcActivity.this);
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
                                loadingDialog = new LoadingDialog(MeasurementSvcActivity.this);
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

        }

        @Override
        public void onDescriptorWrite() {

            handler.post(new Runnable() {
                @Override
                public void run() {
                    connectStateImage.setImageDrawable(AppCompatResources.getDrawable(MeasurementSvcActivity.this, R.drawable.ic_connect_spirokit));
                    connectStateImage.setVisibility(View.VISIBLE);
                    connectStateText.setText(getString(R.string.state_connect));
                    connectStateProgressBar.setVisibility(View.INVISIBLE);
                }
            });
        }

        @Override
        public void onDiscoverServices() {

        }

        @Override
        public void onConnectStateChanged(int status) {
            if (status == BluetoothProfile.STATE_CONNECTED) {
                //testTitleText.setText(String.valueOf(status));
            } else {

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        connectStateImage.setImageDrawable(AppCompatResources.getDrawable(MeasurementSvcActivity.this, R.drawable.ic_disconnect_spirokit));
                        connectStateImage.setVisibility(View.VISIBLE);
                        connectStateText.setText(getString(R.string.state_disconnect));
                        connectStateProgressBar.setVisibility(View.INVISIBLE);
                    }
                });

            }
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
        preSaveButton = findViewById(R.id.btn_pre_save_svc_meas);
        postSaveButton = findViewById(R.id.btn_post_save_svc_meas);
        completeButton = findViewById(R.id.btn_complete_svc_meas);
        emptyText = findViewById(R.id.tv_empty_svc_meas);

        connectStateProgressBar = findViewById(R.id.progress_connecting_svc_meas);
        connectStateImage = findViewById(R.id.img_connect_state_svc_meas);
        connectStateText = findViewById(R.id.tv_connect_title_svc_meas);
        connectButton = findViewById(R.id.constraint_connect_button_svc_meas);

        backButton = findViewById(R.id.img_btn_back_svc_meas);
        retestButton = findViewById(R.id.btn_retest_svc_meas);

        adapter = new SvcResultAdapter(this);
        adapter.setOrderSelectedListener(new OnOrderSelectedListener() {
            @Override
            public void onOrderSelected(int order) {

                selectData(order);

            }
        });
        adapter.addEmptyResult(new ResultSVC(""));

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        resultRV.setLayoutManager(linearLayoutManager);
        resultRV.setAdapter(adapter);

        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mService.isConnect()) {
                    ConfirmDialog confirmDialog = new ConfirmDialog(MeasurementSvcActivity.this);
                    confirmDialog.setTitle("이미 연결돼있습니다, 검사를 시작하세요");
                    confirmDialog.show();

                    return;
                }

                mService.connect(SharedPreferencesManager.getDeviceMacAddress(MeasurementSvcActivity.this));
                connectStateImage.setVisibility(View.INVISIBLE);
                connectStateProgressBar.setVisibility(View.VISIBLE);
                connectStateText.setText(getString(R.string.connecting));

            }
        });

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

        preSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveData(0);

            }
        });

        postSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveData(1);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                removeThisData();
                finish();
            }
        });

        retestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                timerCount = 0;
                pulseWidthList.clear();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        graphView.clear();
                        graphView.postInvalidate();

                    }
                });

            }
        });

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isStart = !isStart;

                if (isStart) {

                    startButton.setText("일시정지");
                    startButton.setIcon(AppCompatResources.getDrawable(MeasurementSvcActivity.this, R.drawable.ic_baseline_pause_30_white));

                } else {

                    startButton.setText("측정하기");
                    startButton.setIcon(AppCompatResources.getDrawable(MeasurementSvcActivity.this, R.drawable.ic_baseline_play_arrow_30_white));

                }
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

        loadingDialog = new LoadingDialog(MeasurementSvcActivity.this);


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

        bindService(new Intent(MeasurementSvcActivity.this, SpiroKitBluetoothLeService.class), serviceConnection, Context.BIND_AUTO_CREATE);

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

                SpiroKitDatabase database = SpiroKitDatabase.getInstance(MeasurementSvcActivity.this);
                database.calHistoryRawDataDao().insertRawData(rawData);
                SpiroKitDatabase.removeInstance();

                //CalHistoryRawDataDatabase.removeInstance();

                addResult(hashed, testOrder, instant, isPost);

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ConfirmDialog confirmDialog = new ConfirmDialog(MeasurementSvcActivity.this);
                        confirmDialog.setTitle(getString(R.string.save_success));
                        confirmDialog.show();

                        isStart = true;
                        timerCount = 0d;

                        graphView.clear();
                        graphView.postInvalidate();

                        adapter.notifyDataSetChanged();

                        resultGraphLayout.removeAllViews();
                        resultGraphLayout.addView(volumeTimeRunViews.get(testOrder - 1));

                        emptyText.setVisibility(View.GONE);

                        testOrder++;

                    }
                });


                Looper.loop();
            }
        };
        thread.start();

    }

    private void addResult(String hash, int order, Instant timestamp, int isPost) {

        //여기서는 어댑터에 추가랑 뷰배열에 추가만 해두고
        //핸들러에서 notify 수행, addVIew 하면 될 듯.

        List<Integer> pulseWidths = new ArrayList<>();

        for (int i = 0; i < pulseWidthList.size(); i++) {
            pulseWidths.add(conversionIntegerFromByteArray(pulseWidthList.get(i).getBytes()));
        }

        CalcSvcSpiroKitE calc = new CalcSvcSpiroKitE(pulseWidths);
        calc.measure();
        pulseWidthList.clear();

        double vc = calc.getVC();

        volumeTimeRunViews.add(createVolumeTimeGraph(calc.getVolumeTimeGraph()));


        ResultSVC resultSVC = new ResultSVC(hash);

        resultSVC.setVc(vc);

        resultSVC.setTimestamp(timestamp.toEpochMilli());
        resultSVC.setPost(isPost);

        adapter.addResult(resultSVC);

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

    private void selectData(int order) {

        resultGraphLayout.removeAllViews();

        resultGraphLayout.addView(volumeTimeRunViews.get(order));

    }

    private void complete() {

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
                        SharedPreferencesManager.getOfficeHash(MeasurementSvcActivity.this),
                        SharedPreferencesManager.getOperatorHash(MeasurementSvcActivity.this),
                        SharedPreferencesManager.getPatientHashed(MeasurementSvcActivity.this),
                        dateTimeFormatter.format(instant),
                        "s",
                        "e",
                        0);

                SpiroKitDatabase database = SpiroKitDatabase.getInstance(MeasurementSvcActivity.this);
                database.calHistoryDao().insertHistory(calHistory);

                database.calHistoryRawDataDao().fillHistoryHash(historyHash);

                SharedPreferencesManager.setHistoryHash(MeasurementSvcActivity.this, historyHash);

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

}