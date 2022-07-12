package kr.co.theresearcher.spirokitfortab.main;

import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.bluetooth.BluetoothProfile;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import kr.co.theresearcher.spirokitfortab.R;
import kr.co.theresearcher.spirokitfortab.bluetooth.SpiroKitBluetoothLeService;
import kr.co.theresearcher.spirokitfortab.db.cal_history.CalHistory;
import kr.co.theresearcher.spirokitfortab.main.information.OnCalHistorySelectedListener;
import kr.co.theresearcher.spirokitfortab.main.information.PatientInformationFragment;
import kr.co.theresearcher.spirokitfortab.main.result.empty.EmptyResultFragment;
import kr.co.theresearcher.spirokitfortab.main.result.fvc.FvcResultFragment;
import kr.co.theresearcher.spirokitfortab.main.result.svc.SvcResultFragment;
import kr.co.theresearcher.spirokitfortab.patient_input.PatientInsertActivity;
import kr.co.theresearcher.spirokitfortab.setting.SettingActivity;

public class MainActivity extends AppCompatActivity {

    private ProxyObserver observer = new ProxyObserver();
    private FragmentManager fragmentManager;
    private ImageButton settingButton, insertPatientButton;
    private ImageView bleConnectionImage;
    private FragmentContainerView patientInfoContainer, resultContainer;
    private PatientInformationFragment informationFragment;

    private SpiroKitBluetoothLeService mService;
    private Handler handler = new Handler(Looper.getMainLooper());

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {

            mService = ((SpiroKitBluetoothLeService.LocalBinder)iBinder).getService();

            if (mService.isConnect()) {
                //bleConnectionImage.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.ble_connection)));
                bleConnectionImage.setImageDrawable(AppCompatResources.getDrawable(MainActivity.this, R.drawable.ic_connect_spirokit));
                //macAddressText.setText(SharedPreferencesManager.getBluetoothDeviceMacAddress(SettingActivity.this));
            } else {
                //macAddressText.setText(getString(R.string.state_disconnect));
                //bleConnectionImage.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.gray_light)));
                bleConnectionImage.setImageDrawable(AppCompatResources.getDrawable(MainActivity.this, R.drawable.ic_disconnect_spirokit));
            }

            mService.setBluetoothLeCallback(new SpiroKitBluetoothLeService.BluetoothLeCallback() {
                @Override
                public void onReadCharacteristic(byte[] data) {
                    //testTitleText.setText("READ CHARACTERISTIC");
                }

                @Override
                public void onWriteCharacteristic() {
                    //testTitleText.setText("WRITE CHARACTERISTIC");
                    //bleConnectionImage.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.ble_connection)));
                    bleConnectionImage.setImageDrawable(AppCompatResources.getDrawable(MainActivity.this, R.drawable.ic_connect_spirokit));
                }

                @Override
                public void onDescriptorWrite() {
                    //testTitleText.setText("DESCRIPTOR WRITE");

                    //연결 완료

                }

                @Override
                public void onDiscoverServices() {
                    //testTitleText.setText("DISCOVERED SERVICES");
                }

                @Override
                public void onConnectStateChanged(int status) {
                    if (status == BluetoothProfile.STATE_CONNECTED) {
                        //testTitleText.setText(String.valueOf(status));
                    } else if (status == BluetoothProfile.STATE_DISCONNECTED) {

                        //bleConnectionImage.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.gray_light)));
                        bleConnectionImage.setImageDrawable(AppCompatResources.getDrawable(MainActivity.this, R.drawable.ic_disconnect_spirokit));

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
        setContentView(R.layout.activity_main);

        settingButton = findViewById(R.id.img_btn_setting_main);
        insertPatientButton = findViewById(R.id.img_btn_insert_patient_main);
        bleConnectionImage = findViewById(R.id.img_ble_connection_main);

        patientInfoContainer = findViewById(R.id.fragment_container_patient_info_main);
        resultContainer = findViewById(R.id.fragment_container_result_main);

        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getRealMetrics(displayMetrics);

        float density = getResources().getDisplayMetrics().density;
        float displayWidth = displayMetrics.heightPixels / density;
        float displayHeight = displayMetrics.widthPixels / density;

        Log.d(getClass().getSimpleName(), "Density : " + density + ", Width : " + displayWidth + ", Height : " + displayHeight);

        ViewGroup.LayoutParams layoutParams = (ViewGroup.LayoutParams) patientInfoContainer.getLayoutParams();
        layoutParams.width = (int)(displayHeight / 2f);
        patientInfoContainer.setLayoutParams(layoutParams);


        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });

        insertPatientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PatientInsertActivity.class);
                startActivity(intent);
            }
        });

        informationFragment = new PatientInformationFragment();
        observer.addObserver(informationFragment);
        informationFragment.setHistorySelectedListener(new OnCalHistorySelectedListener() {
            @Override
            public void onHistorySelected(CalHistory history) {

                if (history == null) {
                    EmptyResultFragment emptyResultFragment = new EmptyResultFragment();
                    setFragment(R.id.fragment_container_result_main, emptyResultFragment);
                }
                setFragmentByMeasGroup(history);

            }
        });

        fragmentManager = getSupportFragmentManager();
        EmptyResultFragment emptyResultFragment = new EmptyResultFragment();
        setFragment(R.id.fragment_container_result_main, emptyResultFragment);
        setFragment(R.id.fragment_container_patient_info_main, informationFragment);

        startService(new Intent(getApplicationContext(), SpiroKitBluetoothLeService.class));

    }

    private void setFragment(int id, Fragment fragment) {

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(id, fragment);
        fragmentTransaction.commit();

    }

    private void replaceFragment(int containerID, Fragment fragment) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(containerID, fragment);
        fragmentTransaction.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        stopService(new Intent(getApplicationContext(), SpiroKitBluetoothLeService.class));
    }

    @Override
    protected void onPause() {
        super.onPause();

        unbindService(serviceConnection);
    }

    @Override
    protected void onResume() {
        super.onResume();

        bindService(new Intent(getApplicationContext(), SpiroKitBluetoothLeService.class), serviceConnection ,Context.BIND_AUTO_CREATE);

        /*
        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();
                Looper.prepare();

                historyDatabase database = Room.databaseBuilder(MainActivity.this, historyDatabase.class, RoomNames.ROOM_history_DB_NAME)
                        .build();
                historyDao historyDao = database.historyDao();
                List<history> historys = historyDao.selectByPatientID(SharedPreferencesManager.getPatientId(MainActivity.this));

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        setFragmentByMeasGroup(historys.get(historys.size() - 1));
                    }
                });


                Looper.loop();

            }
        };

        thread.start();

         */

    }

    private void setFragmentByMeasGroup(CalHistory history) {

        if (history == null) return;

        if (history.getMeasDiv().equals("f")) {
            FvcResultFragment fvcResultFragment = new FvcResultFragment(history);
            observer.deleteObservers();
            observer.addObserver(informationFragment);
            observer.addObserver(fvcResultFragment);
            replaceFragment(R.id.fragment_container_result_main, fvcResultFragment);

        } else if (history.getMeasDiv().equals("s")) {
            SvcResultFragment svcResultFragment = new SvcResultFragment(history);
            observer.deleteObservers();
            observer.addObserver(informationFragment);
            observer.addObserver(svcResultFragment);
            replaceFragment(R.id.fragment_container_result_main, svcResultFragment);

        } else if (history.getMeasDiv().equals("m")) {

        } else {

        }

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        if (ev.getAction() == MotionEvent.ACTION_DOWN) {

            if (!(ev.getX() <= patientInfoContainer.getLayoutParams().width)) {
                Log.d(getClass().getSimpleName(), "CHANGED : " + observer.hasChanged());
                observer.notificationObservers(1);
                Log.d(getClass().getSimpleName(), "DOWN EVENT!!!");
            }


        }


        return super.dispatchTouchEvent(ev);
    }


}