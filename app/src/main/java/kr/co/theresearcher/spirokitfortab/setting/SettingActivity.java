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
    private ImageButton syncButton;
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
        @SuppressLint("MissingPermission")
        @Override
        public void run() {

            bluetoothLeScanner.stopScan(scanCallback);
            scanProgress.setVisibility(View.INVISIBLE);
            startScanText.setVisibility(View.VISIBLE);
        }
    };

    VolleyResponseListener volleyResponseListener = new VolleyResponseListener() {
        @Override
        public void onResponse(JSONObject jsonResponse) {

            Log.e(getClass().getSimpleName(),"=+++++++++wwwwwwwwww+++++++++++++++\n" + jsonResponse.toString());
            loadingDialog.dismiss();

            handleResponse(jsonResponse);
        }

        @Override
        public void onError(ErrorResponse errorResponse) {
            Log.e(getClass().getSimpleName(),"+++++++++++++ERROR RESPONSE+++++++++++\n" + errorResponse.getCode() + "\n" + errorResponse.getMessage());
            loadingDialog.dismiss();

            ConfirmDialog confirmDialog = new ConfirmDialog(SettingActivity.this);
            confirmDialog.setTitle(getString(R.string.failed_server_connection, errorResponse.getMessage()));
            confirmDialog.show();
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
        syncButton = findViewById(R.id.img_btn_sync_setting);
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

        nameModifyText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mService.isConnect()) {

                    DeviceNameModifyDialog modifyDialog = new DeviceNameModifyDialog(SettingActivity.this);
                    modifyDialog.setCurrentDeviceName(SharedPreferencesManager.getDeviceName(SettingActivity.this));
                    modifyDialog.setInputListener(new OnTextInputListener() {
                        @Override
                        public void onTextInput(String text) {

                            mService.writeCharacteristic("name" + text);

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

                            SharedPreferencesManager.setDeviceMacAddress(getApplicationContext(), result.getDevice().getAddress());
                            SharedPreferencesManager.setDeviceName(getApplicationContext(), result.getDevice().getName());

                            Log.d(getClass().getSimpleName(), macAddress + ", " + mService.getClass().getSimpleName());

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

        syncButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sync();

            }
        });

        userNickname.setText(SharedPreferencesManager.getOfficeName(SettingActivity.this));

        scanStandBy();

        loadingDialog = new LoadingDialog(SettingActivity.this);


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

    private void sync() {

        loadingDialog = new LoadingDialog(SettingActivity.this);
        loadingDialog.setTitle(getString(R.string.server_sync_waiting));
        loadingDialog.show();

        Thread thread = new Thread() {

            @Override
            public void run() {
                super.run();
                Looper.prepare();

                SpiroKitDatabase database = SpiroKitDatabase.getInstance(SettingActivity.this);
                JSONObject jsonObject = new JSONObject();

                Office office = database.officeDao().selectOfficeByHash(SharedPreferencesManager.getOfficeHash(SettingActivity.this));

                try {

                    jsonObject.put(JsonKeys.JSON_KEY_HASHED, office.getHashed());
                    jsonObject.put(JsonKeys.JSON_KEY_NAME, office.getName());
                    jsonObject.put(JsonKeys.JSON_KEY_TEL, office.getTel());
                    jsonObject.put(JsonKeys.JSON_KEY_ADDRESS, office.getAddress());
                    jsonObject.put(JsonKeys.JSON_KEY_COUNTRY_CODE, office.getCountryCode());
                    jsonObject.put(JsonKeys.JSON_KEY_IS_USE, office.getIsUse());
                    jsonObject.put(JsonKeys.JSON_KEY_IS_USE_SYNC, office.getIsUseSync());
                    jsonObject.put(JsonKeys.JSON_KEY_UPDATED_DATE, office.getUpdateDate());

                    JSONArray operatorArray = new JSONArray();
                    List<Operator> operators = database.operatorDao().selectAll(office.getHashed());

                    Log.e(getClass().getSimpleName(), "OPERATOR SIZE : " + operators.size());

                    for (Operator operator : operators) {
                        JSONObject operatorJsonObject = new JSONObject();
                        operatorJsonObject.put(JsonKeys.JSON_KEY_HASHED, operator.getHashed());
                        operatorJsonObject.put(JsonKeys.JSON_KEY_OFFICE_HASHED, office.getHashed());
                        operatorJsonObject.put(JsonKeys.JSON_KEY_NAME, operator.getName());
                        operatorJsonObject.put(JsonKeys.JSON_KEY_WORK, operator.getWork());
                        operatorJsonObject.put(JsonKeys.JSON_KEY_IS_DELETED, operator.getIsDeleted());
                        operatorJsonObject.put(JsonKeys.JSON_KEY_C_DATE, operator.getCreateTimestamp());
                        operatorJsonObject.put(JsonKeys.JSON_KEY_UPDATED_DATE, operator.getUpdatedDate());

                        operatorArray.put(operatorJsonObject);
                    }
                    operators.clear();

                    jsonObject.put(JsonKeys.JSON_KEY_OPERATOR, operatorArray);

                    List<Patient> patients = database.patientDao().selectAll(office.getHashed());

                    Log.e(getClass().getSimpleName(), "PATIENT SIZE : " + patients.size());

                    JSONArray patientJsonArray = new JSONArray();

                    for (Patient patient : patients) {

                        String start = patient.getStartSmokingDay();

                        JSONObject patientJsonObject = new JSONObject();
                        patientJsonObject.put(JsonKeys.JSON_KEY_HASHED, patient.getHashed());
                        patientJsonObject.put(JsonKeys.JSON_KEY_OFFICE_HASHED, patient.getOfficeHashed());
                        patientJsonObject.put(JsonKeys.JSON_KEY_CHART_NO, patient.getChartNumber());
                        patientJsonObject.put(JsonKeys.JSON_KEY_NAME, patient.getName());
                        patientJsonObject.put(JsonKeys.JSON_KEY_GENDER, patient.getGender());
                        patientJsonObject.put(JsonKeys.JSON_KEY_HEIGHT, patient.getHeight());
                        patientJsonObject.put(JsonKeys.JSON_KEY_WEIGHT, patient.getWeight());
                        patientJsonObject.put(JsonKeys.JSON_KEY_BIRTHDAY, patient.getBirthDay());
                        patientJsonObject.put(JsonKeys.JSON_KEY_HUMAN_RACE, patient.getHumanRace());
                        patientJsonObject.put(JsonKeys.JSON_KEY_C_DATE, patient.getCreateDate());
                        patientJsonObject.put(JsonKeys.JSON_KEY_UPDATED_DATE, patient.getUpdatedDate());

                        if (start == null) {

                            patientJsonObject.put(JsonKeys.JSON_KEY_SMOKING_AMOUNT, null);
                            patientJsonObject.put(JsonKeys.JSON_KEY_SMOKING_PERIOD, null);
                            patientJsonObject.put(JsonKeys.JSON_KEY_SMOKING_IS_NOW, null);
                        } else {
                            patientJsonObject.put(JsonKeys.JSON_KEY_SMOKING_AMOUNT, patient.getSmokingAmountPerDay());
                            patientJsonObject.put(JsonKeys.JSON_KEY_SMOKING_PERIOD, patient.getSmokingPeriod());
                            patientJsonObject.put(JsonKeys.JSON_KEY_SMOKING_IS_NOW, patient.getNowSmoking());
                        }

                        patientJsonObject.put(JsonKeys.JSON_KEY_STOP_SMOKING_WHEN, patient.getStopSmokingDay());
                        patientJsonObject.put(JsonKeys.JSON_KEY_START_SMOKING_WHEN, patient.getStartSmokingDay());
                        patientJsonObject.put(JsonKeys.JSON_KEY_FROM_OS, patient.getOs());
                        patientJsonObject.put(JsonKeys.JSON_KEY_LATEST_DAY, null);
                        patientJsonObject.put(JsonKeys.JSON_KEY_IS_DELETED, patient.getIsDeleted());

                        patientJsonArray.put(patientJsonObject);

                    }
                    jsonObject.put(JsonKeys.JSON_KEY_PATIENT, patientJsonArray);

                    patients.clear();

                    List<CalHistory> calHistories = database.calHistoryDao().selectAll(office.getHashed());
                    List<CalHistoryRawData> rawDataList = new ArrayList<>();

                    Log.e(getClass().getSimpleName(), "HISTORY SIZE : " + calHistories.size());

                    JSONArray historyArray = new JSONArray();
                    JSONArray rawDataArray = new JSONArray();

                    for (CalHistory calHistory : calHistories) {

                        JSONObject historyJsonObject = new JSONObject();
                        historyJsonObject.put(JsonKeys.JSON_KEY_HASHED, calHistory.getHashed());
                        historyJsonObject.put(JsonKeys.JSON_KEY_OFFICE_HASHED, office.getHashed());
                        historyJsonObject.put(JsonKeys.JSON_KEY_OPERATOR_DOCTOR_HASH, calHistory.getFamilyDoctorHash());
                        historyJsonObject.put(JsonKeys.JSON_KEY_OPERATOR_HASH, calHistory.getOperatorHashed());
                        historyJsonObject.put(JsonKeys.JSON_KEY_PATIENT_HASH, calHistory.getPatientHashed());
                        historyJsonObject.put(JsonKeys.JSON_KEY_CAL_HISTORY_DATE, calHistory.getFinishDate());
                        historyJsonObject.put(JsonKeys.JSON_KEY_CAL_DIV, calHistory.getMeasDiv());
                        historyJsonObject.put(JsonKeys.JSON_KEY_DEVICE_DIV, calHistory.getDeviceDiv());
                        historyJsonObject.put(JsonKeys.JSON_KEY_IS_DELETED_REF, calHistory.getIsDeletedReference());
                        historyJsonObject.put(JsonKeys.JSON_KEY_IS_DELETED, calHistory.getIsDeleted());
                        historyJsonObject.put(JsonKeys.JSON_KEY_C_DATE, calHistory.getCreateDate());
                        historyJsonObject.put(JsonKeys.JSON_KEY_UPDATED_DATE, calHistory.getUpdatedDate());

                        historyArray.put(historyJsonObject);

                        List<CalHistoryRawData> rawData = database.calHistoryRawDataDao().selectAll(calHistory.getHashed());

                        Log.e(getClass().getSimpleName(), "RAW SIZE : " + rawData.size());

                        rawDataList.addAll(rawData);

                    }

                    calHistories.clear();

                    jsonObject.put(JsonKeys.JSON_KEY_CAL_HISTORY, historyArray);

                    Log.e(getClass().getSimpleName(), "RAW TOTAL SIZE : " + rawDataList.size());

                    for (CalHistoryRawData raw : rawDataList) {

                        JSONObject rawJsonObject = new JSONObject();
                        rawJsonObject.put(JsonKeys.JSON_KEY_HASHED, raw.getHashed());
                        rawJsonObject.put(JsonKeys.JSON_KEY_HISTORY_HASH, raw.getCalHistoryHashed());
                        rawJsonObject.put(JsonKeys.JSON_KEY_DATA, raw.getData());
                        rawJsonObject.put(JsonKeys.JSON_KEY_CAL_DATE, raw.getCalDate());
                        rawJsonObject.put(JsonKeys.JSON_KEY_IS_POST, raw.getIsPost());
                        rawJsonObject.put(JsonKeys.JSON_KEY_IS_DELETED_REF, raw.getIsDeletedReference());
                        rawJsonObject.put(JsonKeys.JSON_KEY_IS_DELETED, raw.getIsDeleted());
                        rawJsonObject.put(JsonKeys.JSON_KEY_ORDER, raw.getOrderNumber());
                        rawJsonObject.put(JsonKeys.JSON_KEY_C_DATE, raw.getCreateDate());
                        rawJsonObject.put(JsonKeys.JSON_KEY_UPDATED_DATE, raw.getUpdatedDate());

                        rawDataArray.put(rawJsonObject);

                    }

                    SpiroKitDatabase.removeInstance();

                    rawDataList.clear();

                    jsonObject.put(JsonKeys.JSON_KEY_RAW_DATA, rawDataArray);


                    //Log.e(getClass().getSimpleName(), "JSON LENGTH : " + jsonObject.toString().length());
                    //POST
                    SpiroKitVolley.setVolleyListener(volleyResponseListener);
                    SpiroKitVolley.postJson(jsonObject);

                } catch (JSONException e) {

                    Log.e(getClass().getSimpleName(), "++++++++++++++++++++++++++=\n" + e.toString());

                }

                Looper.loop();
            }
        };

        thread.start();

    }

    private void handleResponse(JSONObject jsonObject) {

        loadingDialog = new LoadingDialog(SettingActivity.this);
        loadingDialog.setTitle(getString(R.string.server_sync_writing));
        loadingDialog.show();

        Thread thread = new Thread() {

            @Override
            public void run() {
                super.run();
                Looper.prepare();

                SpiroKitDatabase database = SpiroKitDatabase.getInstance(SettingActivity.this);
                //office 하나만 삭제하면 CASCADE 로 연쇄삭제됨.
                database.officeDao().removeOffice(SharedPreferencesManager.getOfficeHash(SettingActivity.this));

                try {

                    int resultCode = jsonObject.getInt(JsonKeys.JSON_KEY_RESULT);
                    String instance = jsonObject.getString(JsonKeys.JSON_KEY_INSTANCE);

                    Log.e(getClass().getSimpleName(), "RESULT : " + resultCode);

                    JSONObject items = jsonObject.getJSONObject(JsonKeys.JSON_KEY_ITEMS);
                    Log.e(getClass().getSimpleName(), "RESPONSE JSON SIZE : " + items.length());

                    JSONObject office = items.getJSONObject(JsonKeys.JSON_KEY_OFFICE);
                    Office o = new Office();
                    o.setHashed(office.getString(JsonKeys.JSON_KEY_HASHED));
                    o.setName(office.getString(JsonKeys.JSON_KEY_NAME));
                    o.setCode(office.getString(JsonKeys.JSON_KEY_CODE));
                    o.setTel(office.getString(JsonKeys.JSON_KEY_TEL));
                    o.setAddress(office.getString(JsonKeys.JSON_KEY_ADDRESS));
                    o.setCountryCode(office.getString(JsonKeys.JSON_KEY_COUNTRY_CODE));
                    o.setIsUse(office.getInt(JsonKeys.JSON_KEY_IS_USE));
                    o.setIsUseSync(office.getInt(JsonKeys.JSON_KEY_IS_USE_SYNC));
                    o.setOfficeID(office.getString(JsonKeys.JSON_KEY_OFFICE_ID));
                    o.setIsDeleted(office.getInt(JsonKeys.JSON_KEY_IS_DELETED));
                    o.setOfficePassword(SharedPreferencesManager.getOfficePass(SettingActivity.this));
                    o.setUpdateDate(office.getString(JsonKeys.JSON_KEY_UPDATED_DATE));

                    database.officeDao().insert(o);

                    JSONArray operators = items.getJSONArray(JsonKeys.JSON_KEY_OPERATORS);
                    //for (JSONObject operatorObject : operators.)

                    for (int i = 0; i < operators.length(); i++) {

                        JSONObject operator = operators.getJSONObject(i);
                        Operator op = new Operator();
                        op.setHashed(operator.getString(JsonKeys.JSON_KEY_HASHED));
                        op.setOfficeHashed(operator.getString(JsonKeys.JSON_KEY_OFFICE_HASHED));
                        op.setName(operator.getString(JsonKeys.JSON_KEY_NAME));
                        op.setWork(operator.getString(JsonKeys.JSON_KEY_WORK));
                        op.setIsDeleted(operator.getInt(JsonKeys.JSON_KEY_IS_DELETED));
                        op.setCreateTimestamp(operator.getString(JsonKeys.JSON_KEY_C_DATE));
                        op.setUpdatedDate(operator.getString(JsonKeys.JSON_KEY_UPDATED_DATE));
                        //c_time 패스

                        database.operatorDao().insertOperator(op);
                    }

                    JSONArray patients = items.getJSONArray(JsonKeys.JSON_KEY_PATIENTS);

                    for (int i = 0; i < patients.length(); i++) {

                        JSONObject patient = patients.getJSONObject(i);
                        Patient p = new Patient.Builder()
                                .hashed(patient.getString(JsonKeys.JSON_KEY_HASHED))
                                .officeHashed(patient.getString(JsonKeys.JSON_KEY_OFFICE_HASHED))
                                .chartNumber(patient.getString(JsonKeys.JSON_KEY_CHART_NO))
                                .name(patient.getString(JsonKeys.JSON_KEY_NAME))
                                .gender(patient.getString(JsonKeys.JSON_KEY_GENDER))
                                .height(patient.getInt(JsonKeys.JSON_KEY_HEIGHT))
                                .weight(patient.getInt(JsonKeys.JSON_KEY_WEIGHT))
                                .birthDay(patient.getString(JsonKeys.JSON_KEY_BIRTHDAY))
                                .humanRace(patient.getString(JsonKeys.JSON_KEY_HUMAN_RACE))
                                .createDate(patient.getString(JsonKeys.JSON_KEY_C_DATE))
                                .updatedDate(patient.getString(JsonKeys.JSON_KEY_UPDATED_DATE))
                                //os 기본 a
                                .build();



                        String start = patient.getString(JsonKeys.JSON_KEY_START_SMOKING_WHEN);
                        String stop = patient.getString(JsonKeys.JSON_KEY_STOP_SMOKING_WHEN);
                        String amount = patient.getString(JsonKeys.JSON_KEY_SMOKING_AMOUNT);


                        //Log.e(getClass().getSimpleName(), "start : " + start + ", stop : " + stop + ", " + "now : " + amount);

                        if (start.contains("0000-00-00")) {
                            p.setStartSmokingDay(null);
                            p.setSmokingAmountPerDay("0");
                            p.setStopSmokingDay(null);
                            p.setNowSmoking(0);
                            p.setSmokingPeriod(0);

                        } else {
                            p.setStartSmokingDay(start);
                            p.setSmokingAmountPerDay(amount);

                            if (stop.contains("0000-00-00")) {
                                p.setStopSmokingDay(null);
                                p.setNowSmoking(1);
                                p.setSmokingPeriod(0);

                            } else {
                                p.setStopSmokingDay(stop);
                                p.setNowSmoking(0);
                                p.setSmokingPeriod(0);

                            }
                        }

                        p.setIsDeleted(patient.getInt(JsonKeys.JSON_KEY_IS_DELETED));

                        database.patientDao().insertPatient(p);
                    }

                    JSONArray histories = items.getJSONArray(JsonKeys.JSON_KEY_HISTORIES);

                    for (int i = 0; i < histories.length(); i++) {

                        JSONObject history = histories.getJSONObject(i);

                        CalHistory h = new CalHistory(
                                history.getString(JsonKeys.JSON_KEY_HASHED),
                                history.getString(JsonKeys.JSON_KEY_OFFICE_HASHED),
                                history.getString(JsonKeys.JSON_KEY_OPERATOR_HASH),
                                history.getString(JsonKeys.JSON_KEY_PATIENT_HASH),
                                history.getString(JsonKeys.JSON_KEY_CAL_HISTORY_DATE),
                                history.getString(JsonKeys.JSON_KEY_CAL_DIV),
                                history.getString(JsonKeys.JSON_KEY_DEVICE_DIV),
                                history.getInt(JsonKeys.JSON_KEY_IS_DELETED)
                        );

                        h.setCreateDate(history.getString(JsonKeys.JSON_KEY_C_DATE));
                        h.setUpdatedDate(history.getString(JsonKeys.JSON_KEY_UPDATED_DATE));
                        h.setFamilyDoctorHash(history.getString(JsonKeys.JSON_KEY_OPERATOR_DOCTOR_HASH));
                        h.setIsDeletedReference(history.getInt(JsonKeys.JSON_KEY_IS_DELETED_REF));

                        database.calHistoryDao().insertHistory(h);

                    }

                    JSONArray rawData = items.getJSONArray(JsonKeys.JSON_KEY_RAW_DATA_LIST);

                    for (int i = 0; i < rawData.length(); i++) {

                        JSONObject data = rawData.getJSONObject(i);

                        CalHistoryRawData d = new CalHistoryRawData(
                                data.getString(JsonKeys.JSON_KEY_HASHED),
                                data.getString(JsonKeys.JSON_KEY_HISTORY_HASH),
                                data.getString(JsonKeys.JSON_KEY_ORDER),
                                data.getString(JsonKeys.JSON_KEY_DATA),
                                data.getString(JsonKeys.JSON_KEY_CAL_DATE),
                                data.getInt(JsonKeys.JSON_KEY_IS_POST)
                        );

                        d.setCreateDate(data.getString(JsonKeys.JSON_KEY_C_DATE));
                        d.setUpdatedDate(data.getString(JsonKeys.JSON_KEY_UPDATED_DATE));
                        d.setIsDeleted(data.getInt(JsonKeys.JSON_KEY_IS_DELETED));
                        d.setIsDeletedReference(data.getInt(JsonKeys.JSON_KEY_IS_DELETED_REF));

                        database.calHistoryRawDataDao().insertRawData(d);

                    }

                    SpiroKitDatabase.removeInstance();
                    items = null;

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            loadingDialog.dismiss();

                            ConfirmDialog confirmDialog = new ConfirmDialog(SettingActivity.this);
                            confirmDialog.setTitle(getString(R.string.success_sync));
                            confirmDialog.show();
                        }
                    });

                } catch (JSONException e) {
                    Log.e(getClass().getSimpleName(), e.toString());
                    for (StackTraceElement stack : e.getStackTrace()) {
                        Log.e(getClass().getSimpleName(), stack.toString());
                    }

                }



                Looper.loop();
            }
        };

        thread.start();

    }

}