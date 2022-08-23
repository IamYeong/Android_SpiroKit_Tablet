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
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Point;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import kr.co.theresearcher.spirokitfortab.OnItemChangedListener;
import kr.co.theresearcher.spirokitfortab.R;
import kr.co.theresearcher.spirokitfortab.SharedPreferencesManager;
import kr.co.theresearcher.spirokitfortab.bluetooth.SpiroKitBluetoothLeService;
import kr.co.theresearcher.spirokitfortab.db.SpiroKitDatabase;
import kr.co.theresearcher.spirokitfortab.db.cal_history.CalHistory;
import kr.co.theresearcher.spirokitfortab.db.cal_history_raw_data.CalHistoryRawData;
import kr.co.theresearcher.spirokitfortab.db.office.Office;
import kr.co.theresearcher.spirokitfortab.db.operator.Operator;
import kr.co.theresearcher.spirokitfortab.db.patient.Patient;
import kr.co.theresearcher.spirokitfortab.dialog.ConfirmDialog;
import kr.co.theresearcher.spirokitfortab.dialog.LoadingDialog;
import kr.co.theresearcher.spirokitfortab.dialog.SyncLoadingDialog;
import kr.co.theresearcher.spirokitfortab.login.LoginActivity;
import kr.co.theresearcher.spirokitfortab.main.information.OnCalHistorySelectedListener;
import kr.co.theresearcher.spirokitfortab.main.information.PatientInformationFragment;
import kr.co.theresearcher.spirokitfortab.main.result.empty.EmptyResultFragment;
import kr.co.theresearcher.spirokitfortab.main.result.fvc.FvcResultFragment;
import kr.co.theresearcher.spirokitfortab.main.result.svc.SvcResultFragment;
import kr.co.theresearcher.spirokitfortab.patient_input.PatientInsertActivity;
import kr.co.theresearcher.spirokitfortab.setting.SettingActivity;
import kr.co.theresearcher.spirokitfortab.volley.ErrorResponse;
import kr.co.theresearcher.spirokitfortab.volley.JsonKeys;
import kr.co.theresearcher.spirokitfortab.volley.SpiroKitVolley;
import kr.co.theresearcher.spirokitfortab.volley.VolleyResponseListener;

public class MainActivity extends AppCompatActivity {

    private ProxyObserver observer = new ProxyObserver();
    private FragmentManager fragmentManager;
    private ImageButton settingButton, insertPatientButton, syncButton;
    private ImageView bleConnectionImage;
    private FragmentContainerView patientInfoContainer, resultContainer;
    private PatientInformationFragment informationFragment;

    private LoadingDialog loadingDialog;
    private SyncLoadingDialog syncLoadingDialog;

    private SpiroKitBluetoothLeService mService;
    private Handler handler = new Handler(Looper.getMainLooper());

    private VolleyResponseListener volleyResponseListener = new VolleyResponseListener() {
        @Override
        public void onResponse(JSONObject jsonResponse) {

            Log.e(getClass().getSimpleName(),"=+++++++++wwwwwwwwww+++++++++++++++\n" + jsonResponse.toString());
            syncLoadingDialog.dismiss();

            handleResponse(jsonResponse);
        }

        @Override
        public void onError(ErrorResponse errorResponse) {
            Log.e(getClass().getSimpleName(),"+++++++++++++ERROR RESPONSE+++++++++++\n" + errorResponse.getCode() + "\n" + errorResponse.getMessage());
            syncLoadingDialog.dismiss();

            ConfirmDialog confirmDialog = new ConfirmDialog(MainActivity.this);
            confirmDialog.setTitle(getString(R.string.fail_server_response, errorResponse.getMessage()));
            confirmDialog.show();
        }
    };

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {

            mService = ((SpiroKitBluetoothLeService.LocalBinder)iBinder).getService();

            if (mService.isConnect()) {
                //bleConnectionImage.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.ble_connection)));
                bleConnectionImage.setImageDrawable(AppCompatResources.getDrawable(MainActivity.this, R.drawable.connected_device));
                //macAddressText.setText(SharedPreferencesManager.getBluetoothDeviceMacAddress(SettingActivity.this));
            } else {
                //macAddressText.setText(getString(R.string.state_disconnect));
                //bleConnectionImage.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.gray_light)));
                bleConnectionImage.setImageDrawable(AppCompatResources.getDrawable(MainActivity.this, R.drawable.disconnected_device));
            }

            mService.setBluetoothLeCallback(new SpiroKitBluetoothLeService.BluetoothLeCallback() {
                @Override
                public void onReadCharacteristic(String data) {
                    //testTitleText.setText("READ CHARACTERISTIC");
                }

                @Override
                public void onWriteCharacteristic() {
                    //testTitleText.setText("WRITE CHARACTERISTIC");
                    //bleConnectionImage.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.ble_connection)));
                    bleConnectionImage.setImageDrawable(AppCompatResources.getDrawable(MainActivity.this, R.drawable.connected_device));
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
                        bleConnectionImage.setImageDrawable(AppCompatResources.getDrawable(MainActivity.this, R.drawable.disconnected_device));

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
        syncButton = findViewById(R.id.img_btn_sync_main);
        bleConnectionImage = findViewById(R.id.img_ble_connection_main);

        patientInfoContainer = findViewById(R.id.fragment_container_patient_info_main);
        resultContainer = findViewById(R.id.fragment_container_result_main);

        Display display = getWindowManager().getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        Log.e(getClass().getSimpleName(), "X : " + point.x + ", Y : " + point.y);

        ViewGroup.LayoutParams layoutParams = (ViewGroup.LayoutParams) patientInfoContainer.getLayoutParams();
        layoutParams.width = (int)((float)point.y / (float)3f);
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

        syncButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                SharedPreferencesManager.setPatientHash(MainActivity.this, null);
                observer.notificationObservers(404);
                sync();

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
        setFragment(R.id.fragment_container_patient_info_main, informationFragment);

        SharedPreferencesManager.setPatientHash(getApplicationContext(), null);
        //observer.notificationObservers(404);
        setFragmentByMeasGroup(null);
        startService(new Intent(getApplicationContext(), SpiroKitBluetoothLeService.class));

        loadingDialog = new LoadingDialog(MainActivity.this);
        syncLoadingDialog = new SyncLoadingDialog(MainActivity.this);

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
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        SpiroKitVolley.cancelAllRequest();
    }

    @Override
    protected void onResume() {
        super.onResume();


        bindService(new Intent(getApplicationContext(), SpiroKitBluetoothLeService.class), serviceConnection ,Context.BIND_AUTO_CREATE);

        if (SharedPreferencesManager.getOfficeHash(MainActivity.this) == null) {

            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();

            return;
        }

        Office office = SpiroKitDatabase.getInstance(MainActivity.this).officeDao().selectOfficeByHash(SharedPreferencesManager.getOfficeHash(MainActivity.this));

        if ((office.getIsUseSync() == 0) || (!SharedPreferencesManager.getUseSync(MainActivity.this))) {

            syncButton.setEnabled(false);
            syncButton.setElevation(0f);
            syncButton.setBackgroundTintList(AppCompatResources.getColorStateList(MainActivity.this, R.color.gray_dark));

            /*
            ConfirmDialog confirmDialog = new ConfirmDialog(MainActivity.this);
            confirmDialog.setTitle(getString(R.string.not_sync_permission));
            confirmDialog.show();

             */

        } else {

            syncButton.setEnabled(true);
            syncButton.setElevation(3f);
            syncButton.setBackgroundTintList(AppCompatResources.getColorStateList(MainActivity.this, R.color.gray_background));

        }



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



        if (history == null) {

            EmptyResultFragment emptyResultFragment = new EmptyResultFragment();
            setFragment(R.id.fragment_container_result_main, emptyResultFragment);

            return;
        }


        if (history.getMeasDiv().equals("f")) {
            FvcResultFragment fvcResultFragment = new FvcResultFragment(history);
            observer.deleteObservers();
            observer.addObserver(informationFragment);
            observer.addObserver(fvcResultFragment);

            fvcResultFragment.setChangedListener(new OnItemChangedListener() {
                @Override
                public void onChanged() {
                    observer.notificationObservers(404);
                }
            });

            replaceFragment(R.id.fragment_container_result_main, fvcResultFragment);

        } else if (history.getMeasDiv().equals("s")) {
            SvcResultFragment svcResultFragment = new SvcResultFragment(history);
            observer.deleteObservers();
            observer.addObserver(informationFragment);
            observer.addObserver(svcResultFragment);

            svcResultFragment.setChangedListener(new OnItemChangedListener() {
                @Override
                public void onChanged() {
                    observer.notificationObservers(404);
                }
            });

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

    private void sync() {

        syncLoadingDialog = new SyncLoadingDialog(MainActivity.this);
        syncLoadingDialog.setCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                SpiroKitVolley.cancelAllRequest();
            }
        });
        syncLoadingDialog.show();
        syncLoadingDialog.setProgress(0);

        Thread thread = new Thread() {

            @Override
            public void run() {
                super.run();
                Looper.prepare();

                SpiroKitDatabase database = SpiroKitDatabase.getInstance(MainActivity.this);
                JSONObject jsonObject = new JSONObject();

                Office office = database.officeDao().selectOfficeByHash(SharedPreferencesManager.getOfficeHash(MainActivity.this));

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


                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            syncLoadingDialog.setTitle(getString(R.string.sync_preparing_office));
                            syncLoadingDialog.setMaxProgress(operators.size());
                        }
                    });

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

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                syncLoadingDialog.incrementProgress();
                            }
                        });

                    }
                    operators.clear();

                    jsonObject.put(JsonKeys.JSON_KEY_OPERATOR, operatorArray);

                    List<Patient> patients = database.patientDao().selectAll(office.getHashed());

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            syncLoadingDialog.setTitle(getString(R.string.sync_preparing_patients));
                            syncLoadingDialog.setMaxProgress(patients.size());

                        }
                    });

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

                        Log.e(getClass().getSimpleName(), "START : " + patient.getStartSmokingDay());
                        Log.e(getClass().getSimpleName(), "STOP : " + patient.getStopSmokingDay());

                        patientJsonObject.put(JsonKeys.JSON_KEY_FROM_OS, patient.getOs());
                        patientJsonObject.put(JsonKeys.JSON_KEY_LATEST_DAY, null);
                        patientJsonObject.put(JsonKeys.JSON_KEY_IS_DELETED, patient.getIsDeleted());

                        patientJsonArray.put(patientJsonObject);

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                syncLoadingDialog.incrementProgress();
                            }
                        });

                    }
                    jsonObject.put(JsonKeys.JSON_KEY_PATIENT, patientJsonArray);

                    patients.clear();

                    List<CalHistory> calHistories = database.calHistoryDao().selectAll(office.getHashed());
                    List<CalHistoryRawData> rawDataList = new ArrayList<>();

                    Log.e(getClass().getSimpleName(), "HISTORY SIZE : " + calHistories.size());

                    JSONArray historyArray = new JSONArray();
                    JSONArray rawDataArray = new JSONArray();

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            syncLoadingDialog.setTitle(getString(R.string.sync_preparing_histories));
                            syncLoadingDialog.setMaxProgress(calHistories.size());
                        }
                    });

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

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                syncLoadingDialog.incrementProgress();
                            }
                        });

                    }

                    calHistories.clear();

                    jsonObject.put(JsonKeys.JSON_KEY_CAL_HISTORY, historyArray);

                    Log.e(getClass().getSimpleName(), "RAW TOTAL SIZE : " + rawDataList.size());

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            syncLoadingDialog.setTitle(getString(R.string.sync_preparing_raw_data));
                            syncLoadingDialog.setMaxProgress(rawDataList.size());
                        }
                    });

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

                        //Log.e(getClass().getSimpleName(), "RAW DATA UPDATED DATE : " + raw.getUpdatedDate());

                        rawDataArray.put(rawJsonObject);

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                syncLoadingDialog.incrementProgress();
                            }
                        });

                    }

                    SpiroKitDatabase.removeInstance();

                    rawDataList.clear();

                    jsonObject.put(JsonKeys.JSON_KEY_RAW_DATA, rawDataArray);


                    //Log.e(getClass().getSimpleName(), "JSON LENGTH : " + jsonObject.toString().length());
                    //POST
                    SpiroKitVolley.setVolleyListener(volleyResponseListener);
                    SpiroKitVolley.postJson(jsonObject);

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            syncLoadingDialog.setTitle(getString(R.string.server_sync_waiting));
                            syncLoadingDialog.setProgress(0);
                            syncLoadingDialog.setCancelableUseButton(true);
                        }
                    });

                } catch (JSONException e) {

                    Log.e(getClass().getSimpleName(), "++++++++++++++++++++++++++=\n" + e.toString());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            syncLoadingDialog.dismiss();

                            ConfirmDialog confirmDialog = new ConfirmDialog(MainActivity.this);
                            confirmDialog.setTitle(getString(R.string.failed_server_connection, e.toString()));
                            confirmDialog.show();
                        }
                    });

                }

                Looper.loop();
            }
        };

        thread.start();

    }

    private void handleResponse(JSONObject jsonObject) {

        syncLoadingDialog = new SyncLoadingDialog(MainActivity.this);
        syncLoadingDialog.show();

        Thread thread = new Thread() {

            @Override
            public void run() {
                super.run();
                Looper.prepare();

                SpiroKitDatabase database = SpiroKitDatabase.getInstance(MainActivity.this);
                //office 하나만 삭제하면 CASCADE 로 연쇄삭제됨.

                try {

                    int resultCode = jsonObject.getInt(JsonKeys.JSON_KEY_RESULT);
                    String instance = jsonObject.getString(JsonKeys.JSON_KEY_INSTANCE);

                    Log.e(getClass().getSimpleName(), "RESULT : " + resultCode);

                    JSONObject items = jsonObject.getJSONObject(JsonKeys.JSON_KEY_ITEMS);
                    Log.e(getClass().getSimpleName(), "RESPONSE JSON SIZE : " + items.length());

                    JSONObject office = items.getJSONObject(JsonKeys.JSON_KEY_OFFICE);

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            syncLoadingDialog.setTitle(getString(R.string.sync_handling_office));
                            syncLoadingDialog.setMaxProgress(100);
                            syncLoadingDialog.setProgress(0);
                        }
                    });

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
                    o.setOfficePassword(SharedPreferencesManager.getOfficePass(MainActivity.this));
                    o.setUpdateDate(office.getString(JsonKeys.JSON_KEY_UPDATED_DATE));

                    database.officeDao().insert(o);

                    Log.e(getClass().getSimpleName(),
                            "\nOffice\n"
                            + "\nhashed : " + o.getHashed()
                            + "\nname : " + o.getName()
                            + "\ncode : " + o.getCode()
                            + "\ntel : " + o.getTel()
                            + "\naddress : " + o.getAddress()
                            + "\ncountry code : " + o.getCountryCode()
                            + "\nis use : " + o.getIsUse()
                            + "\nis use sync : " + o.getIsUseSync()
                            + "\noffice id : " + o.getOfficeID()
                            + "\npassword : " + o.getOfficePassword()
                            + "\nupdate date : " + o.getUpdateDate()
                            + "\nis deleted : " + o.getIsDeleted()
                            );

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            syncLoadingDialog.setProgress(100);
                        }
                    });

                    JSONArray operators = items.getJSONArray(JsonKeys.JSON_KEY_OPERATORS);
                    //for (JSONObject operatorObject : operators.)

                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            syncLoadingDialog.setTitle(getString(R.string.sync_handling_operators));
                            syncLoadingDialog.setMaxProgress(operators.length());

                        }
                    });

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

                        Log.e(getClass().getSimpleName(),
                                "\nOperator\n"
                                        + "\nhashed : " + op.getHashed()
                                        + "\noffice hashed : " + op.getOfficeHashed()
                                        + "\nname : " + op.getName()
                                        + "\nwork : " + op.getWork()
                                        + "\nis deleted : " + op.getIsDeleted()
                                        + "\ncreate date : " + op.getCreateTimestamp()
                                        + "\nupdate date : " + op.getUpdatedDate()

                        );

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                syncLoadingDialog.incrementProgress();
                            }
                        });
                    }

                    JSONArray patients = items.getJSONArray(JsonKeys.JSON_KEY_PATIENTS);

                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            syncLoadingDialog.setTitle(getString(R.string.sync_handling_patients));
                            syncLoadingDialog.setMaxProgress(patients.length());

                        }
                    });

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

                        if (start.contains("0000-00-00") || (start == null)) {
                            p.setStartSmokingDay(null);
                            p.setSmokingAmountPerDay("0");
                            p.setStopSmokingDay(null);
                            p.setNowSmoking(0);
                            p.setSmokingPeriod(0);

                        } else {
                            p.setStartSmokingDay(start);
                            p.setSmokingAmountPerDay(amount);

                            if (stop.contains("0000-00-00") || (stop == null)) {
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

                        Log.e(getClass().getSimpleName(),
                                "\nPatient\n"
                                        + "\nhashed : " + p.getHashed()
                                        + "\noffice hashed : " + p.getOfficeHashed()
                                        + "\nname : " + p.getName()
                                        + "\ngender : " + p.getGender()
                                        + "\nrace : " + p.getHumanRace()
                                        + "\ncreate date : " + p.getCreateDate()
                                        + "\nupdate date : " + p.getUpdatedDate()

                        );

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                syncLoadingDialog.incrementProgress();
                            }
                        });

                    }

                    JSONArray histories = items.getJSONArray(JsonKeys.JSON_KEY_HISTORIES);

                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            syncLoadingDialog.setTitle(getString(R.string.sync_handling_histories));
                            syncLoadingDialog.setMaxProgress(histories.length());

                        }
                    });

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

                        Log.e(getClass().getSimpleName(),
                                "\nHistory\n"
                                        + "\nhashed : " + h.getHashed()
                                        + "\noffice hashed : " + h.getOfficeHashed()
                                        + "\npatient hashed : " + h.getPatientHashed()
                                        + "\noperator hashed : " + h.getOperatorHashed()
                                        + "\nfinish date : " + h.getFinishDate()
                                        + "\ncreate date : " + h.getCreateDate()
                                        + "\nupdate date : " + h.getUpdatedDate()

                        );

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                syncLoadingDialog.incrementProgress();
                            }
                        });

                    }

                    JSONArray rawData = items.getJSONArray(JsonKeys.JSON_KEY_RAW_DATA_LIST);
                    Log.e(getClass().getSimpleName(), "RECEIVE RAW DATA SIZE : " + rawData.length());

                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            syncLoadingDialog.setTitle(getString(R.string.sync_handling_raw_data));
                            syncLoadingDialog.setMaxProgress(rawData.length());

                        }
                    });

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

                        Log.e(getClass().getSimpleName(),
                                "\nData\n"
                                        + "\nhashed : " + d.getHashed()
                                        + "\nhistory hashed : " + d.getCalHistoryHashed()
                                        + "\nordinal : " + d.getOrderNumber()
                                        + "\nis deleted : " + d.getIsDeleted()
                                        + "\ncreate date : " + d.getCreateDate()
                                        + "\nupdate date : " + d.getUpdatedDate()
                                        + "\ndata : " + d.getData()

                        );

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                syncLoadingDialog.incrementProgress();
                            }
                        });

                    }

                    SpiroKitDatabase.removeInstance();
                    items = null;

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            syncLoadingDialog.dismiss();

                            ConfirmDialog confirmDialog = new ConfirmDialog(MainActivity.this);
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