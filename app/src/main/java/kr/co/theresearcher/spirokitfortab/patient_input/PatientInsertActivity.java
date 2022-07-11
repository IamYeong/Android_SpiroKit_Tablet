package kr.co.theresearcher.spirokitfortab.patient_input;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.room.Room;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import kr.co.theresearcher.spirokitfortab.HashConverter;
import kr.co.theresearcher.spirokitfortab.R;
import kr.co.theresearcher.spirokitfortab.SharedPreferencesManager;

import kr.co.theresearcher.spirokitfortab.db.SpiroKitDatabase;
import kr.co.theresearcher.spirokitfortab.db.human_race.HumanRace;
import kr.co.theresearcher.spirokitfortab.db.patient.Patient;

import kr.co.theresearcher.spirokitfortab.dialog.ConfirmDialog;

public class PatientInsertActivity extends AppCompatActivity {

    private EditText chartNumberField, nameField, heightField, weightField, smokeAmountField;
    private Button insertButton, maleButton, femaleButton, smokeButton, nonSmokeButton, haveSmokeButton, haveNotSmokeButton
            , birthSelectButton, startSmokeDateSelectButton, stopSmokeDateSelectButton;
    private AppCompatSpinner humanRaceSpinner;

    private ArrayAdapter<String> humanRaceAdapter, doctorAdapter;

    private ImageButton backButton;

    private boolean isMale = true;
    private boolean isSmoking = false;
    private boolean haveSmoking = false;

    private long birthDate = -1;
    private long startSmokeDate = -1;
    private long stopSmokeDate = -1;

    private int humanRaceID = 0;
    private int doctorID = 0;

    private Handler handler = new Handler(Looper.getMainLooper());
    private String dateFormat = "yyyy-MM-dd";
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_insert);

        backButton = findViewById(R.id.img_btn_back_insert_patient);

        chartNumberField = findViewById(R.id.et_chart_number_insert_patient);

        nameField = findViewById(R.id.et_name_insert_patient);
        heightField = findViewById(R.id.et_height_insert_patient);
        weightField = findViewById(R.id.et_weight_insert_patient);
        smokeAmountField = findViewById(R.id.et_smoke_amount_insert_patient);
        insertButton = findViewById(R.id.btn_insert_patient);
        maleButton = findViewById(R.id.btn_male_insert_patient);
        femaleButton = findViewById(R.id.btn_female_insert_patient);
        smokeButton = findViewById(R.id.btn_now_smoke_insert_patient);
        nonSmokeButton = findViewById(R.id.btn_not_now_smoke_insert_patient);

        haveSmokeButton = findViewById(R.id.btn_have_smoking_insert_patient);
        haveNotSmokeButton = findViewById(R.id.btn_have_not_smoking_insert_patient);

        humanRaceSpinner = findViewById(R.id.spinner_human_race_insert_patient);

        birthSelectButton = findViewById(R.id.btn_birth_date_insert_patient);
        startSmokeDateSelectButton = findViewById(R.id.btn_start_smoke_date_insert_patient);
        stopSmokeDateSelectButton = findViewById(R.id.btn_stop_smoke_date_insert_patient);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        maleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isMale = true;

                maleButton.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.primary_color)));
                maleButton.setTextColor(ColorStateList.valueOf(getColor(R.color.white)));

                femaleButton.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.white)));
                femaleButton.setTextColor(ColorStateList.valueOf(getColor(R.color.black)));
            }
        });

        femaleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isMale = false;

                femaleButton.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.primary_color)));
                femaleButton.setTextColor(ColorStateList.valueOf(getColor(R.color.white)));

                maleButton.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.white)));
                maleButton.setTextColor(ColorStateList.valueOf(getColor(R.color.black)));
            }
        });

        haveSmokeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                haveSmoking = true;

                haveSmokeButton.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.primary_color)));
                haveSmokeButton.setTextColor(ColorStateList.valueOf(getColor(R.color.white)));

                haveNotSmokeButton.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.white)));
                haveNotSmokeButton.setTextColor(ColorStateList.valueOf(getColor(R.color.black)));

                smokeButton.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.white)));
                smokeButton.setTextColor(ColorStateList.valueOf(getColor(R.color.black)));
                smokeButton.setClickable(true);

                nonSmokeButton.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.white)));
                nonSmokeButton.setTextColor(ColorStateList.valueOf(getColor(R.color.black)));
                nonSmokeButton.setClickable(true);

                startSmokeDateSelectButton.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.white)));
                startSmokeDateSelectButton.setTextColor(ColorStateList.valueOf(getColor(R.color.black)));
                startSmokeDateSelectButton.setClickable(true);

                smokeAmountField.setBackgroundResource(R.drawable.text_field_inner_shadow_white);
                smokeAmountField.setHintTextColor(getColor(R.color.gray_dark));
                smokeAmountField.setFocusable(true);

                stopSmokeDateSelectButton.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.white)));
                stopSmokeDateSelectButton.setTextColor(ColorStateList.valueOf(getColor(R.color.black)));
                stopSmokeDateSelectButton.setClickable(true);

            }
        });

        haveNotSmokeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                haveSmoking = false;

                haveNotSmokeButton.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.primary_color)));
                haveNotSmokeButton.setTextColor(ColorStateList.valueOf(getColor(R.color.white)));

                haveSmokeButton.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.white)));
                haveSmokeButton.setTextColor(ColorStateList.valueOf(getColor(R.color.black)));

                //하위 선택지 비활성화
                smokeButton.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.gray_background)));
                smokeButton.setTextColor(ColorStateList.valueOf(getColor(R.color.gray_dark)));
                smokeButton.setClickable(false);

                nonSmokeButton.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.gray_background)));
                nonSmokeButton.setTextColor(ColorStateList.valueOf(getColor(R.color.gray_dark)));
                nonSmokeButton.setClickable(false);

                startSmokeDateSelectButton.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.gray_background)));
                startSmokeDateSelectButton.setTextColor(ColorStateList.valueOf(getColor(R.color.gray_dark)));
                startSmokeDateSelectButton.setText(dateFormat);
                startSmokeDateSelectButton.setClickable(false);

                smokeAmountField.setBackgroundResource(R.drawable.text_field_background_round_gray);
                smokeAmountField.setHintTextColor(getColor(R.color.gray_dark));
                smokeAmountField.setFocusable(false);

                stopSmokeDateSelectButton.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.gray_background)));
                stopSmokeDateSelectButton.setTextColor(ColorStateList.valueOf(getColor(R.color.gray_dark)));
                stopSmokeDateSelectButton.setText(dateFormat);
                stopSmokeDateSelectButton.setClickable(false);

                isSmoking = false;
                startSmokeDate = -1L;
                stopSmokeDate = -1L;

            }
        });

        smokeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isSmoking = true;

                smokeButton.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.primary_color)));
                smokeButton.setTextColor(ColorStateList.valueOf(getColor(R.color.white)));

                nonSmokeButton.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.white)));
                nonSmokeButton.setTextColor(ColorStateList.valueOf(getColor(R.color.black)));

                stopSmokeDateSelectButton.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.gray_background)));
                stopSmokeDateSelectButton.setTextColor(ColorStateList.valueOf(getColor(R.color.gray_dark)));
                stopSmokeDateSelectButton.setClickable(false);

            }
        });

        nonSmokeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isSmoking = false;

                nonSmokeButton.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.primary_color)));
                nonSmokeButton.setTextColor(ColorStateList.valueOf(getColor(R.color.white)));

                smokeButton.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.white)));
                smokeButton.setTextColor(ColorStateList.valueOf(getColor(R.color.black)));

                stopSmokeDateSelectButton.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.white)));
                stopSmokeDateSelectButton.setTextColor(ColorStateList.valueOf(getColor(R.color.black)));
                stopSmokeDateSelectButton.setClickable(true);

            }
        });

        birthSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MaterialDatePicker<Long> materialDatePicker = MaterialDatePicker.Builder.datePicker()
                        .setTitleText(getString(R.string.select_birth_date))


                        .build();

                materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
                    @Override
                    public void onPositiveButtonClick(Long selection) {

                        birthDate = selection;
                        birthSelectButton.setText(simpleDateFormat.format(birthDate));

                    }
                });

                materialDatePicker.show(getSupportFragmentManager(), "DATE_PICKER");

            }
        });

        startSmokeDateSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MaterialDatePicker<Long> materialDatePicker = MaterialDatePicker.Builder.datePicker()
                        .setTitleText(getString(R.string.select_start_smoke_date))


                        .build();

                materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
                    @Override
                    public void onPositiveButtonClick(Long selection) {

                        startSmokeDate = selection;
                        startSmokeDateSelectButton.setText(simpleDateFormat.format(startSmokeDate));

                    }
                });

                materialDatePicker.show(getSupportFragmentManager(), "DATE_PICKER");

            }
        });

        stopSmokeDateSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MaterialDatePicker<Long> materialDatePicker = MaterialDatePicker.Builder.datePicker()
                        .setTitleText(getString(R.string.select_stop_smoke_date))

                        .build();

                materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
                    @Override
                    public void onPositiveButtonClick(Long selection) {

                        stopSmokeDate = selection;
                        stopSmokeDateSelectButton.setText(simpleDateFormat.format(stopSmokeDate));

                    }
                });

                materialDatePicker.show(getSupportFragmentManager(), "DATE_PICKER");

            }
        });

        List<String> humanRaces = new ArrayList<>();
        List<String> doctors = new ArrayList<>();
        //for (HumanRace humanRace : HumanRace.values()) humanRaces.add(humanRace.getValue());
        //연도부터 선택하면 활성화 하는 방향도 좋을 듯

        humanRaceAdapter = new ArrayAdapter<String>(
                this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, humanRaces
        );

        doctorAdapter = new ArrayAdapter<String>(
                PatientInsertActivity.this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, doctors
        );

        humanRaceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                humanRaceID = position;

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        humanRaceSpinner.setAdapter(humanRaceAdapter);



        insertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean check = checkInputData();
                if (check) {

                    Thread thread = new Thread() {

                        @Override
                        public void run() {
                            super.run();
                            Looper.prepare();

                            String name = nameField.getText().toString();
                            String chartNumber = chartNumberField.getText().toString();
                            int height = Integer.parseInt("0" + heightField.getText().toString());
                            int weight = Integer.parseInt("0" + weightField.getText().toString());
                            String smokeAmount = smokeAmountField.getText().toString() + "0";
                            SimpleDateFormat birthDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                            char gender = 'm';
                            int nowSmoking = 0;
                            if (!isMale) gender = 'f';
                            if (isSmoking) nowSmoking = 1;

                            try {

                                Patient patient = new Patient.Builder()
                                        .officeHashed(SharedPreferencesManager.getOfficeHash(PatientInsertActivity.this))
                                        .name(name)
                                        .gender(gender + "")
                                        .height(height)
                                        .weight(weight)
                                        .chartNumber(chartNumber)
                                        .hashed(HashConverter.hashingFromString(
                                                chartNumber +
                                                        name +
                                                        birthDateFormat.format(birthDate) +
                                                        SharedPreferencesManager.getOfficeHash(PatientInsertActivity.this)))
                                        .humanRace("y")
                                        .nowSmoking(nowSmoking)
                                        .smokingAmountDay(smokeAmount)
                                        .birthDay(birthDateFormat.format(birthDate))
                                        .build();

                                patient.setStartSmokingDay(birthDateFormat.format(startSmokeDate));
                                patient.setStopSmokingDay(birthDateFormat.format(stopSmokeDate));
                                patient.setSmokingPeriod(diffDateMonth(startSmokeDate, stopSmokeDate));

                                SpiroKitDatabase database = SpiroKitDatabase.getInstance(getApplicationContext());
                                database.patientDao().insertPatient(patient);
                                SpiroKitDatabase.removeInstance();

                            } catch (NoSuchAlgorithmException e) {

                            }

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

                } else {
                    ConfirmDialog confirmDialog = new ConfirmDialog(PatientInsertActivity.this);
                    confirmDialog.setTitle("");
                    confirmDialog.show();
                }

            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }


    private boolean checkInputData() {

        if (chartNumberField.getText().toString().length() == 0) return false;
        if (nameField.getText().toString().length() == 0) return false;
        if (heightField.getText().toString().length() == 0) return false;
        if (weightField.getText().toString().length() == 0) return false;
        if (chartNumberField.getText().toString().length() == 0) return false;
        if (birthDate == -1) return false;

        return true;
    }

    private void setPatientInfoInPreferences(Context context, Patient patient) {

        /*
        SharedPreferencesManager.setPatientID(context, );
        SharedPreferencesManager.setPatientName(context, );
        SharedPreferencesManager.setPatientHash(context, );
        SharedPreferencesManager.setPatientChartNumber(context, );
        SharedPreferencesManager.setPatientGender(context, );
        SharedPreferencesManager.setPatientHeight(context, );
        SharedPreferencesManager.setPatientWeight(context, );
        SharedPreferencesManager.setPatientOperatorDoctorHash(context, );
        SharedPreferencesManager.setPatientHumanRace(context, );

        SharedPreferencesManager.setPatientSmokingIsNow(context, );
        SharedPreferencesManager.setPatientSmokingStartDate(context, );
        SharedPreferencesManager.setPatientSmokingStopDate(context, );
        SharedPreferencesManager.setPatientSmokingAmountPerDay(context, );
        SharedPreferencesManager.setPatientSmokingPeriod(context, );


         */
    }

    private int diffDateMonth(long from, long to) {

        Date fromDate = new Date(from);
        Date toDate = new Date(to);

        Calendar fromCal = Calendar.getInstance();
        fromCal.setTime(fromDate);

        Calendar toCal = Calendar.getInstance();
        toCal.setTime(toDate);

        int diffYear = toCal.get(Calendar.YEAR) - fromCal.get(Calendar.YEAR);
        int diffMonth = toCal.get(Calendar.MONTH) - fromCal.get(Calendar.MONTH);

        return (diffYear * 12) + diffMonth;

    }

}