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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import kr.co.theresearcher.spirokitfortab.R;
import kr.co.theresearcher.spirokitfortab.SharedPreferencesManager;

import kr.co.theresearcher.spirokitfortab.db.human_race.HumanRace;
import kr.co.theresearcher.spirokitfortab.db.patient.Patient;

public class PatientModifyActivity extends AppCompatActivity {

    private EditText chartNumberField, nameField, heightField, weightField, smokeAmountField;
    private Button modifyButton, maleButton, femaleButton, smokeButton, nonSmokeButton, haveSmokeButton, haveNotSmokeButton
            , birthSelectButton, startSmokeDateSelectButton, stopSmokeDateSelectButton;
    private AppCompatSpinner matchDoctorSpinner, humanRaceSpinner;

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

    private Patient patient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_insert);

        patient = getPatientFromPreferences(PatientModifyActivity.this);

        backButton = findViewById(R.id.img_btn_back_insert_patient);

        chartNumberField = findViewById(R.id.et_chart_number_insert_patient);

        nameField = findViewById(R.id.et_name_insert_patient);
        heightField = findViewById(R.id.et_height_insert_patient);
        weightField = findViewById(R.id.et_weight_insert_patient);
        smokeAmountField = findViewById(R.id.et_smoke_amount_insert_patient);
        modifyButton = findViewById(R.id.btn_insert_patient);
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
                        stopSmokeDateSelectButton.setText(simpleDateFormat.format(birthDate));

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
                PatientModifyActivity.this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, doctors
        );

        matchDoctorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                doctorID = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        humanRaceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                humanRaceID = position;

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        modifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Thread thread = new Thread() {

                    @Override
                    public void run() {
                        super.run();
                        Looper.prepare();

                        String name = nameField.getText().toString();
                        String chartNumber = chartNumberField.getText().toString();
                        int height = Integer.parseInt(heightField.getText().toString());
                        int weight = Integer.parseInt(weightField.getText().toString());
                        float smokeAmount = Float.parseFloat(smokeAmountField.getText().toString());

                        Patient patient = new Patient.Builder()
                                .officeHashed(SharedPreferencesManager.getOfficeHash(PatientModifyActivity.this))
                                .name(name)
                                .height(height)
                                .weight(weight)
                                .chartNumber(chartNumber)


                                .build();

                        patient.setId(SharedPreferencesManager.getPatientId(PatientModifyActivity.this));
                        setPatientInfoInPreferences(PatientModifyActivity.this, patient);

                        //PatientDatabase.getInstance(PatientModifyActivity.this).patientDao().updatePatient(patient);

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
        });

        humanRaceSpinner.setAdapter(humanRaceAdapter);



    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        selectDoctors();
        setViewStateByPatientInfo();
    }

    private void selectDoctors() {

        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();
                Looper.prepare();


                /*

                doctorAdapter = new ArrayAdapter<String>(
                        PatientModifyActivity.this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, operatorNames
                );

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        matchDoctorSpinner.setAdapter(doctorAdapter);
                    }
                });

                 */


                Looper.loop();
            }
        };
        thread.start();

    }

    private void setViewStateByPatientInfo() {

        chartNumberField.setText(patient.getChartNumber());
        nameField.setText(patient.getName());
        if (patient.getGender().equals("m")) {
            isMale = true;

            maleButton.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.primary_color)));
            maleButton.setTextColor(ColorStateList.valueOf(getColor(R.color.white)));

            femaleButton.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.white)));
            femaleButton.setTextColor(ColorStateList.valueOf(getColor(R.color.black)));
        } else {
            isMale = false;

            femaleButton.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.primary_color)));
            femaleButton.setTextColor(ColorStateList.valueOf(getColor(R.color.white)));

            maleButton.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.white)));
            maleButton.setTextColor(ColorStateList.valueOf(getColor(R.color.black)));
        }

        birthSelectButton.setText(patient.getBirthDay());
        heightField.setText(Integer.toString(patient.getHeight()));
        weightField.setText(Integer.toString(patient.getWeight()));
        humanRaceSpinner.setSelection(0);

        //doctorSpinner.setSelection(patient.getDoctorID)
        if (patient.getNowSmoking() == 1) {

            isSmoking = true;

            smokeButton.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.primary_color)));
            smokeButton.setTextColor(ColorStateList.valueOf(getColor(R.color.white)));

            nonSmokeButton.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.white)));
            nonSmokeButton.setTextColor(ColorStateList.valueOf(getColor(R.color.black)));

            stopSmokeDateSelectButton.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.gray_background)));
            stopSmokeDateSelectButton.setTextColor(ColorStateList.valueOf(getColor(R.color.gray_dark)));
            stopSmokeDateSelectButton.setClickable(false);

            smokeAmountField.setText(patient.getSmokingAmountPerDay());

            //startSmokeDateSelectButton.setText(simpleDateFormat.format(patient.getStartSmokeDate()));

            haveSmokeButton.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.primary_color)));
            haveSmokeButton.setTextColor(ColorStateList.valueOf(getColor(R.color.white)));

            haveNotSmokeButton.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.white)));
            haveNotSmokeButton.setTextColor(ColorStateList.valueOf(getColor(R.color.black)));

        } else {

            if (patient.getStartSmokingDay().equals("")) {

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


            } else {

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

        }



    }

    private Patient getPatientFromPreferences(Context context) {

        Patient patient = new Patient.Builder()

                .build();

        return patient;
    }

    private void setPatientInfoInPreferences(Context context, Patient patient) {



    }

}