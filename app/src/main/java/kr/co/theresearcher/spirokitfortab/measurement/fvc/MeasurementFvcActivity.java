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
import kr.co.theresearcher.spirokitfortab.OnItemChangedListener;
import kr.co.theresearcher.spirokitfortab.OnItemDeletedListener;
import kr.co.theresearcher.spirokitfortab.R;
import kr.co.theresearcher.spirokitfortab.SharedPreferencesManager;
import kr.co.theresearcher.spirokitfortab.bluetooth.SpiroKitBluetoothLeService;

import kr.co.theresearcher.spirokitfortab.calc.SpiroKitDataHandler;
import kr.co.theresearcher.spirokitfortab.db.SpiroKitDatabase;
import kr.co.theresearcher.spirokitfortab.db.cal_history.CalHistory;
import kr.co.theresearcher.spirokitfortab.db.cal_history_raw_data.CalHistoryRawData;
import kr.co.theresearcher.spirokitfortab.db.patient.Patient;
import kr.co.theresearcher.spirokitfortab.dialog.ConfirmDialog;
import kr.co.theresearcher.spirokitfortab.dialog.LoadingDialog;
import kr.co.theresearcher.spirokitfortab.graph.Coordinate;
import kr.co.theresearcher.spirokitfortab.graph.TimerProgressView;
import kr.co.theresearcher.spirokitfortab.graph.VolumeFlowGraphView;

import kr.co.theresearcher.spirokitfortab.graph.VolumeTimeGraphView;

import kr.co.theresearcher.spirokitfortab.graph.WeakFlowProgressView;
import kr.co.theresearcher.spirokitfortab.main.result.OnOrderSelectedListener;
import kr.co.theresearcher.spirokitfortab.measurement.svc.MeasurementSvcActivity;

public class MeasurementFvcActivity extends AppCompatActivity {

    private SpiroKitBluetoothLeService mService;
    private FrameLayout realTimeVolumeFlowGraphLayout, realTimeVolumeTimeGraphLayout;
    private FrameLayout resultVolumeFlowGraphLayout, resultVolumeTimeGraphLayout;
    private FrameLayout timerFrameLayout, weakFlowFrameLayout;
    private TimerProgressView timerProgressView;
    private WeakFlowProgressView weakFlowProgressView;

    //private VolumeFlowRunView volumeFlowRunView;
    //private VolumeTimeRunView volumeTimeRunView;
    private VolumeFlowGraphView volumeFlowGraphView;
    private VolumeTimeGraphView volumeTimeGraphView;


    private List<VolumeFlowGraphView> volumeFlowResultViewList = new ArrayList<>();
    private List<VolumeTimeGraphView> volumeTimeResultViewList = new ArrayList<>();
    private List<String> pulseWidthList = new ArrayList<>();
    private RecyclerView rv;
    private Button completeButton;

    private LinearLayout preSaveButton, postSaveButton, retestButton, startStopButton;
    private ImageView startStopImage;
    private TextView startStopText;

    private ConstraintLayout connectButton;
    private ImageView connectStateImage;
    private TextView connectStateText;
    private ProgressBar timerProgressBar, weakFlowProgressBar, connectingProgressBar;
    private ImageButton backButton;
    private FvcResultAdapter resultAdapter;
    private TextView patientNameText;
    private TextView emptyText;
    private LoadingDialog loadingDialog;

    private boolean flowToggle = false;

    private int testOrder = 1;

    private Timer timer;
    private TimerTask timerTask;
    private int timerCount = 0;

    private Handler handler = new Handler(Looper.getMainLooper());
    private SpiroKitDataHandler spiroKitDataHandler = new SpiroKitDataHandler();

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
                public void onReadCharacteristic(String data) {
                    if (data.length() == 10) {

                        if (!startStopImage.isSelected()) return;

                        pulseWidthList.add(data);
                        int n = pulseWidthList.size();
                        if (n <= 1) handleData(null, data);
                        else handleData(pulseWidthList.get(n - 2), data);

                    } else {

                        int value = Integer.parseInt(data.charAt(0) + "");

                        if (value == 2) {

                            handler.postDelayed(initializeEndRunnable, 3000);

                            handler.post(new Runnable() {
                                @Override
                                public void run() {

                                    if (loadingDialog.isShowing()) loadingDialog.dismiss();

                                    if (!loadingDialog.isShowing() && !isFinishing()) {
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

                                    if (loadingDialog.isShowing()) loadingDialog.dismiss();

                                    if (!loadingDialog.isShowing() && !isFinishing()) {
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
                            connectStateImage.setImageDrawable(AppCompatResources.getDrawable(MeasurementFvcActivity.this, R.drawable.connected_device));
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
                                connectStateImage.setImageDrawable(AppCompatResources.getDrawable(MeasurementFvcActivity.this, R.drawable.disconnected_device));
                                connectStateImage.setVisibility(View.VISIBLE);
                                connectStateText.setText(getString(R.string.state_disconnect));
                                connectingProgressBar.setVisibility(View.INVISIBLE);
                            }
                        });

                    }
                }
            });

            if (mService.isConnect()) {

                connectStateImage.setImageDrawable(AppCompatResources.getDrawable(MeasurementFvcActivity.this, R.drawable.connected_device));
                connectStateImage.setVisibility(View.VISIBLE);
                connectStateText.setText(getString(R.string.state_connect));
                connectingProgressBar.setVisibility(View.INVISIBLE);
            } else {

                connectStateImage.setImageDrawable(AppCompatResources.getDrawable(MeasurementFvcActivity.this, R.drawable.disconnected_device));
                connectStateImage.setVisibility(View.VISIBLE);
                connectStateText.setText(getString(R.string.state_disconnect));
                connectingProgressBar.setVisibility(View.INVISIBLE);

                if (SharedPreferencesManager.getDeviceMacAddress(MeasurementFvcActivity.this) == null) {

                    ConfirmDialog confirmDialog = new ConfirmDialog(MeasurementFvcActivity.this);
                    confirmDialog.setTitle(getString(R.string.not_found_saved_device));
                    confirmDialog.show();


                } else {
                    mService.connect(SharedPreferencesManager.getDeviceMacAddress(MeasurementFvcActivity.this));
                }

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

        startStopImage = findViewById(R.id.img_start_stop_meas_fvc);
        startStopText = findViewById(R.id.tv_start_stop_meas_fvc);

        resultVolumeFlowGraphLayout = findViewById(R.id.frame_volume_flow_graph_result_fvc);
        resultVolumeTimeGraphLayout = findViewById(R.id.frame_volume_time_graph_result_fvc);

        timerFrameLayout = findViewById(R.id.frame_expiratory_timer_marking);
        weakFlowFrameLayout = findViewById(R.id.frame_weak_expiratory_marking);

        preSaveButton = findViewById(R.id.btn_pre_save_fvc_meas);
        postSaveButton = findViewById(R.id.btn_post_save_fvc_meas);

        emptyText = findViewById(R.id.tv_empty_list_fvc_meas);

        rv = findViewById(R.id.rv_meas);
        retestButton = findViewById(R.id.btn_retest_meas_fvc);
        startStopButton = findViewById(R.id.btn_start_stop_fvc_meas);
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

        resultAdapter.setChangedListener(new OnItemChangedListener() {
            @Override
            public void onChanged() {
                //다시 조회
            }
        });

        resultAdapter.setDeletedListener(new OnItemDeletedListener() {
            @Override
            public void onDeleted(int index) {

                volumeFlowResultViewList.remove(index);
                volumeTimeResultViewList.remove(index);

                if (resultAdapter.getItemCount() > 0) {
                    selectData(resultAdapter.getItemCount() - 1);
                } else {
                    resultAdapter.addEmptyObject(new ResultFVC(""));
                    emptyText.setVisibility(View.VISIBLE);
                    initializeVolumeFlowResultGraph(resultVolumeFlowGraphLayout.getWidth(), resultVolumeFlowGraphLayout.getHeight());
                    initializeVolumeTimeResultGraph(resultVolumeTimeGraphLayout.getWidth(), resultVolumeTimeGraphLayout.getHeight());
                }

                resultAdapter.notifyDataSetChanged();


            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        resultAdapter.addEmptyObject(new ResultFVC(""));
        rv.setLayoutManager(linearLayoutManager);
        rv.setAdapter(resultAdapter);

        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mService.isConnect()) {
                    ConfirmDialog confirmDialog = new ConfirmDialog(MeasurementFvcActivity.this);
                    confirmDialog.setTitle(getString(R.string.already_connect));
                    confirmDialog.show();

                    return;
                }

                if (SharedPreferencesManager.getDeviceMacAddress(MeasurementFvcActivity.this) == null) {

                    ConfirmDialog confirmDialog = new ConfirmDialog(MeasurementFvcActivity.this);
                    confirmDialog.setTitle(getString(R.string.not_found_saved_device));
                    confirmDialog.show();


                } else {
                    mService.connect(SharedPreferencesManager.getDeviceMacAddress(MeasurementFvcActivity.this));
                    connectStateImage.setVisibility(View.INVISIBLE);
                    connectingProgressBar.setVisibility(View.VISIBLE);
                    connectStateText.setText(getString(R.string.connecting));
                }



            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

            }
        });

        realTimeVolumeFlowGraphLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                realTimeVolumeFlowGraphLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                int width = realTimeVolumeFlowGraphLayout.getWidth();
                int height = realTimeVolumeFlowGraphLayout.getHeight();

                volumeFlowGraphView = new VolumeFlowGraphView(MeasurementFvcActivity.this);
                volumeFlowGraphView.setId(View.generateViewId());
                volumeFlowGraphView.setCanvasSize(width, height);
                volumeFlowGraphView.setX(1.6f * ((float)width / (float)height), 0f);
                volumeFlowGraphView.setY(1.4f, -0.8f);
                volumeFlowGraphView.setMargin(30, 30, 60, 30);

                volumeFlowGraphView.commit();

                realTimeVolumeFlowGraphLayout.addView(volumeFlowGraphView);

            }
        });

        realTimeVolumeTimeGraphLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                realTimeVolumeTimeGraphLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                int width = realTimeVolumeTimeGraphLayout.getWidth();
                int height = realTimeVolumeTimeGraphLayout.getHeight();

                volumeTimeGraphView = new VolumeTimeGraphView(MeasurementFvcActivity.this);
                volumeTimeGraphView.setId(View.generateViewId());

                volumeTimeGraphView.setCanvasSize(width, height);
                volumeTimeGraphView.setX(1.5f * ((float)width / (float)height), 0f);
                volumeTimeGraphView.setY(1f, 0f);
                volumeTimeGraphView.setMargin(30,30,60,30);

                volumeTimeGraphView.commit();

                realTimeVolumeTimeGraphLayout.addView(volumeTimeGraphView);

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

                initializeVolumeFlowResultGraph(width, height);

            }
        });

        resultVolumeTimeGraphLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                resultVolumeTimeGraphLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                int width = resultVolumeTimeGraphLayout.getWidth();
                int height = resultVolumeTimeGraphLayout.getHeight();

                initializeVolumeTimeResultGraph(width, height);

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

        completeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (resultAdapter.getItemCount() == 0) {
                    return;
                }

                complete();

            }
        });


        retestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pulseWidthList.clear();
                //dataReceivedCount = 0;
                //프로그레스 바 초기화

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        volumeFlowGraphView.clear();
                        volumeTimeGraphView.clear();

                        volumeFlowGraphView.postInvalidate();
                        volumeTimeGraphView.postInvalidate();


                    }
                });
            }
        });

        startStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!startStopImage.isSelected()) {

                    startStopText.setText(getString(R.string.pause_meas));
                    startStopImage.setSelected(true);

                } else {

                    startStopText.setText(getString(R.string.start_meas));
                    startStopImage.setSelected(false);

                }



            }
        });

        loadingDialog = new LoadingDialog(MeasurementFvcActivity.this);
        setPatientInfo();

    }

    @Override
    protected void onPause() {
        super.onPause();

        unbindService(serviceConnection);

        handler.removeCallbacks(initializeEndRunnable);
        handler.removeCallbacks(terminateEndRunnable);



    }



    private void cleanUpData() {

        Thread thread = new Thread() {

            @Override
            public void run() {
                super.run();
                Looper.prepare();

                SpiroKitDatabase db = SpiroKitDatabase.getInstance(MeasurementFvcActivity.this);
                db.calHistoryRawDataDao().deleteNotCompleteData();

                Looper.loop();
            }
        };
        thread.start();
    }

    @Override
    protected void onResume() {
        super.onResume();

        bindService(new Intent(getApplicationContext(), SpiroKitBluetoothLeService.class), serviceConnection, Context.BIND_AUTO_CREATE);

        if (loadingDialog.isShowing()) loadingDialog.dismiss();
    }

    private void handleData(String pre, String data) {

        Coordinate coordinate = spiroKitDataHandler.getValue(pre, data);

        weakFlowProgressBar.setProgress((int)((float)coordinate.getLps() * 1000f));
        volumeFlowGraphView.setValue((float)coordinate.getTime(), (float)coordinate.getLps(), (float)coordinate.getVolume());
        volumeTimeGraphView.setValue((float)coordinate.getTime(), (float)coordinate.getLps(), (float)coordinate.getVolume());

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                volumeFlowGraphView.postInvalidate();
                volumeTimeGraphView.postInvalidate();
            }
        });

        if (coordinate.getLps() < 0d) {
            //흡기


            if (flowToggle) {
                flowToggle = false;

                timer.cancel();
                timerCount = 0;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        timerProgressBar.setProgress(0);
                        weakFlowProgressBar.setProgress(0);
                        volumeTimeGraphView.clear();
                    }
                });

            }

        } else {
            //호기

            if (!flowToggle) {

                flowToggle = true;

                timerCount = 0;
                timer = new Timer();
                //timerTask.cancel();
                timerTask = new TimerTask() {
                    @Override
                    public void run() {

                        if (timerCount >= 60) {


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



        }

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
                rawData.setCreateDate(dateTimeFormatter.format(instant));
                rawData.setUpdatedDate(dateTimeFormatter.format(instant));
                rawData.setCalDate(dateTimeFormatter.format(instant));

                SpiroKitDatabase database = SpiroKitDatabase.getInstance(MeasurementFvcActivity.this);
                database.calHistoryRawDataDao().insertRawData(rawData);
                SpiroKitDatabase.removeInstance();

                //CalHistoryRawDataDatabase.removeInstance();

                addResult(hashed, testOrder, instant, isPost);
                pulseWidthList.clear();

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ConfirmDialog confirmDialog = new ConfirmDialog(MeasurementFvcActivity.this);
                        confirmDialog.setTitle(getString(R.string.save_success));
                        confirmDialog.show();

                        //isStart = true;

                        volumeFlowGraphView.clear();
                        volumeTimeGraphView.clear();
                        volumeFlowGraphView.postInvalidate();
                        volumeTimeGraphView.postInvalidate();

                        resultAdapter.notifyDataSetChanged();
                        resultVolumeFlowGraphLayout.removeAllViews();
                        resultVolumeTimeGraphLayout.removeAllViews();
                        resultVolumeFlowGraphLayout.addView(volumeFlowResultViewList.get(volumeFlowResultViewList.size() - 1));
                        resultVolumeTimeGraphLayout.addView(volumeTimeResultViewList.get(volumeTimeResultViewList.size() - 1));

                        emptyText.setVisibility(View.GONE);

                        testOrder++;

                    }
                });


                Looper.loop();
            }
        };
        thread.start();

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

    private void addResult(String hash, int order, Instant timestamp, int isPost) {

        Patient patient = SpiroKitDatabase.getInstance(MeasurementFvcActivity.this).patientDao()
                .selectPatientByHash(SharedPreferencesManager.getPatientHashed(MeasurementFvcActivity.this));
        //여기서는 어댑터에 추가랑 뷰배열에 추가만 해두고
        //핸들러에서 notify 수행, addVIew 하면 될 듯.

        List<Integer> dataList = SpiroKitDataHandler.convertAll(pulseWidthList);


        double fvc = SpiroKitDataHandler.getVC(dataList);
        double fev1 = SpiroKitDataHandler.getEV1(dataList);
        double pef = SpiroKitDataHandler.getPEF(dataList);

        double fvcP = SpiroKitDataHandler.getPredictFVC(
                0,
                patient.getHeight(),
                patient.getWeight(),
                patient.getGender()
        );

        double fev1P = SpiroKitDataHandler.getPredictFEV1(
                0,
                patient.getHeight(),
                patient.getWeight(),
                patient.getGender()
        );

        volumeFlowResultViewList.add(createVolumeFlowGraph(SpiroKitDataHandler.getValues(dataList), resultVolumeFlowGraphLayout.getWidth(), resultVolumeFlowGraphLayout.getHeight()));
        volumeTimeResultViewList.add(createVolumeTimeGraph(SpiroKitDataHandler.getForcedValues(dataList), resultVolumeTimeGraphLayout.getWidth(), resultVolumeTimeGraphLayout.getHeight()));

        ResultFVC resultFVC = new ResultFVC(hash);

        resultFVC.setFvc(fvc);
        resultFVC.setFvcPredict(fvcP);

        resultFVC.setFev1(fev1);
        resultFVC.setFev1Predict(fev1P);

        resultFVC.setFev1percent((fev1 / fvc) * 100f);
        resultFVC.setFev1PercentPredict((fev1P / fvcP) * 100f);

        resultFVC.setPef(pef);

        resultFVC.setTimestamp(timestamp.toEpochMilli());

        resultFVC.setPost(isPost);

        resultFVC.setOrder(order + "");

        resultAdapter.addFvcResult(resultFVC);

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
                        SharedPreferencesManager.getOfficeHash(MeasurementFvcActivity.this),
                        SharedPreferencesManager.getOperatorHash(MeasurementFvcActivity.this),
                        SharedPreferencesManager.getPatientHashed(MeasurementFvcActivity.this),
                        dateTimeFormatter.format(instant),
                        "f",
                        "e",
                        0);

                calHistory.setUpdatedDate(dateTimeFormatter.format(instant));
                calHistory.setCreateDate(dateTimeFormatter.format(instant));
                calHistory.setFamilyDoctorHash(SharedPreferencesManager.getFamilyDoctorHash(MeasurementFvcActivity.this));

                SpiroKitDatabase database = SpiroKitDatabase.getInstance(MeasurementFvcActivity.this);
                database.calHistoryDao().insertHistory(calHistory);

                database.calHistoryRawDataDao().fillHistoryHash(historyHash);
                database.calHistoryRawDataDao().deleteNotCompleteData();
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

    private VolumeTimeGraphView createVolumeTimeGraph(List<Coordinate> coordinates, int width, int height) {

        VolumeTimeGraphView volumeTimeResultView = new VolumeTimeGraphView(this);
        volumeTimeResultView.setId(View.generateViewId());
        volumeTimeResultView.setCanvasSize(width, height);
        volumeTimeResultView.setX(1.5f * ((float)width / (float)height), 0f);
        volumeTimeResultView.setY(1f, 0f);
        volumeTimeResultView.setMargin(30,30,60,30);
        volumeTimeResultView.setFinalPath(true);

        volumeTimeResultView.commit();

        for (int i = 0; i < coordinates.size(); i++) {

            Coordinate coordinate = coordinates.get(i);
            volumeTimeResultView.setValue((float)coordinate.getTime(), (float)coordinate.getLps(), (float)coordinate.getVolume());

        }

        return volumeTimeResultView;
    }

    private VolumeFlowGraphView createVolumeFlowGraph(List<Coordinate> coordinates, int width, int height) {

        VolumeFlowGraphView volumeFlowResultView = new VolumeFlowGraphView(this);
        volumeFlowResultView.setId(View.generateViewId());
        volumeFlowResultView.setCanvasSize(width, height);
        volumeFlowResultView.setX(1.2f * (((float)width / (float)height)), 0f);
        volumeFlowResultView.setY(1.4f, -0.8f);
        volumeFlowResultView.setMargin(30,30,60,30);

        volumeFlowResultView.commit();

        for (int i = 0; i < coordinates.size(); i++) {

            Coordinate coordinate = coordinates.get(i);
            volumeFlowResultView.setValue((float)coordinate.getTime(), (float)coordinate.getLps(), (float)coordinate.getVolume());

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
    protected void onDestroy() {
        super.onDestroy();

        cleanUpData();

    }

    private void setPatientInfo() {

        Thread thread = new Thread() {

            @Override
            public void run() {
                super.run();
                Looper.prepare();

                Patient patient = SpiroKitDatabase.getInstance(MeasurementFvcActivity.this).patientDao().selectPatientByHash(SharedPreferencesManager.getPatientHashed(MeasurementFvcActivity.this));

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

    private void initializeVolumeFlowResultGraph(int width, int height) {

        VolumeFlowGraphView volumeFlowResultView = new VolumeFlowGraphView(this);
        volumeFlowResultView.setId(View.generateViewId());
        volumeFlowResultView.setCanvasSize(width, height);
        volumeFlowResultView.setX(1.2f * (((float)width / (float)height)), 0f);
        volumeFlowResultView.setY(1.4f, -0.8f);
        volumeFlowResultView.setMargin(30,30,60,30);

        volumeFlowResultView.commit();

        resultVolumeFlowGraphLayout.removeAllViews();
        resultVolumeFlowGraphLayout.addView(volumeFlowResultView);

    }

    private void initializeVolumeTimeResultGraph(int width, int height) {

        VolumeTimeGraphView volumeTimeResultView = new VolumeTimeGraphView(this);
        volumeTimeResultView.setId(View.generateViewId());
        volumeTimeResultView.setCanvasSize(width, height);
        volumeTimeResultView.setX(1.5f * ((float)width / (float)height), 0f);
        volumeTimeResultView.setY(1f, 0f);
        volumeTimeResultView.setMargin(30,30,60,30);

        volumeTimeResultView.commit();

        resultVolumeTimeGraphLayout.removeAllViews();
        resultVolumeTimeGraphLayout.addView(volumeTimeResultView);

    }

}