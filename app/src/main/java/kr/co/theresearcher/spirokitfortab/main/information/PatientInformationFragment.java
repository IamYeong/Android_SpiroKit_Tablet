package kr.co.theresearcher.spirokitfortab.main.information;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

import kr.co.theresearcher.spirokitfortab.OnItemChangedListener;
import kr.co.theresearcher.spirokitfortab.R;
import kr.co.theresearcher.spirokitfortab.SharedPreferencesManager;
import kr.co.theresearcher.spirokitfortab.db.SpiroKitDatabase;
import kr.co.theresearcher.spirokitfortab.db.cal_history.CalHistory;
import kr.co.theresearcher.spirokitfortab.db.human_race.HumanRace;
import kr.co.theresearcher.spirokitfortab.db.patient.Patient;
import kr.co.theresearcher.spirokitfortab.dialog.ConfirmDialog;
import kr.co.theresearcher.spirokitfortab.dialog.MeasSelectionDialog;
import kr.co.theresearcher.spirokitfortab.main.patients.OnItemSimpleSelectedListener;
import kr.co.theresearcher.spirokitfortab.main.patients.PatientsAdapter;
import kr.co.theresearcher.spirokitfortab.patient_input.PatientModifyActivity;

public class PatientInformationFragment extends Fragment implements Observer {

    private Context context;
    private Button startMeasButton;
    private EditText patientSearchField;

    private RecyclerView patientsRV, measurementsRV;
    private ImageButton dateRangeButton, modifyButton;
    private TextView patientNameText, patientInfoText, dateRangeText;
    private FrameLayout volumeFlowLayout, volumeTimeLayout;
    private FrameLayout patientsEmptyView;
    private CardView patientCard;
    private ConstraintLayout patientAreaLayout;

    private PatientsAdapter patientsAdapter;
    private MeasurementAdapter measurementAdapter;

    private OnCalHistorySelectedListener historySelectedListener;
    private ImageView informationExpandImage;
    private boolean isExpanded = true;
    private boolean isFocused = false;
    private InputMethodManager inputMethodManager;

    private SimpleDateFormat resultDateFormat = new SimpleDateFormat("yyyy.MM.dd", Locale.getDefault());
    private long minDate = Calendar.getInstance().getTime().getTime();
    private long maxDate = Calendar.getInstance().getTime().getTime();

    private Handler handler = new Handler(Looper.getMainLooper());

    private String doctorName = "";

    public PatientInformationFragment() {
        // Required empty public constructor
    }

    @Override
    public void update(Observable o, Object arg) {

        if ((Integer) arg == 404) {

            selectMeasurements();

        } else {
            // arg == 1

            if (isFocused) {

                isFocused = false;
                patientsRV.setVisibility(View.INVISIBLE);
                patientsEmptyView.setVisibility(View.INVISIBLE);
                patientsRV.setClickable(true);
                patientSearchField.clearFocus();
                patientAreaLayout.setClickable(true);
                inputMethodManager.hideSoftInputFromWindow(patientSearchField.getWindowToken(), 0);

            }

            Log.d(getClass().getSimpleName(), "INFO FRAGMENT : TOUCH");
        }


    }

    public void setHistorySelectedListener(OnCalHistorySelectedListener listener) {
        this.historySelectedListener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_patient_information, container, false);

        inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);

        patientAreaLayout = view.findViewById(R.id.constraint_patient_info_area);
        startMeasButton = view.findViewById(R.id.btn_test_start_patient_info);
        patientSearchField = view.findViewById(R.id.et_search_patient_info_fragment);
        patientsRV = view.findViewById(R.id.rv_patients_info_fragment);
        measurementsRV = view.findViewById(R.id.rv_test_history_patient_info_fragment);
        dateRangeButton = view.findViewById(R.id.img_btn_date_range_picker);
        modifyButton = view.findViewById(R.id.img_btn_edit_patient_info_fragment);
        patientNameText = view.findViewById(R.id.tv_name_patient_info_fragment);
        patientInfoText = view.findViewById(R.id.tv_content_patient_info_fragment);
        dateRangeText = view.findViewById(R.id.tv_date_range_patient_info_fragment);
        informationExpandImage = view.findViewById(R.id.img_expand_patient_info);
        patientsEmptyView = view.findViewById(R.id.frame_empty_patients_notification);
        patientCard = view.findViewById(R.id.card_patient_info);

        volumeFlowLayout = view.findViewById(R.id.frame_volume_flow_graph_result_fragment);
        volumeTimeLayout = view.findViewById(R.id.frame_volume_time_graph_result_fragment);

        patientsAdapter = new PatientsAdapter(container.getContext());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(container.getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        patientsAdapter.setOnItemChangedListener(new OnItemChangedListener() {
            @Override
            public void onChanged() {

                selectPatients();
                updatePatientInformation();
                selectMeasurements();

            }
        });

        modifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Patient patient = SpiroKitDatabase.getInstance(context).patientDao().selectPatientByHash(SharedPreferencesManager.getPatientHashed(context));

                if (patient.getIsDeleted() == 1) {

                    ConfirmDialog confirmDialog = new ConfirmDialog(context);
                    confirmDialog.setTitle(getString(R.string.please_select_patient));
                    confirmDialog.show();

                    return;
                }
                Intent intent = new Intent(context, PatientModifyActivity.class);
                startActivity(intent);
            }
        });

        patientCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isExpanded) {
                    updatePatientSimpleInfo();
                    informationExpandImage.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_baseline_keyboard_arrow_down_30_black));
                } else {
                    updatePatientInformation();
                    informationExpandImage.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_baseline_keyboard_arrow_up_30_black));
                }

                isExpanded = !isExpanded;

            }
        });

        patientsAdapter.setSimpleSelectedListener(new OnItemSimpleSelectedListener() {
            @Override
            public void onSimpleSelected() {

                isFocused = false;
                inputMethodManager.hideSoftInputFromWindow(patientSearchField.getWindowToken(), 0);

                patientsRV.setVisibility(View.INVISIBLE);
                patientsRV.setClickable(false);
                patientSearchField.clearFocus();

                if (isExpanded) updatePatientInformation();
                else updatePatientSimpleInfo();
                patientAreaLayout.setClickable(true);

                selectMeasurements();

            }
        });

        patientsRV.setLayoutManager(linearLayoutManager);
        patientsRV.setAdapter(patientsAdapter);

        measurementAdapter = new MeasurementAdapter(container.getContext());
        measurementAdapter.setSelectedListener(new OnCalHistorySelectedListener() {
            @Override
            public void onHistorySelected(CalHistory history) {

                historySelectedListener.onHistorySelected(history);

            }
        });

        measurementAdapter.setOnItemChangedListener(new OnItemChangedListener() {
            @Override
            public void onChanged() {

                selectMeasurements();

            }
        });

        linearLayoutManager = new LinearLayoutManager(container.getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        measurementsRV.setLayoutManager(linearLayoutManager);
        measurementsRV.setAdapter(measurementAdapter);

        patientSearchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                patientsAdapter.search(s.toString());

            }
        });

        patientSearchField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                //Log.d(getClass().getSimpleName(), "Has Focus : " + hasFocus);
            }
        });

        patientSearchField.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_UP) {

                    Log.d(getClass().getSimpleName(), "Patient edit text click");

                    patientAreaLayout.setClickable(false);
                    isFocused = true;
                    selectPatients();

                }

                return false;
            }
        });

        startMeasButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SpiroKitDatabase database = SpiroKitDatabase.getInstance(context);

                Patient patient = database.patientDao().selectPatientByHash(SharedPreferencesManager.getPatientHashed(context));
                int operatorCount = database.operatorDao().getItemCount();

                SpiroKitDatabase.removeInstance();

                if (patient == null) {
                    ConfirmDialog confirmDialog = new ConfirmDialog(context);
                    confirmDialog.setTitle(getString(R.string.please_select_patient));
                    confirmDialog.show();

                    return;
                }

                if (patient.getIsDeleted() == 1) {

                    ConfirmDialog confirmDialog = new ConfirmDialog(context);
                    confirmDialog.setTitle(getString(R.string.please_select_patient));
                    confirmDialog.show();

                    return;
                }

                if (operatorCount == 0) {

                    ConfirmDialog confirmDialog = new ConfirmDialog(context);
                    confirmDialog.setTitle(getString(R.string.please_add_operator));
                    confirmDialog.show();

                    return;

                }

                MeasSelectionDialog dialog = new MeasSelectionDialog(context);
                dialog.show();

            }
        });



        dateRangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDatePicker<Pair<Long, Long>> materialDatePicker = MaterialDatePicker.Builder.dateRangePicker()
                        .setTitleText(getString(R.string.select_history_range))


                        .build();

                materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
                    @Override
                    public void onPositiveButtonClick(Pair<Long, Long> selection) {

                        if (!measurementAdapter.searchMeasInRange(selection.first, selection.second)) {
                            //Toast.makeText(context, getString(R.string.time_select_error), Toast.LENGTH_SHORT).show();
                            measurementAdapter.initializing();
                            measurementAdapter.notifyDataSetChanged();
                        } else {
                            measurementAdapter.notifyDataSetChanged();
                            dateRangeText.setText(getString(R.string.date_to_date, resultDateFormat.format(selection.first), resultDateFormat.format(selection.second)));
                            //testsPeriodText.setText(simpleDateFormat.format(selection.first) + " ~ " + simpleDateFormat.format(selection.second));
                        }

                    }
                });

                materialDatePicker.show(getActivity().getSupportFragmentManager(), "DATE_PICKER");

            }
        });


        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.e(getClass().getSimpleName(), "PatientFragment Resume");
        //selectPatients();
        selectMeasurements();
        updateUI();

    }

    private void selectPatients() {

        Thread thread = new Thread() {

            @Override
            public void run() {
                super.run();
                Looper.prepare();

                SpiroKitDatabase database = SpiroKitDatabase.getInstance(context);
                List<Patient> patientList = database.patientDao().selectPatientByOffice(SharedPreferencesManager.getOfficeHash(context));
                SpiroKitDatabase.removeInstance();

                patientsAdapter.setPatients(patientList);

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (patientList.size() > 0) {
                            patientsEmptyView.setVisibility(View.INVISIBLE);
                            patientsRV.setVisibility(View.VISIBLE);
                            patientsRV.setClickable(true);
                            updateUI();
                        } else {
                            patientsEmptyView.setVisibility(View.VISIBLE);
                            patientInfoText.setText("");
                            patientNameText.setText("N/A");

                        }
                        patientsAdapter.notifyDataSetChanged();
                    }
                });

                Looper.loop();

            }
        };

        thread.start();


    }

    private void selectMeasurements() {

        Log.e(getClass().getSimpleName(), "SELECT MEASUREMENTS");

        Thread thread = new Thread() {

            @Override
            public void run() {
                super.run();
                Looper.prepare();

                List<CalHistory> histories = SpiroKitDatabase.getInstance(context).calHistoryDao().selectHistoryByPatient(SharedPreferencesManager.getPatientHashed(context));
                SpiroKitDatabase.removeInstance();

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

                for (CalHistory history : histories) {

                    //yyyy-MM-dd HH:mm:ss.SSSSSS
                    String stringDate = history.getFinishDate();
                    stringDate = stringDate.substring(0, stringDate.length() - 7);

                    try {

                        long date = simpleDateFormat.parse(stringDate).getTime();
                        history.setTimestamp(date);
                        if (minDate > date) minDate = date;
                        if (maxDate < date) maxDate = date;

                    } catch (ParseException e) {
                        Log.e(getClass().getSimpleName(), e.toString());
                    }

                }

                measurementAdapter.setCalHistories(histories);

                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        measurementAdapter.notifyDataSetChanged();
                        dateRangeText.setText(getString(R.string.date_to_date, resultDateFormat.format(minDate), resultDateFormat.format(maxDate)));
                    }
                });

                Looper.loop();

            }
        };

        thread.start();
    }

    private void updateUI() {
        if (isExpanded) updatePatientInformation();
        else updatePatientSimpleInfo();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void updatePatientInformation() {

        SpiroKitDatabase database = SpiroKitDatabase.getInstance(context);
        Patient patient = database.patientDao()
                .selectPatientByHash(SharedPreferencesManager.getPatientHashed(context));
        //Log.e(getClass().getSimpleName(), patient.toString());
        SpiroKitDatabase.removeInstance();
        List<HumanRace> humanRaces = database.humanRaceDao().selectAllHumanRace();
        SpiroKitDatabase.removeInstance();

        if (patient == null) {
            patientNameText.setText(getString(R.string.not_applicable));
            patientInfoText.setText(getString(R.string.please_select_patient));
            return;
        }

        if (patient.getIsDeleted() == 1) {

            patientNameText.setText(getString(R.string.not_applicable));
            patientInfoText.setText(getString(R.string.please_select_patient));
            return;
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        SimpleDateFormat birthDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        patientNameText.setText(patient.getName());
        StringBuilder info = new StringBuilder();

        info.append(getString(R.string.chart_number_is, patient.getChartNumber())).append("\n");
        if (patient.getGender().equals("m")) {
            info.append(getString(R.string.gender_is, getString(R.string.male))).append("\n");
        } else {
            info.append(getString(R.string.gender_is, getString(R.string.female))).append("\n");
        }
        info.append(getString(R.string.height_is, patient.getHeight())).append("\n");
        info.append(getString(R.string.weight_is, patient.getWeight())).append("\n");

        String birthString = patient.getBirthDay();

        try {
            long date = simpleDateFormat.parse(birthString).getTime();
            info.append(getString(R.string.birth_is, birthDateFormat.format(date))).append("\n");
        } catch (ParseException e) {
            Log.e(getClass().getSimpleName(), e.toString());
        }

        //인종
        String humanRace = patient.getHumanRace();
        String[] humanResources = getResources().getStringArray(R.array.human_races);
        for (int i = 0; i < humanRaces.size(); i++) {
            HumanRace race = humanRaces.get(i);
            if (race.getRace().equals(humanRace)) {
                info.append(getString(R.string.human_race_is, humanResources[i])).append("\n");
                break;
            }
        }

        String startSmokingDateString = patient.getStartSmokingDay();
        if ((startSmokingDateString == null) || (startSmokingDateString.equals("null")) || (startSmokingDateString.isEmpty())) {
            info.append(getString(R.string.smoke_start_date_is, getString(R.string.not_applicable))).append("\n");
        } else {
            info.append(getString(R.string.smoke_start_date_is, startSmokingDateString.substring(0, startSmokingDateString.length() - 9))).append("\n");
        }

        String smokingAmount = patient.getSmokingAmountPerDay();
        if ((startSmokingDateString == null) || (startSmokingDateString.equals("null")) || (startSmokingDateString.isEmpty())) {
            info.append(getString(R.string.smoking_per_day_is_na, getString(R.string.not_applicable))).append("\n");
        } else {
            info.append(getString(R.string.smoking_per_day_is, smokingAmount)).append("\n");
        }

        if (patient.getNowSmoking() == 0) {
            info.append(getString(R.string.smoking_now_is, getString(R.string.not_smoking))).append("\n");
        } else {
            info.append(getString(R.string.smoking_now_is, getString(R.string.smoking))).append("\n");
        }

        String stopSmokingDateString = patient.getStopSmokingDay();
        if ((stopSmokingDateString == null) || (stopSmokingDateString.equals("null")) || (stopSmokingDateString.isEmpty())) {
            info.append(getString(R.string.smoke_stop_date_is, getString(R.string.not_applicable))).append("\n");
        } else {
            info.append(getString(R.string.smoke_stop_date_is, stopSmokingDateString.subSequence(0, stopSmokingDateString.length() - 9))).append("\n");
        }


        //info.append(getString(R.string.human_race_is, humanRace)).append("\n")

        Log.d(getClass().getSimpleName(), info.toString());
        patientInfoText.setText(info.toString());
        patientInfoText.setVisibility(View.VISIBLE);

    }

    private void updatePatientSimpleInfo() {

        //SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd", Locale.getDefault());

        Patient patient = SpiroKitDatabase.getInstance(context).patientDao()
                .selectPatientByHash(SharedPreferencesManager.getPatientHashed(context));

        if (patient == null) {
            patientNameText.setText(getString(R.string.not_applicable));
            patientInfoText.setText(getString(R.string.please_select_patient));
            return;
        }

        if (patient.getIsDeleted() == 1) {

            patientNameText.setText(getString(R.string.not_applicable));
            patientInfoText.setText(getString(R.string.please_select_patient));
            return;
        }

        patientNameText.setText(patient.getName());

        patientInfoText.setVisibility(View.GONE);
    }

    public void changeSearchFieldFocus(boolean enable) {

        if (enable) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    patientSearchField.requestFocus();
                }
            });
        } else {
            patientSearchField.clearFocus();
        }

    }

}