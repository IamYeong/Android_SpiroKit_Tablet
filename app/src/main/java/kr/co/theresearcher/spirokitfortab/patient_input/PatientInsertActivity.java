package kr.co.theresearcher.spirokitfortab.patient_input;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.room.Room;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import kr.co.theresearcher.spirokitfortab.R;
import kr.co.theresearcher.spirokitfortab.db.RoomNames;
import kr.co.theresearcher.spirokitfortab.db.gender.Gender;
import kr.co.theresearcher.spirokitfortab.db.human_race.HumanRace;
import kr.co.theresearcher.spirokitfortab.db.meas_group.MeasGroup;
import kr.co.theresearcher.spirokitfortab.db.patient.Patient;
import kr.co.theresearcher.spirokitfortab.db.patient.PatientDao;
import kr.co.theresearcher.spirokitfortab.db.patient.PatientDatabase;
import kr.co.theresearcher.spirokitfortab.dialog.ConfirmDialog;

public class PatientInsertActivity extends AppCompatActivity {

    private EditText chartNumberField, nameField, heightField, weightField, smokeAmountField;
    private Button insertButton, maleButton, femaleButton, smokeButton, nonSmokeButton;
    private AppCompatSpinner birthYearSpr, birthMonthSpr, birthDaySpr,
    startSmokeYearSpr, startSmokeMonthSpr, startSmokeDaySpr, stopSmokeYearSpr, stopSmokeMonthSpr, stopSmokeDaySpr,
    humanRaceSpr, matchDoctorSpr;

    private ImageButton backButton;

    private boolean isMale = true;
    private boolean isSmoking = true;

    private String chartNumber, name;
    private int birthYear, birthMonth, birthDay, startSmokeYear, startSmokeMonth, startSmokeDay, stopSmokeYear, stopSmokeMonth, stopSmokeDay;
    private int height, weight;
    private String gender;
    private float smokeAmount = 0f;

    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_insert);

        backButton = findViewById(R.id.img_btn_back_insert_patient);

        chartNumberField = findViewById(R.id.et_chart_number_insert_patient);
        nameField = findViewById(R.id.et_name_insert_patient);
        heightField = findViewById(R.id.et_height_insert_patient);
        weightField = findViewById(R.id.et_weight_insert_patient);
        smokeAmountField = findViewById(R.id.et_smoke_per_day_insert_patient);
        insertButton = findViewById(R.id.btn_insert_patient);
        maleButton = findViewById(R.id.btn_select_male_insert_patient);
        femaleButton = findViewById(R.id.btn_select_female_insert_patient);
        smokeButton = findViewById(R.id.btn_select_smoking_insert_patient);
        nonSmokeButton = findViewById(R.id.btn_select_non_smoking_insert_patient);

        birthYearSpr = findViewById(R.id.spinner_birth_year_insert_patient);
        birthMonthSpr = findViewById(R.id.spinner_birth_month_insert_patient);
        birthDaySpr = findViewById(R.id.spinner_birth_day_insert_patient);

        startSmokeYearSpr = findViewById(R.id.spinner_start_smoke_year_insert_patient);
        startSmokeMonthSpr = findViewById(R.id.spinner_start_smoke_month_insert_patient);
        startSmokeDaySpr = findViewById(R.id.spinner_start_smoke_day_insert_patient);

        stopSmokeYearSpr = findViewById(R.id.spinner_stop_smoke_year_insert_patient);
        stopSmokeMonthSpr = findViewById(R.id.spinner_stop_smoke_month_insert_patient);
        stopSmokeDaySpr = findViewById(R.id.spinner_stop_smoke_day_insert_patient);

        humanRaceSpr = findViewById(R.id.spinner_human_race_insert_patient);
        matchDoctorSpr = findViewById(R.id.spinner_match_doctor_insert_patient);

        heightField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        weightField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

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

                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());


                            Patient patient = new Patient();

                            try {

                                patient.setBirthDate(simpleDateFormat.parse("" + birthYear + birthMonth + birthDay).getTime());
                                patient.setName(name);
                                patient.setChartNumber(chartNumber);
                                patient.setHeight(height);
                                patient.setWeight(weight);
                                patient.setGender(isMale);
                                patient.setSmoke(isSmoking);
                                patient.setSmokeAmountPack(smokeAmount);
                                patient.setStartSmokeDate(simpleDateFormat.parse("" + startSmokeYear + startSmokeMonth + startSmokeDay).getTime());
                                patient.setStopSmokeDate(simpleDateFormat.parse("" + stopSmokeYear + stopSmokeMonth + stopSmokeDay).getTime());

                            } catch (ParseException e) {

                                ConfirmDialog confirmDialog = new ConfirmDialog(PatientInsertActivity.this);
                                confirmDialog.setTitle("");
                                confirmDialog.show();

                            }


                            PatientDatabase database = Room.databaseBuilder(PatientInsertActivity.this, PatientDatabase.class, RoomNames.ROOM_PATIENT_DB_NAME).build();
                            PatientDao patientDao = database.patientDao();

                            patientDao.insertPatient(patient);

                            database.close();

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

        View.OnClickListener genderSelectListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isMale = !isMale;

                if (isMale) {

                    maleButton.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.primary_color)));
                    femaleButton.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.white)));

                    maleButton.setTextColor(ColorStateList.valueOf(getColor(R.color.white)));
                    femaleButton.setTextColor(ColorStateList.valueOf(getColor(R.color.black)));

                } else {

                    femaleButton.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.primary_color)));
                    maleButton.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.white)));

                    femaleButton.setTextColor(ColorStateList.valueOf(getColor(R.color.white)));
                    maleButton.setTextColor(ColorStateList.valueOf(getColor(R.color.black)));

                }

            }
        };

        View.OnClickListener smokingSelectListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isSmoking = !isSmoking;

                if (isSmoking) {

                    smokeButton.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.primary_color)));
                    nonSmokeButton.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.white)));

                    smokeButton.setTextColor(ColorStateList.valueOf(getColor(R.color.white)));
                    nonSmokeButton.setTextColor(ColorStateList.valueOf(getColor(R.color.black)));

                } else {

                    nonSmokeButton.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.primary_color)));
                    smokeButton.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.white)));

                    smokeButton.setTextColor(ColorStateList.valueOf(getColor(R.color.black)));
                    nonSmokeButton.setTextColor(ColorStateList.valueOf(getColor(R.color.white)));
                }

            }
        };

        maleButton.setOnClickListener(genderSelectListener);
        femaleButton.setOnClickListener(genderSelectListener);

        smokeButton.setOnClickListener(smokingSelectListener);
        nonSmokeButton.setOnClickListener(smokingSelectListener);

        //년도는 1900~올해까지 해도 200개 미만이니 그렇게 결정
        //달은12개
        //일은 최대 31개 로 통일
        //인종은 계약에 따라 늘어날 것이므로 적을 것임(DB 조회 필요)
        //담당의사 DB 조회 필요


        List<Integer> years = new ArrayList<>();
        List<Integer> months = new ArrayList<>();
        List<Integer> days = new ArrayList<>();
        List<String> humanRaces = new ArrayList<>();

        for (int i = Calendar.getInstance().get(Calendar.YEAR); i >= 1900; i--) years.add(i);
        for (int i = 1; i <= 12; i++) months.add(i);
        for (int i = 1; i <= 31; i++) days.add(i);
        for (HumanRace humanRace : HumanRace.values()) humanRaces.add(humanRace.getValue());

        //연도부터 선택하면 활성화 하는 방향도 좋을 듯


        ArrayAdapter<Integer> yearAdapter = new ArrayAdapter<Integer>(
                this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, years
        );

        ArrayAdapter<Integer> monthAdapter = new ArrayAdapter<Integer>(
                this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, months
        );

        ArrayAdapter<Integer> dayAdapter = new ArrayAdapter<Integer>(
                this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, days
        );

        ArrayAdapter<String> humanRaceAdapter = new ArrayAdapter<String>(
                this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, humanRaces
        );

        birthYearSpr.setAdapter(yearAdapter);
        birthMonthSpr.setAdapter(monthAdapter);
        birthDaySpr.setAdapter(dayAdapter);
        birthYearSpr.setSelection(0);
        birthMonthSpr.setSelection(0);
        birthDaySpr.setSelection(0);

        startSmokeYearSpr.setAdapter(yearAdapter);
        startSmokeMonthSpr.setAdapter(monthAdapter);
        startSmokeDaySpr.setAdapter(dayAdapter);
        startSmokeYearSpr.setSelection(0);
        startSmokeMonthSpr.setSelection(0);
        startSmokeDaySpr.setSelection(0);

        stopSmokeYearSpr.setAdapter(yearAdapter);
        stopSmokeMonthSpr.setAdapter(monthAdapter);
        stopSmokeDaySpr.setAdapter(dayAdapter);
        stopSmokeYearSpr.setSelection(0);
        stopSmokeMonthSpr.setSelection(0);
        stopSmokeDaySpr.setSelection(0);

        humanRaceSpr.setAdapter(humanRaceAdapter);
        humanRaceSpr.setSelection(0);

        birthYearSpr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                birthYear = Calendar.getInstance().get(Calendar.YEAR) - position;

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        birthMonthSpr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                birthMonth = position + 1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        birthDaySpr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                birthDay = position + 1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        startSmokeYearSpr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                startSmokeYear = Calendar.getInstance().get(Calendar.YEAR) - position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        startSmokeMonthSpr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                startSmokeMonth = position + 1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        startSmokeDaySpr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                startSmokeDay = position + 1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        stopSmokeYearSpr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                stopSmokeYear = Calendar.getInstance().get(Calendar.YEAR) - position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        stopSmokeMonthSpr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                stopSmokeMonth = position + 1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        stopSmokeDaySpr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                stopSmokeDay = position + 1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        humanRaceSpr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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

        chartNumber = chartNumberField.getText().toString();
        name = nameField.getText().toString();
        if (isMale) gender = Gender.m.toString();
        else gender = Gender.f.toString();
        if (heightField.getText().toString().length() == 0) return false;
        if (weightField.getText().toString().length() == 0) return false;
        if (smokeAmountField.getText().toString().length() == 0) return false;
        if (chartNumberField.getText().toString().length() == 0) return false;
        if (nameField.getText().toString().length() == 0) return false;
        if (birthYear == 0) return false;
        if (birthMonth == 0) return false;
        if (birthDay == 0) return false;
        if (startSmokeYear == 0) return false;
        if (startSmokeMonth == 0) return false;
        if (startSmokeDay == 0) return false;
        if (stopSmokeYear == 0) return false;
        if (stopSmokeMonth == 0) return false;
        if (stopSmokeDay == 0) return false;

        smokeAmount = Float.parseFloat(smokeAmountField.getText().toString());
        height = Integer.parseInt(heightField.getText().toString());
        weight = Integer.parseInt(weightField.getText().toString());
        name = nameField.getText().toString();
        chartNumber = chartNumberField.getText().toString();

        return true;
    }



}