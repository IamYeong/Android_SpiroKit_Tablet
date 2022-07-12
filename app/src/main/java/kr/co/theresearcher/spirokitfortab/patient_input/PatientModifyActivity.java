package kr.co.theresearcher.spirokitfortab.patient_input;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.room.Room;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
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

public class PatientModifyActivity extends AppCompatActivity {

    private EditText chartNumberField, nameField, heightField, weightField, smokeAmountField;
    private Button insertButton, maleButton, femaleButton, smokeButton, nonSmokeButton, haveSmokeButton, haveNotSmokeButton
            , birthSelectButton, startSmokeDateSelectButton, stopSmokeDateSelectButton;
    private AppCompatSpinner humanRaceSpinner;

    private ArrayAdapter<String> humanRaceAdapter;
    private List<HumanRace> humanRaceDatabases;

    private ImageButton backButton;

    private boolean isMale = true;
    private boolean isSmoking = false;
    private boolean haveSmoking = false;

    private long birthDate = Long.MAX_VALUE;
    private long startSmokeDate = Long.MAX_VALUE;
    private long stopSmokeDate = Long.MAX_VALUE;

    private int humanRaceID = -1;

    private InputMethodManager inputMethodManager;

    private Handler handler = new Handler(Looper.getMainLooper());
    private String dateFormat = "yyyy-MM-dd";
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.getDefault());

    private Patient patient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_insert);

        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

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

        smokeAmountField.setFocusableInTouchMode(true);
        smokeAmountField.setText("0");

        disableButton(birthSelectButton);
        disableTextField(chartNumberField);
        disableTextField(nameField);

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

                enableButton(maleButton, true);
                enableButton(femaleButton, false);
            }
        });

        femaleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isMale = false;

                enableButton(maleButton, false);
                enableButton(femaleButton, true);
            }
        });

        haveSmokeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                haveSmoking = true;

                enableButton(haveSmokeButton, true);
                enableButton(haveNotSmokeButton, false);
                enableButton(smokeButton, false);
                enableButton(nonSmokeButton, false);
                enableButton(startSmokeDateSelectButton, false);
                enableButton(stopSmokeDateSelectButton, false);
                enableTextField(smokeAmountField);

            }
        });

        haveNotSmokeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                haveSmoking = false;

                enableButton(haveNotSmokeButton, true);
                enableButton(haveSmokeButton, false);

                disableButton(smokeButton);
                disableButton(nonSmokeButton);
                disableButton(startSmokeDateSelectButton);
                disableButton(stopSmokeDateSelectButton);

                disableTextField(smokeAmountField);

                startSmokeDateSelectButton.setText(dateFormat);
                stopSmokeDateSelectButton.setText(dateFormat);

                isSmoking = false;
                startSmokeDate = Long.MAX_VALUE;
                stopSmokeDate = Long.MAX_VALUE;

            }
        });

        smokeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isSmoking = true;

                enableButton(smokeButton, true);
                enableButton(nonSmokeButton, false);
                disableButton(stopSmokeDateSelectButton);

            }
        });

        nonSmokeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isSmoking = false;

                enableButton(nonSmokeButton, true);
                enableButton(smokeButton, false);
                enableButton(stopSmokeDateSelectButton, false);

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

        String[] humanRaces = getResources().getStringArray(R.array.human_races);
        humanRaceDatabases = SpiroKitDatabase.getInstance(PatientModifyActivity.this).humanRaceDao().selectAllHumanRace();

        humanRaceAdapter = new ArrayAdapter<String>(
                this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, humanRaces
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

                            int height = Integer.parseInt(heightField.getText().toString());
                            int weight = Integer.parseInt(weightField.getText().toString());
                            String smokeAmount = smokeAmountField.getText().toString();
                            char gender = 'm';
                            int nowSmoking = 0;
                            if (!isMale) gender = 'f';
                            if (isSmoking) nowSmoking = 1;

                            patient.setGender(gender + "");
                            patient.setHeight(height);
                            patient.setWeight(weight);
                            patient.setHumanRace(humanRaceDatabases.get(humanRaceID).getRace());
                            patient.setNowSmoking(nowSmoking);
                            patient.setSmokingAmountPerDay(smokeAmount);

                            SpiroKitDatabase database = SpiroKitDatabase.getInstance(getApplicationContext());

                            database.patientDao().updatePatient(patient);
                            setPatientInfoInPreferences(PatientModifyActivity.this, patient);

                            SpiroKitDatabase.removeInstance();

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
                    ConfirmDialog confirmDialog = new ConfirmDialog(PatientModifyActivity.this);
                    confirmDialog.setTitle("");
                    confirmDialog.show();
                }

            }
        });

        patient = getPatientFromPreferences(PatientModifyActivity.this);
        setViewState(patient);

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

        try {

            if (chartNumberField.getText().toString().length() == 0) {
                Log.d(getClass().getSimpleName(), "CHART");
                return false;
            }
            if (nameField.getText().toString().length() == 0) {
                Log.d(getClass().getSimpleName(), "NAME");
                return false;
            }
            if (!(Integer.parseInt(heightField.getText().toString()) > 0)) {
                Log.d(getClass().getSimpleName(), "HEIGHT");
                return false;
            }
            if (!(Integer.parseInt(weightField.getText().toString()) > 0)) {
                Log.d(getClass().getSimpleName(), "WEIGHT");
                return false;
            }
            if (birthDate == Long.MAX_VALUE) {
                Log.d(getClass().getSimpleName(), "BIRTH");
                return false;
            }
            if (humanRaceID == -1) {
                Log.d(getClass().getSimpleName(), "HUMAN");
                return false;
            }

            if ((haveSmoking) && (startSmokeDate == Long.MAX_VALUE)) {
                Log.d(getClass().getSimpleName(), "HAVE & NOT START");
                return false;
            }
            if (haveSmoking && (!isSmoking) && (stopSmokeDate == Long.MAX_VALUE)) {
                Log.d(getClass().getSimpleName(), "STOP & NOT SELECT DATE");
                return false;
            }
            if ((haveSmoking) && (Float.parseFloat(smokeAmountField.getText().toString()) == 0f)) {
                Log.d(getClass().getSimpleName(), "HAVE & NOT ENTER AMOUNT");
                return false;
            }

        } catch(NumberFormatException e) {
            Log.d(getClass().getSimpleName(), e.toString());
            return false;
        }

        return true;
    }

    private void setPatientInfoInPreferences(Context context, Patient patient) {

        SharedPreferencesManager.setPatientHash(context, patient.getHashed());

    }

    private Patient getPatientFromPreferences(Context context) {



        return SpiroKitDatabase.getInstance(context).patientDao().selectPatientByHash(SharedPreferencesManager.getPatientHashed(context));

    }

    private void setViewState(Patient patient) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String formatString = "yyyy-mm-dd";

        chartNumberField.setText(patient.getChartNumber());
        nameField.setText(patient.getName());
        heightField.setText(Integer.toString(patient.getHeight()));
        weightField.setText(Integer.toString(patient.getWeight()));

        String humanRace = patient.getHumanRace();

        if (patient.getGender().equals("m")) {
            enableButton(maleButton, true);
            enableButton(femaleButton, false);
        } else if (patient.getGender().equals("f")) {
            enableButton(maleButton, false);
            enableButton(femaleButton, true);
        }
        try {
            birthDate = simpleDateFormat.parse(patient.getBirthDay()).getTime();
            birthSelectButton.setText(dateFormat.format(birthDate));

            if (patient.getStartSmokingDay() != null) {

                startSmokeDate = simpleDateFormat.parse(patient.getStartSmokingDay()).getTime();
                startSmokeDateSelectButton.setText(dateFormat.format(startSmokeDate));

                enableButton(haveNotSmokeButton, false);
                enableButton(haveSmokeButton, true);

                if (patient.getStopSmokingDay() != null) {

                    stopSmokeDate = simpleDateFormat.parse(patient.getStopSmokingDay()).getTime();
                    stopSmokeDateSelectButton.setText(dateFormat.format(stopSmokeDate));

                    enableButton(nonSmokeButton, true);
                    enableButton(smokeButton, false);

                } else {

                    stopSmokeDateSelectButton.setText(formatString);
                    enableButton(nonSmokeButton, false);
                    enableButton(smokeButton, true);

                }

                smokeAmountField.setText(patient.getSmokingAmountPerDay());

            } else {
                //시작날짜가 없으면
                haveSmoking = false;

                enableButton(haveNotSmokeButton, true);
                enableButton(haveSmokeButton, false);

                disableButton(smokeButton);
                disableButton(nonSmokeButton);
                disableButton(startSmokeDateSelectButton);
                disableButton(stopSmokeDateSelectButton);

                disableTextField(smokeAmountField);

                startSmokeDateSelectButton.setText(formatString);
                stopSmokeDateSelectButton.setText(formatString);

                isSmoking = false;
                startSmokeDate = Long.MAX_VALUE;
                stopSmokeDate = Long.MAX_VALUE;



            }


        } catch (ParseException e) {
            Log.d(getClass().getSimpleName(), e.toString());
        }



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

    private void enableButton(Button button, boolean isSelected) {

        if (isSelected) {
            button.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.primary_color)));
            button.setTextColor(ColorStateList.valueOf(getColor(R.color.white)));
            button.setClickable(true);
        } else {
            button.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.white)));
            button.setTextColor(ColorStateList.valueOf(getColor(R.color.black)));
            button.setClickable(true);
        }

    }

    private void disableButton(Button button) {

        button.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.gray_background)));
        button.setTextColor(ColorStateList.valueOf(getColor(R.color.gray_dark)));
        button.setClickable(false);

    }

    private void enableTextField(EditText editText) {

        editText.setBackgroundResource(R.drawable.text_field_inner_shadow_white);
        editText.setHintTextColor(getColor(R.color.gray_dark));
        editText.setEnabled(true);

    }

    private void disableTextField(EditText editText) {

        editText.setBackgroundResource(R.drawable.text_field_background_round_gray);
        editText.setHintTextColor(getColor(R.color.gray_dark));
        editText.setEnabled(false);
        inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);

    }

    private String conversionDate(long date) {

        if (date == Long.MAX_VALUE) {
            return null;
        } else {

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            return simpleDateFormat.format(date);
        }

    }

}