package kr.co.theresearcher.spirokitfortab.setting;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.sql.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import kr.co.theresearcher.spirokitfortab.R;
import kr.co.theresearcher.spirokitfortab.SharedPreferencesManager;
import kr.co.theresearcher.spirokitfortab.bluetooth.BluetoothAttributes;
import kr.co.theresearcher.spirokitfortab.bluetooth.SpiroKitBluetoothLeService;
import kr.co.theresearcher.spirokitfortab.db.SpiroKitDatabase;
import kr.co.theresearcher.spirokitfortab.db.cal_history.CalHistory;
import kr.co.theresearcher.spirokitfortab.db.cal_history_raw_data.CalHistoryRawData;
import kr.co.theresearcher.spirokitfortab.db.office.Office;
import kr.co.theresearcher.spirokitfortab.db.operator.Operator;
import kr.co.theresearcher.spirokitfortab.db.patient.Patient;
import kr.co.theresearcher.spirokitfortab.dialog.ConfirmDialog;
import kr.co.theresearcher.spirokitfortab.dialog.DeviceNameModifyDialog;
import kr.co.theresearcher.spirokitfortab.dialog.LoadingDialog;
import kr.co.theresearcher.spirokitfortab.dialog.OnSelectedInDialogListener;
import kr.co.theresearcher.spirokitfortab.dialog.OnTextInputListener;
import kr.co.theresearcher.spirokitfortab.dialog.SelectionDialog;
import kr.co.theresearcher.spirokitfortab.setting.operator.OperatorActivity;
import kr.co.theresearcher.spirokitfortab.volley.ErrorResponse;
import kr.co.theresearcher.spirokitfortab.volley.JsonKeys;
import kr.co.theresearcher.spirokitfortab.volley.SpiroKitVolley;
import kr.co.theresearcher.spirokitfortab.volley.VolleyResponseListener;

public class SettingActivity extends AppCompatActivity {

    private ImageButton backButton;
    private FrameLayout startFrameButton;
    private Button logoutButton;
    private ProgressBar scanProgress;
    private CardView operatorCard;
    private RecyclerView rv;
    private BluetoothScanResultsAdapter adapter;
    private TextView connectStateText, startScanText, userNickname, nameModifyText;
    private ImageButton syncEnableButton;
    private boolean enableAutoPairing, enableSleepMode;

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bluetoothLeScanner;
    private List<ScanFilter> filters;
    private ScanCallback scanCallback;
    private ScanSettings scanSettings;

    private SpiroKitBluetoothLeService mService;


    private Handler handler = new Handler(Looper.getMainLooper());

    private Runnable stopScanRunnable = new Runnable() {
        @SuppressLint("MissingPermission")
        @Override
        public void run() {

            bluetoothLeScanner.stopScan(scanCallback);
            scanProgress.setVisibility(View.INVISIBLE);
            startScanText.setVisibility(View.VISIBLE);
        }
    };



    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {

            mService = ((SpiroKitBluetoothLeService.LocalBinder)iBinder).getService();

            if (mService.isConnect()) {
                Log.d(getClass().getSimpleName(), "*******************isConnect");
                connectStateText.setText(getString(R.string.connect_with_what, SharedPreferencesManager.getDeviceName(SettingActivity.this)));
                startScanText.setText(getString(R.string.do_disconnect));
            } else {
                Log.d(getClass().getSimpleName(), "*******************is NOT Connect");
               connectStateText.setText(getString(R.string.state_disconnect));
            }

            mService.setBluetoothLeCallback(new SpiroKitBluetoothLeService.BluetoothLeCallback() {
                @Override
                public void onReadCharacteristic(byte[] data) {
                    //testTitleText.setText("READ CHARACTERISTIC");
                }

                @Override
                public void onWriteCharacteristic() {
                    //testTitleText.setText("WRITE CHARACTERISTIC");
                }

                @Override
                public void onDescriptorWrite() {
                    //testTitleText.setText("DESCRIPTOR WRITE");

                    //연결 완료
                    Log.d(getClass().getSimpleName(), "**********onDescriptorWrite");
                    //if (loadingDialog.isShowing()) loadingDialog.dismiss();

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            connectStateText.setText(getString(R.string.connect_with_what, SharedPreferencesManager.getDeviceName(SettingActivity.this)));
                            startScanText.setText(getString(R.string.do_disconnect));
                            nameModifyText.setVisibility(View.VISIBLE);

                        }
                    });

                    //macAddressText.setText(SharedPreferencesManager.getBluetoothDeviceMacAddress(SettingActivity.this));
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
                                connectStateText.setText(getString(R.string.state_disconnect));
                                startScanText.setText(getString(R.string.scan));
                                nameModifyText.setVisibility(View.INVISIBLE);
                            }
                        });


                    }
                }
            });

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {



        }
    };

    private ActivityResultLauncher<Intent> activityResultLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @SuppressLint("MissingPermission")
                        @Override
                        public void onActivityResult(ActivityResult result) {

                            if (result.getResultCode() == RESULT_OK) {
                                handler.postDelayed(stopScanRunnable, 5000);

                                scanStandBy();
                                bluetoothLeScanner.startScan(filters, scanSettings, scanCallback);
                                startScanText.setVisibility(View.INVISIBLE);
                                scanProgress.setVisibility(View.VISIBLE);

                            } else {

                                ConfirmDialog confirmDialog = new ConfirmDialog(SettingActivity.this);
                                confirmDialog.setTitle(getString(R.string.must_be_enable_ble));
                                confirmDialog.show();

                            }

                        }
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        userNickname = findViewById(R.id.tv_user_nickname_setting);
        startFrameButton = findViewById(R.id.frame_btn_start_scan_setting);
        connectStateText = findViewById(R.id.tv_spirokit_connect_state_setting);
        backButton = findViewById(R.id.img_btn_back_setting);
        scanProgress = findViewById(R.id.progress_scan_loading);
        startScanText = findViewById(R.id.tv_start_scan);
        syncEnableButton = findViewById(R.id.img_btn_sync_setting);
        nameModifyText = findViewById(R.id.tv_device_name_modify_setting);
        logoutButton = findViewById(R.id.btn_logout_setting);
        operatorCard = findViewById(R.id.card_operator_management);

        userNickname.setText(SharedPreferencesManager.getOfficeName(SettingActivity.this));


        operatorCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(SettingActivity.this, OperatorActivity.class);
                startActivity(intent);

            }
        });

        syncEnableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean enabled = SharedPreferencesManager.getUseSync(SettingActivity.this);
                Log.e(getClass().getSimpleName(), enabled + "");

                SelectionDialog selectionDialog = new SelectionDialog(SettingActivity.this);

                if (enabled) {

                    selectionDialog.setTitle(getString(R.string.agree_disable_sync));

                } else {

                    selectionDialog.setTitle(getString(R.string.agree_enable_sync));

                }

                selectionDialog.setSelectedListener(new OnSelectedInDialogListener() {
                    @Override
                    public void onSelected(boolean select) {
                        SharedPreferencesManager.setUseSync(SettingActivity.this, !enabled);
                        syncEnableButton.setSelected(!enabled);
                    }
                });

                selectionDialog.show();



            }
        });

        nameModifyText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mService.isConnect()) {

                    DeviceNameModifyDialog modifyDialog = new DeviceNameModifyDialog(SettingActivity.this);
                    modifyDialog.setCurrentDeviceName(SharedPreferencesManager.getDeviceName(SettingActivity.this));
                    modifyDialog.setInputListener(new OnTextInputListener() {
                        @Override
                        public void onTextInput(String text) {

                            Log.e(getClass().getSimpleName(), "NAME CHANGE : " + text);

                            mService.writeCharacteristic("blen" + text);

                            ConfirmDialog confirmDialog = new ConfirmDialog(SettingActivity.this);
                            confirmDialog.setTitle(getString(R.string.info_modify_name));
                            confirmDialog.show();

                        }
                    });
                    modifyDialog.show();

                    //이름변경 다이얼로그 띄우기
                    //다이얼로그에서 숫자 입력받고 확인 누르면 그 값을 리스너로 받아서 서비스로 보내기
                    //mService.writeCharacteristic("name00");

                }

            }
        });

        rv = findViewById(R.id.rv_setting);
        adapter = new BluetoothScanResultsAdapter(SettingActivity.this);
        adapter.setLookupListener(new OnDeviceLookupListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onDeviceLookup(ScanResult result) {

                SelectionDialog selectionDialog = new SelectionDialog(SettingActivity.this);
                selectionDialog.setTitle(getString(R.string.question_connect, result.getDevice().getName()));
                selectionDialog.setSelectedListener(new OnSelectedInDialogListener() {
                    @Override
                    public void onSelected(boolean select) {

                        if (select) {

                            String macAddress = result.getDevice().getAddress();
                            String name = result.getScanRecord().getDeviceName();

                            SharedPreferencesManager.setDeviceMacAddress(getApplicationContext(), macAddress);
                            SharedPreferencesManager.setDeviceName(getApplicationContext(), name);

                            Log.e(getClass().getSimpleName(), "Device.name : " + result.getDevice().getName() + ", ScanRecord.name : " + name);
                            Log.e(getClass().getSimpleName(), "MacAddress : " + macAddress);

                            if (mService != null) {

                                //loadingDialog.setTitle(getString(R.string.connecting));
                                //loadingDialog.show();
                                mService.connect(macAddress);
                                adapter.clear();


                            }

                        } else {

                        }

                    }
                });

                selectionDialog.show();

            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(SettingActivity.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(linearLayoutManager);
        rv.setAdapter(adapter);

        startFrameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mService.isConnect()) {
                    mService.disconnect();

                } else {
                    activityResultLauncher.launch(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE));
                }


            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
                //프리퍼런스 데이터를 user id 를 -1 처럼 만들고
                //설정화면을 닫은 뒤 메인화면이 onResume 되는데, ID 가 -1이면 닫고 로그인화면으로 이동해도 좋음.

            }
        });


        userNickname.setText(SharedPreferencesManager.getOfficeName(SettingActivity.this));

        scanStandBy();



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

        SpiroKitDatabase db = SpiroKitDatabase.getInstance(SettingActivity.this);
        Office office = db.officeDao().selectOfficeByHash(SharedPreferencesManager.getOfficeHash(SettingActivity.this));
        if (office.getIsUseSync() == 0) {
            syncEnableButton.setEnabled(false);
        } else {
            syncEnableButton.setSelected(SharedPreferencesManager.getUseSync(SettingActivity.this));
        }


    }

    private boolean checkBLE() {

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            //Toast.makeText(this, getString(R.string.can_not_ble), Toast.LENGTH_SHORT).show();

            return false;
        }
        return true;
    }

    private void scanStandBy() {

        if (!checkBLE()) {
            return;
        }

        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();

        ScanFilter scanFilter = new ScanFilter.Builder()
                //.setDeviceAddress(MAC_ADDRESS)
                //.setDeviceName(BluetoothAttributes.BLU
                //.setServiceUuid(ParcelUuid.fromString(BluetoothAttributes.BLUETOOTH_LE_SERVICE_UUID))
                .build();

        scanSettings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
                .build();

        filters = new ArrayList<>();
        filters.add(scanFilter);

        scanCallback = new ScanCallback() {
            @SuppressLint("MissingPermission")
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                super.onScanResult(callbackType, result);
                //조건에 해당하는 기기를 찾음.

                String name = result.getDevice().getName();

                if (name == null) return;

                adapter.addResult(result);

            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {

                for (ScanResult r : results) {
                    adapter.addResult(r);
                }

            }

            @Override
            public void onScanFailed(int errorCode) {

                switch (errorCode) {

                    case ScanCallback.SCAN_FAILED_ALREADY_STARTED:

                        ConfirmDialog confirmDialog1 = new ConfirmDialog(SettingActivity.this);
                        confirmDialog1.setTitle(getString(R.string.already_scanning));
                        confirmDialog1.show();

                        break;

                    case ScanCallback.SCAN_FAILED_APPLICATION_REGISTRATION_FAILED:
                        ConfirmDialog confirmDialog2 = new ConfirmDialog(SettingActivity.this);
                        confirmDialog2.setTitle(getString(R.string.not_available_ble));
                        confirmDialog2.show();
                        break;


                    case ScanCallback.SCAN_FAILED_FEATURE_UNSUPPORTED:
                        ConfirmDialog confirmDialog3 = new ConfirmDialog(SettingActivity.this);
                        confirmDialog3.setTitle(getString(R.string.not_available_ble));
                        confirmDialog3.show();
                        break;


                    case ScanCallback.SCAN_FAILED_INTERNAL_ERROR:
                        ConfirmDialog confirmDialog4 = new ConfirmDialog(SettingActivity.this);
                        confirmDialog4.setTitle(getString(R.string.not_available_ble));
                        confirmDialog4.show();
                        break;


                }

            }
        };

    }



}