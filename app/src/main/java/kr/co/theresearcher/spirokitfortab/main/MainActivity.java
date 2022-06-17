package kr.co.theresearcher.spirokitfortab.main;

import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.room.Room;

import android.bluetooth.BluetoothProfile;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import kr.co.theresearcher.spirokitfortab.R;
import kr.co.theresearcher.spirokitfortab.SharedPreferencesManager;
import kr.co.theresearcher.spirokitfortab.bluetooth.SpiroKitBluetoothLeService;
import kr.co.theresearcher.spirokitfortab.db.RoomNames;
import kr.co.theresearcher.spirokitfortab.db.patient.Patient;
import kr.co.theresearcher.spirokitfortab.db.patient.PatientDao;
import kr.co.theresearcher.spirokitfortab.db.patient.PatientDatabase;
import kr.co.theresearcher.spirokitfortab.main.information.PatientInformationFragment;
import kr.co.theresearcher.spirokitfortab.main.patients.PatientsFragment;
import kr.co.theresearcher.spirokitfortab.main.result.fvc.FvcResultFragment;
import kr.co.theresearcher.spirokitfortab.patient_input.PatientInsertActivity;
import kr.co.theresearcher.spirokitfortab.setting.SettingActivity;

public class MainActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;
    private ImageButton settingButton, insertPatientButton;
    private Button measButton;
    private ImageView bleConnectionImage;

    private SpiroKitBluetoothLeService mService;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {

            mService = ((SpiroKitBluetoothLeService.LocalBinder)iBinder).getService();

            if (mService.isConnect()) {
                bleConnectionImage.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.ble_connection)));
                bleConnectionImage.setImageDrawable(AppCompatResources.getDrawable(MainActivity.this, R.drawable.ic_baseline_bluetooth_30_white));
                //macAddressText.setText(SharedPreferencesManager.getBluetoothDeviceMacAddress(SettingActivity.this));
            } else {
                //macAddressText.setText(getString(R.string.state_disconnect));
                bleConnectionImage.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.gray_light)));
                bleConnectionImage.setImageDrawable(AppCompatResources.getDrawable(MainActivity.this, R.drawable.ic_baseline_bluetooth_30_black));
            }

            mService.setBluetoothLeCallback(new SpiroKitBluetoothLeService.BluetoothLeCallback() {
                @Override
                public void onReadCharacteristic(byte[] data) {
                    //testTitleText.setText("READ CHARACTERISTIC");
                }

                @Override
                public void onWriteCharacteristic() {
                    //testTitleText.setText("WRITE CHARACTERISTIC");
                    bleConnectionImage.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.ble_connection)));
                    bleConnectionImage.setImageDrawable(AppCompatResources.getDrawable(MainActivity.this, R.drawable.ic_baseline_bluetooth_30_white));
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

                        bleConnectionImage.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.gray_light)));
                        bleConnectionImage.setImageDrawable(AppCompatResources.getDrawable(MainActivity.this, R.drawable.ic_baseline_bluetooth_30_black));

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

        fragmentManager = getSupportFragmentManager();

        setFragment(R.id.fragment_container_patient_info_main, new PatientInformationFragment());
        setFragment(R.id.fragment_container_result_main, new FvcResultFragment());

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

        startService(new Intent(getApplicationContext(), SpiroKitBluetoothLeService.class));

    }

    private void setFragment(int id, Fragment fragment) {

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(id, fragment);
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


    }
}