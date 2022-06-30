package kr.co.theresearcher.spirokitfortab.setting;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import kr.co.theresearcher.spirokitfortab.R;
import kr.co.theresearcher.spirokitfortab.SharedPreferencesManager;
import kr.co.theresearcher.spirokitfortab.bluetooth.BluetoothAttributes;
import kr.co.theresearcher.spirokitfortab.bluetooth.SpiroKitBluetoothLeService;
import kr.co.theresearcher.spirokitfortab.dialog.ConfirmDialog;
import kr.co.theresearcher.spirokitfortab.dialog.LoadingDialog;
import kr.co.theresearcher.spirokitfortab.dialog.OnSelectedInDialogListener;
import kr.co.theresearcher.spirokitfortab.dialog.SelectionDialog;
import kr.co.theresearcher.spirokitfortab.setting.operator.OperatorActivity;

public class SettingActivity extends AppCompatActivity {

    private ImageButton backButton;
    private FrameLayout startFrameButton;
    private Button logoutButton;
    private ProgressBar scanProgress;
    private CardView operatorCard;
    private RecyclerView rv;
    private BluetoothScanResultsAdapter adapter;
    private TextView connectStateText, startScanText, userNickname;
    private boolean enableAutoPairing, enableSleepMode;

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bluetoothLeScanner;
    private List<ScanFilter> filters;
    private ScanCallback scanCallback;
    private ScanSettings scanSettings;

    private SpiroKitBluetoothLeService mService;
    private LoadingDialog loadingDialog;
    private Handler handler = new Handler(Looper.getMainLooper());

    private Runnable stopScanRunnable = new Runnable() {
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
                connectStateText.setText(getString(R.string.connect_with_what, SharedPreferencesManager.getConnectedDeviceName(SettingActivity.this)));
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
                    if (loadingDialog.isShowing()) loadingDialog.dismiss();
                    connectStateText.setText(getString(R.string.connect_with_what, SharedPreferencesManager.getConnectedDeviceName(SettingActivity.this)));
                    startScanText.setText(getString(R.string.do_disconnect));
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
                        connectStateText.setText(getString(R.string.state_disconnect));
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
                        @Override
                        public void onActivityResult(ActivityResult result) {

                            if (result.getResultCode() == RESULT_OK) {
                                handler.postDelayed(stopScanRunnable, 5000);

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

        operatorCard = findViewById(R.id.card_operator_management);
        operatorCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(SettingActivity.this, OperatorActivity.class);
                startActivity(intent);

            }
        });

        rv = findViewById(R.id.rv_setting);
        adapter = new BluetoothScanResultsAdapter(SettingActivity.this);
        adapter.setLookupListener(new OnDeviceLookupListener() {
            @Override
            public void onDeviceLookup(ScanResult result) {

                SelectionDialog selectionDialog = new SelectionDialog(SettingActivity.this);
                selectionDialog.setTitle(getString(R.string.connect_this_device));
                selectionDialog.setSelectedListener(new OnSelectedInDialogListener() {
                    @Override
                    public void onSelected(boolean select) {

                        if (select) {

                            String macAddress = result.getDevice().getAddress();

                            SharedPreferencesManager.setBluetoothDeviceMacAddress(getApplicationContext(), result.getDevice().getAddress());
                            SharedPreferencesManager.setConnectedDeviceName(getApplicationContext(), result.getDevice().getName());

                            Log.d(getClass().getSimpleName(), macAddress + ", " + mService.getClass().getSimpleName());

                            if (mService != null) {

                                loadingDialog.setTitle(getString(R.string.connecting));
                                loadingDialog.show();
                                mService.connect(macAddress);

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

        loadingDialog = new LoadingDialog(SettingActivity.this);
        loadingDialog.setTitle(getString(R.string.scanning));



        startFrameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                activityResultLauncher.launch(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE));

            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        userNickname.setText(SharedPreferencesManager.getInputNickname(SettingActivity.this));

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
    }

    private boolean checkBLE() {

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, getString(R.string.can_not_ble), Toast.LENGTH_SHORT).show();

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
                        confirmDialog1.setTitle(getString(R.string.fail_already_scanning));
                        confirmDialog1.show();

                        break;

                    case ScanCallback.SCAN_FAILED_APPLICATION_REGISTRATION_FAILED:
                        ConfirmDialog confirmDialog2 = new ConfirmDialog(SettingActivity.this);
                        confirmDialog2.setTitle(getString(R.string.fail_app_registration));
                        confirmDialog2.show();
                        break;


                    case ScanCallback.SCAN_FAILED_FEATURE_UNSUPPORTED:
                        ConfirmDialog confirmDialog3 = new ConfirmDialog(SettingActivity.this);
                        confirmDialog3.setTitle(getString(R.string.fail_feature_unsupported));
                        confirmDialog3.show();
                        break;


                    case ScanCallback.SCAN_FAILED_INTERNAL_ERROR:
                        ConfirmDialog confirmDialog4 = new ConfirmDialog(SettingActivity.this);
                        confirmDialog4.setTitle(getString(R.string.fail_internal_error));
                        confirmDialog4.show();
                        break;


                }

            }
        };

    }

}