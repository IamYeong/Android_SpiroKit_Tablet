package kr.co.theresearcher.spirokitfortab.main.information;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;
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

import java.text.SimpleDateFormat;
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
    private TextView patientNameText, patientInfoText, dateRangeText, patientsEmptyText, measurementsEmptyText;
    private FrameLayout volumeFlowLayout, volumeTimeLayout;
    private CardView patientCard;

    private PatientsAdapter patientsAdapter;
    private MeasurementAdapter measurementAdapter;

    private OnCalHistorySelectedListener historySelectedListener;
    private ImageView informationExpandImage;
    private boolean isExpanded = true;
    private boolean isFocused = false;
    private InputMethodManager inputMethodManager;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd", Locale.getDefault());
    private long minDate = Calendar.getInstance().getTime().getTime();
    private long maxDate = Calendar.getInstance().getTime().getTime();

    private Handler handler = new Handler(Looper.getMainLooper());

    private String doctorName = "";

    public PatientInformationFragment() {
        // Required empty public constructor
    }

    @Override
    public void update(Observable o, Object arg) {

        if (isFocused) {

            isFocused = false;
            patientsRV.setVisibility(View.INVISIBLE);
            patientsEmptyText.setVisibility(View.INVISIBLE);
            patientsRV.setClickable(true);
            patientSearchField.clearFocus();

            inputMethodManager.hideSoftInputFromWindow(patientSearchField.getWindowToken(), 0);

        }

        Log.d(getClass().getSimpleName(), "INFO FRAGMENT : TOUCH");
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
        patientsEmptyText = view.findViewById(R.id.tv_empty_patients_notification);
        measurementsEmptyText = view.findViewById(R.id.tv_empty_measurements_notification);
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

            }
        });

        modifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        Looper.prepare();

                        List<CalHistory> histories = new ArrayList<>();

                        measurementAdapter.setCalHistories(histories);

                        handler.post(new Runnable() {
                            @Override
                            public void run() {

                                if (histories.size() > 0) {
                                    measurementsEmptyText.setVisibility(View.INVISIBLE);
                                    historySelectedListener.onHistorySelected(histories.get(histories.size() - 1));
                                } else {
                                    measurementsEmptyText.setVisibility(View.VISIBLE);
                                    historySelectedListener.onHistorySelected(null);
                                }

                                measurementAdapter.notifyDataSetChanged();
                            }
                        });

                        Looper.loop();
                    }
                };
                thread.start();

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

                    isFocused = true;
                    selectPatients();

                }

                return false;
            }
        });

        startMeasButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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

                        System.out.println(simpleDateFormat.format(selection.first));
                        System.out.println(simpleDateFormat.format(selection.second));

                        if (!measurementAdapter.searchMeasInRange(selection.first, selection.second)) {
                            //Toast.makeText(context, getString(R.string.time_select_error), Toast.LENGTH_SHORT).show();
                            measurementAdapter.initializing();
                            measurementAdapter.notifyDataSetChanged();
                        } else {
                            measurementAdapter.notifyDataSetChanged();
                            dateRangeText.setText(getString(R.string.date_to_date, simpleDateFormat.format(selection.first), simpleDateFormat.format(selection.second)));
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

                patientsAdapter.setPatients(patientList);

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (patientList.size() > 0) {
                            patientsEmptyText.setVisibility(View.INVISIBLE);
                            patientsRV.setVisibility(View.VISIBLE);
                            patientsRV.setClickable(true);
                            updateUI();
                        } else {
                            patientsEmptyText.setVisibility(View.VISIBLE);
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
        Thread thread = new Thread() {

            @Override
            public void run() {
                super.run();
                Looper.prepare();

                List<CalHistory> histories = new ArrayList<>();

                for (CalHistory history : histories) {
                    //if (minDate > measurement.getMeasDate()) minDate = measurement.getMeasDate();
                    //if (maxDate < measurement.getMeasDate()) maxDate = measurement.getMeasDate();
                }

                measurementAdapter.setCalHistories(histories);

                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        if (histories.size() > 0) {
                            measurementsEmptyText.setVisibility(View.INVISIBLE);
                            historySelectedListener.onHistorySelected(histories.get(histories.size() - 1));
                        } else {
                            measurementsEmptyText.setVisibility(View.VISIBLE);
                            historySelectedListener.onHistorySelected(null);
                        }
                        measurementAdapter.notifyDataSetChanged();
                        dateRangeText.setText(getString(R.string.date_to_date, simpleDateFormat.format(minDate), simpleDateFormat.format(maxDate)));
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

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd", Locale.getDefault());

        patientNameText.setText(SharedPreferencesManager.getPatientName(context));
        StringBuilder info = new StringBuilder();

        info.append(getString(R.string.chart_number_is, SharedPreferencesManager.getPatientChartNumber(context))).append("\n");
        if (SharedPreferencesManager.getPatientGender(context).equals("m")) {
            info.append(getString(R.string.gender_is, getString(R.string.male))).append("\n");
        } else {
            info.append(getString(R.string.gender_is, getString(R.string.female))).append("\n");
        }
        info.append(getString(R.string.height_is, SharedPreferencesManager.getPatientHeight(context))).append("\n");
        info.append(getString(R.string.weight_is, SharedPreferencesManager.getPatientWeight(context))).append("\n");
        info.append(getString(R.string.birth_is, SharedPreferencesManager.getPatientBirthday(context))).append("\n");

        patientInfoText.setText(info.toString());


    }

    private void updatePatientSimpleInfo() {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd", Locale.getDefault());

        patientNameText.setText(SharedPreferencesManager.getPatientName(context));
        StringBuilder info = new StringBuilder();

        /*

        info.append(getString(R.string.chart_number_for_input, SharedPreferencesManager.getPatientChartNumber(context))).append("\n");
        if (SharedPreferencesManager.getPatientGender(context)) {
            info.append(getString(R.string.gender_for_input, getString(R.string.male))).append("\n");
        } else {
            info.append(getString(R.string.gender_for_input, getString(R.string.female))).append("\n");
        }
        info.append(getString(R.string.height_for_input, SharedPreferencesManager.getPatientHeight(context))).append("\n");
        info.append(getString(R.string.weight_for_input, SharedPreferencesManager.getPatientWeight(context))).append("\n");
        info.append(getString(R.string.birth_for_input, simpleDateFormat.format(SharedPreferencesManager.getPatientBirth(context)))).append("\n");

         */
        patientInfoText.setText(info.toString());
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