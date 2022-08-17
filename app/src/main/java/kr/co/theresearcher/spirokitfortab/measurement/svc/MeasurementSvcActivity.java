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
import android.widget.LinearLayout;
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
import kr.co.theresearcher.spirokitfortab.OnItemDeletedListener;
import kr.co.theresearcher.spirokitfortab.R;
import kr.co.theresearcher.spirokitfortab.SharedPreferencesManager;
import kr.co.theresearcher.spirokitfortab.bluetooth.SpiroKitBluetoothLeService;
import kr.co.theresearcher.spirokitfortab.calc.CalcSpiroKitE;
import kr.co.theresearcher.spirokitfortab.calc.CalcSvcSpiroKitE;

import kr.co.theresearcher.spirokitfortab.db.SpiroKitDatabase;
import kr.co.theresearcher.spirokitfortab.db.cal_history.CalHistory;
import kr.co.theresearcher.spirokitfortab.db.cal_history_raw_data.CalHistoryRawData;
import kr.co.theresearcher.spirokitfortab.db.patient.Patient;
import kr.co.theresearcher.spirokitfortab.dialog.ConfirmDialog;
import kr.co.theresearcher.spirokitfortab.dialog.LoadingDialog;
import kr.co.theresearcher.spirokitfortab.graph.ResultCoordinate;
import kr.co.theresearcher.spirokitfortab.graph.SlowVolumeTimeGraphView;
import kr.co.theresearcher.spirokitfortab.main.result.OnOrderSelectedListener;
import kr.co.theresearcher.spirokitfortab.measurement.fvc.MeasurementFvcActivity;
import kr.co.theresearcher.spirokitfortab.measurement.fvc.ResultFVC;

public class MeasurementSvcActivity extends AppCompatActivity {

    private RecyclerView resultRV;
    private FrameLayout volumeTimeGraphLayout, resultGraphLayout;
    private Button completeButton;
    private LinearLayout startButton, retestButton, preSaveButton, postSaveButton;
    private ImageView startImage;
    private TextView startText;

    private ImageButton backButton;
    private TextView titleText, emptyText;

    private ConstraintLayout connectButton;
    private ImageView connectStateImage;
    private TextView connectStateText, patientNameText;
    private ProgressBar connectStateProgressBar;

    private SvcResultAdapter adapter;
    private SpiroKitBluetoothLeService mService;

    private long startTimestamp;
    private List<SlowVolumeTimeGraphView> volumeTimeRunViews = new ArrayList<>();
    private List<String> pulseWidthList = new ArrayList<>();

    //private SlowVolumeTimeRunView graphView;
    private SlowVolumeTimeGraphView svcGraphView;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmssSS", Locale.getDefault());

    private double timerCount = 0d;
    private int testOrder = 1;

    private int calibrationPW = 0;

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
                connectStateImage.setImageDrawable(AppCompatResources.getDrawable(MeasurementSvcActivity.this, R.drawable.connected_device));
                connectStateImage.setVisibility(View.VISIBLE);
                connectStateText.setText(getString(R.string.state_connect));
                connectStateProgressBar.setVisibility(View.INVISIBLE);
            } else {
                connectStateImage.setImageDrawable(AppCompatResources.getDrawable(MeasurementSvcActivity.this, R.drawable.disconnected_device));
                connectStateImage.setVisibility(View.VISIBLE);
                connectStateText.setText(getString(R.string.state_disconnect));
                connectStateProgressBar.setVisibility(View.INVISIBLE);


                if (SharedPreferencesManager.getDeviceMacAddress(MeasurementSvcActivity.this) == null) {

                    ConfirmDialog confirmDialog = new ConfirmDialog(MeasurementSvcActivity.this);
                    confirmDialog.setTitle(getString(R.string.not_found_saved_device));
                    confirmDialog.show();


                } else {
                    mService.connect(SharedPreferencesManager.getDeviceMacAddress(MeasurementSvcActivity.this));
                }
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private SpiroKitBluetoothLeService.BluetoothLeCallback bluetoothLeCallback = new SpiroKitBluetoothLeService.BluetoothLeCallback() {
        @Override
        public void onReadCharacteristic(String data) {


            if (data.length() == 10) {

                pulseWidthList.add(data);

                if (!startImage.isSelected()) return;
                if (timerCount >= 60d) return;
                handleData(dataToInteger(data));

            } else {

                int value = Integer.parseInt(data.charAt(0) + "");

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
                    connectStateImage.setImageDrawable(AppCompatResources.getDrawable(MeasurementSvcActivity.this, R.drawable.connected_device));
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
                        connectStateImage.setImageDrawable(AppCompatResources.getDrawable(MeasurementSvcActivity.this, R.drawable.disconnected_device));
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
        patientNameText = findViewById(R.id.tv_patient_name_svc_meas);
        volumeTimeGraphLayout = findViewById(R.id.frame_volume_time_graph_svc_meas);
        resultGraphLayout = findViewById(R.id.frame_volume_time_graph_result_svc);
        startButton = findViewById(R.id.btn_start_stop_svc_meas);
        preSaveButton = findViewById(R.id.btn_pre_save_svc_meas);
        postSaveButton = findViewById(R.id.btn_post_save_svc_meas);
        completeButton = findViewById(R.id.btn_complete_svc_meas);
        emptyText = findViewById(R.id.tv_empty_svc_meas);

        startImage = findViewById(R.id.img_start_stop_meas_svc);
        startText = findViewById(R.id.tv_start_stop_meas_svc);

        connectStateProgressBar = findViewById(R.id.progress_connecting_svc_meas);
        connectStateImage = findViewById(R.id.img_connect_state_svc_meas);
        connectStateText = findViewById(R.id.tv_connect_title_svc_meas);
        connectButton = findViewById(R.id.constraint_connect_button_svc_meas);

        backButton = findViewById(R.id.img_btn_back_svc_meas);
        retestButton = findViewById(R.id.btn_retest_meas_svc);

        adapter = new SvcResultAdapter(this);
        adapter.setOrderSelectedListener(new OnOrderSelectedListener() {
            @Override
            public void onOrderSelected(int order) {

                selectData(order);

            }
        });
        adapter.addEmptyResult(new ResultSVC(""));


        adapter.setDeletedListener(new OnItemDeletedListener() {
            @Override
            public void onDeleted(int index) {

                volumeTimeRunViews.remove(index);

                if (adapter.getItemCount() > 0) {
                    selectData(adapter.getItemCount() - 1);
                } else {

                    adapter.addEmptyResult(new ResultSVC(""));
                    emptyText.setVisibility(View.VISIBLE);
                    initializeResultGraph(resultGraphLayout.getWidth(), resultGraphLayout.getHeight());
                }

                adapter.notifyDataSetChanged();

            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        resultRV.setLayoutManager(linearLayoutManager);
        resultRV.setAdapter(adapter);
        resultRV.setVisibility(View.INVISIBLE);

        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mService.isConnect()) {
                    ConfirmDialog confirmDialog = new ConfirmDialog(MeasurementSvcActivity.this);
                    confirmDialog.setTitle(getString(R.string.already_connect));
                    confirmDialog.show();

                    return;
                }

                if (SharedPreferencesManager.getDeviceMacAddress(MeasurementSvcActivity.this) == null) {
                    ConfirmDialog confirmDialog = new ConfirmDialog(MeasurementSvcActivity.this);
                    confirmDialog.setTitle(getString(R.string.not_found_saved_device));
                    confirmDialog.show();
                } else {
                    mService.connect(SharedPreferencesManager.getDeviceMacAddress(MeasurementSvcActivity.this));
                    connectStateImage.setVisibility(View.INVISIBLE);
                    connectStateProgressBar.setVisibility(View.VISIBLE);
                    connectStateText.setText(getString(R.string.connecting));
                }

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
                if (adapter.getItemCount() == 0) {
                    return;
                }
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

                        svcGraphView.clear();
                        svcGraphView.postInvalidate();

                    }
                });

            }
        });

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!startImage.isSelected()) {

                    startText.setText(getString(R.string.pause_meas));
                    startImage.setSelected(true);

                } else {

                    startText.setText(getString(R.string.start_meas));
                    startImage.setSelected(false);

                }
            }
        });


        volumeTimeGraphLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                volumeTimeGraphLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                int width = volumeTimeGraphLayout.getWidth();
                int height = volumeTimeGraphLayout.getHeight();

                svcGraphView = new SlowVolumeTimeGraphView(MeasurementSvcActivity.this);
                svcGraphView.setId(View.generateViewId());
                svcGraphView.setCanvasSize(width, height);
                svcGraphView.setX(60f, 0f);
                svcGraphView.setY(0.5f  * ((float)height / (float)width), -0.5f * ((float)height / (float)width));
                svcGraphView.setMargin(30,30,60,30);

                svcGraphView.commit();

                volumeTimeGraphLayout.addView(svcGraphView);

            }
        });

        resultGraphLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                resultGraphLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                int width = resultGraphLayout.getWidth();
                int height = resultGraphLayout.getHeight();

                initializeResultGraph(width, height);

            }
        });

        loadingDialog = new LoadingDialog(MeasurementSvcActivity.this);
        setPatientInfo();

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

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();
                Looper.prepare();

                SpiroKitDatabase database = SpiroKitDatabase.getInstance(MeasurementSvcActivity.this);
                database.calHistoryRawDataDao().deleteNotCompleteData();
                SpiroKitDatabase.removeInstance();

                Looper.loop();Thread thread = new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        Looper.prepare();

                        SpiroKitDatabase database = SpiroKitDatabase.getInstance(MeasurementSvcActivity.this);
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
            }
        };
        thread.start();
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

    private int calibratePW(int prePW, int pw1, int pw2) {

        //부호 같은지 검사
        if (((pw1 / 100_000_000) + (pw2 / 100_000_000)) == 1) return pw2;
            //5,000,000 이상 검사
        else if ((pw2 >= 105_000_000) || ((pw2 < 100_000_000) && (pw2 >= 5_000_000))) return pw2;
        else if ((pw1 >= 105_000_000) || ((pw1 < 100_000_000) && (pw1 >= 5_000_000))) return pw2;
        else {

            //조건통과하면 실제 보정
            return prePW - (int)(((float)pw1 - (float)pw2) * 1.012f);

        }

    }

    private int dataToInteger(String data) {
        int value = 0;

        for (int i = 0; i < data.length() - 1; i++) {

            char c = data.charAt(i);
            value *= 10;
            value += Integer.parseInt(c + "");

        }

        return value;
    }

    private void handleData(int value) {

        float time = 0f;
        float rps = 0f;
        float lps = 0f;
        float volume = 0f;

        if (pulseWidthList.size() >= 2) value = calibratePW(calibrationPW, dataToInteger(pulseWidthList.get(pulseWidthList.size() - 2)), value);
        calibrationPW = value;

        if ((value > 100_000_000) && (value < 200_000_000)) {
            //흡기
            value -= 100_000_000;

            time = (float) Fluid.getTimeFromPulseWidthForE(value);
            rps = (float)Fluid.calcRevolutionPerSecond(time);
            lps = (float)Fluid.conversionLiterPerSecond(rps);
            if (lps > 0.12f) volume = (float)Fluid.calcVolume(time, lps);
            else {
                time = 0f;
            }


        } else if ((value > 0) && (value < 100_000_000)) {
            //호기

            time = (float) Fluid.getTimeFromPulseWidthForE(value);
            rps = (float)Fluid.calcRevolutionPerSecond(time);
            lps = (float)Fluid.conversionLiterPerSecond(rps);
            if (lps > 0.12f) volume = (float)Fluid.calcVolume(time, lps);
            else {
                time = 0f;
            }
            volume *= -1f;

        } else {



        }

        timerCount += time;
        svcGraphView.setValue(time, volume);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                svcGraphView.postInvalidate();

            }
        });

    }

    private void saveData(int isPost) {

        //isStart = false;

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

                    stringBuilder.append(pulseWidthList.get(i));

                }

                CalHistoryRawData rawData = new CalHistoryRawData(
                        hashed,
                        null,
                        Integer.toString(testOrder),
                        stringBuilder.toString(),
                        dateTimeFormatter.format(instant),
                        isPost
                );
                rawData.setCalDate(dateTimeFormatter.format(instant));
                rawData.setUpdatedDate(dateTimeFormatter.format(instant));

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

                        //isStart = true;
                        timerCount = 0d;

                        svcGraphView.clear();
                        svcGraphView.postInvalidate();

                        adapter.notifyDataSetChanged();
                        resultRV.setVisibility(View.VISIBLE);

                        resultGraphLayout.removeAllViews();
                        resultGraphLayout.addView(volumeTimeRunViews.get(volumeTimeRunViews.size() - 1));

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

        List<Integer> dataList = new ArrayList<>();

        for (int i = 0; i < pulseWidthList.size(); i++) {
            dataList.add(dataToInteger(pulseWidthList.get(i)));
        }

        CalcSvcSpiroKitE calc = new CalcSvcSpiroKitE(dataList);
        calc.measure();
        pulseWidthList.clear();

        double vc = calc.getVC();

        volumeTimeRunViews.add(createVolumeTimeGraph(calc.getVolumeTimeGraph(), resultGraphLayout.getWidth(), resultGraphLayout.getHeight()));


        ResultSVC resultSVC = new ResultSVC(hash);

        resultSVC.setVc(vc);

        resultSVC.setTimestamp(timestamp.toEpochMilli());
        resultSVC.setPost(isPost);
        resultSVC.setOrder(order + "");

        adapter.addResult(resultSVC);

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

    private SlowVolumeTimeGraphView createVolumeTimeGraph(List<ResultCoordinate> coordinates, int width, int height) {

        SlowVolumeTimeGraphView graphView = new SlowVolumeTimeGraphView(MeasurementSvcActivity.this);
        graphView.setId(View.generateViewId());
        graphView.setCanvasSize(width, height);
        graphView.setX(60f, 0f);
        graphView.setY(0.5f * ((float)height / (float)width), -0.5f * ((float)height / (float)width));
        graphView.setMargin(30,30,60,30);

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

    private String byteToString(byte[] data) {
        String value = "";
        for (int i = 0; i < data.length; i++) {
            value += (char)data[i];
        }

        return value;
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

                calHistory.setUpdatedDate(dateTimeFormatter.format(instant));
                calHistory.setFamilyDoctorHash(SharedPreferencesManager.getFamilyDoctorHash(MeasurementSvcActivity.this));

                SpiroKitDatabase database = SpiroKitDatabase.getInstance(MeasurementSvcActivity.this);
                database.calHistoryDao().insertHistory(calHistory);

                database.calHistoryRawDataDao().fillHistoryHash(historyHash);
                database.calHistoryRawDataDao().deleteNotCompleteData();

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

    private void setPatientInfo() {

        Thread thread = new Thread() {

            @Override
            public void run() {
                super.run();
                Looper.prepare();

                Patient patient = SpiroKitDatabase.getInstance(MeasurementSvcActivity.this).patientDao().selectPatientByHash(SharedPreferencesManager.getPatientHashed(MeasurementSvcActivity.this));

                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        patientNameText.setText(patient.getName());

                    }
                });

                Looper.loop();
            }
        };
        thread.start();

    }

    private void initializeResultGraph(int width, int height) {
        SlowVolumeTimeGraphView graphView = new SlowVolumeTimeGraphView(MeasurementSvcActivity.this);
        graphView.setId(View.generateViewId());
        graphView.setCanvasSize(width, height);
        graphView.setX(60f, 0f);
        graphView.setY(0.5f * ((float)height / (float)width), -0.5f * ((float)height / (float)width));
        graphView.setMargin(30,30,60,30);

        graphView.commit();

        resultGraphLayout.removeAllViews();
        resultGraphLayout.addView(graphView);
    }

}