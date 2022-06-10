package kr.co.theresearcher.spirokitfortab.measurement;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ButtonBarLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.bluetooth.BluetoothProfile;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import kr.co.theresearcher.spirokitfortab.Fluid;
import kr.co.theresearcher.spirokitfortab.R;
import kr.co.theresearcher.spirokitfortab.SharedPreferencesManager;
import kr.co.theresearcher.spirokitfortab.bluetooth.SpiroKitBluetoothLeService;
import kr.co.theresearcher.spirokitfortab.graph.VolumeFlowResultView;
import kr.co.theresearcher.spirokitfortab.graph.VolumeFlowRunView;
import kr.co.theresearcher.spirokitfortab.graph.VolumeTimeResultView;
import kr.co.theresearcher.spirokitfortab.graph.VolumeTimeRunView;
import kr.co.theresearcher.spirokitfortab.setting.SettingActivity;

public class MeasurementFvcActivity extends AppCompatActivity {

    private SpiroKitBluetoothLeService mService;
    private FrameLayout realTimeVolumeFlowGraphLayout, realTimeVolumeTimeGraphLayout;
    private VolumeFlowRunView volumeFlowRunView;
    private VolumeTimeRunView volumeTimeRunView;
    private List<VolumeFlowResultView> volumeFlowResultViewList = new ArrayList<>();
    private List<VolumeTimeResultView> volumeTimeResultViewList = new ArrayList<>();
    private List<Integer> pulseWidthList = new ArrayList<>();
    private RecyclerView rv;
    private Button retestButton, completeButton, startStopButton;

    private boolean isStart = false;
    private int dataReceivedCount = 0;
    private boolean flowToggle = false;

    private Timer timer;
    private TimerTask timerTask;
    private int timerCount = 0;

    //...

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
        rv = findViewById(R.id.rv_meas);
        retestButton = findViewById(R.id.btn_retest_meas);
        startStopButton = findViewById(R.id.btn_start_stop_meas);
        completeButton = findViewById(R.id.btn_complete_meas);

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
                volumeFlowRunView.setMarkingCount(5, 10);

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
                volumeTimeRunView.setY(0.2f, 0f);
                volumeTimeRunView.setMarkingCount(8, 8);
                volumeTimeRunView.setxStartPosition(0f);

                volumeTimeRunView.commit();

                realTimeVolumeTimeGraphLayout.addView(volumeTimeRunView);

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
                        realTimeVolumeFlowGraphLayout.removeAllViews();
                        realTimeVolumeTimeGraphLayout.removeAllViews();
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

                /*
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initializingFlowProgressBar();
                        initializingTimeProgressBar();
                    }
                });

                 */
            }

            volumeTimeRunView.clear();

        } else if ((value > 0) && (value < 100_000_000)) {
            //호기

            if (!flowToggle) {

                flowToggle = true;

                timer = new Timer();
                //timerTask.cancel();
                timerTask = new TimerTask() {
                    @Override
                    public void run() {

                        if (timerCount > 160) {

                            timerCount = 0;
                            timer.cancel();

                        } else if (timerCount >= 60) {

                            /*
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    timeProgressBar.setProgressTintList(getResources().getColorStateList(R.color.theme_bar_change, getTheme()));
                                    timeProgressBar.setProgress(timerCount++);
                                }
                            });

                             */


                        } else {
                            /*

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    timeProgressBar.setProgressTintList(getResources().getColorStateList(R.color.white, getTheme()));
                                    timeProgressBar.setProgress(timerCount++);
                                }
                            });

                             */

                        }

                    }
                };

                //timer.scheduleAtFixedRate(timerTask, 0, 100);

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

}