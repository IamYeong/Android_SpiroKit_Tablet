package kr.co.theresearcher.spirokitfortab.main.information;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import kr.co.theresearcher.spirokitfortab.R;
import kr.co.theresearcher.spirokitfortab.db.RoomNames;
import kr.co.theresearcher.spirokitfortab.db.patient.Patient;
import kr.co.theresearcher.spirokitfortab.db.patient.PatientDao;
import kr.co.theresearcher.spirokitfortab.db.patient.PatientDatabase;
import kr.co.theresearcher.spirokitfortab.main.patients.PatientsAdapter;
import kr.co.theresearcher.spirokitfortab.measurement.fvc.MeasurementFvcActivity;

public class PatientInformationFragment extends Fragment {

    private Context context;
    private Button startMeasButton;
    private EditText patientSearchField;

    private RecyclerView patientsRV, measurementsRV;
    private ImageButton dateRangeButton, modifyButton;
    private TextView patientNameText, patientInfoText, dateRangeText;

    private PatientsAdapter patientsAdapter;

    private Handler handler = new Handler(Looper.getMainLooper());

    public PatientInformationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_patient_information, container, false);

        startMeasButton = view.findViewById(R.id.btn_test_start_patient_info);
        patientSearchField = view.findViewById(R.id.et_search_patient_info_fragment);
        patientsRV = view.findViewById(R.id.rv_patients_info_fragment);
        measurementsRV = view.findViewById(R.id.rv_test_history_patient_info_fragment);
        dateRangeButton = view.findViewById(R.id.img_btn_date_range_picker);
        modifyButton = view.findViewById(R.id.img_btn_edit_patient_info_fragment);
        patientNameText = view.findViewById(R.id.tv_name_patient_info_fragment);
        patientInfoText = view.findViewById(R.id.tv_content_patient_info_fragment);
        dateRangeText = view.findViewById(R.id.tv_date_range_patient_info_fragment);

        patientsAdapter = new PatientsAdapter(context);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        patientsRV.setLayoutManager(linearLayoutManager);
        patientsRV.setAdapter(patientsAdapter);



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
                if (hasFocus) {
                    patientsRV.setVisibility(View.VISIBLE);
                    patientsRV.setClickable(true);
                }
            }
        });


        startMeasButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MeasurementFvcActivity.class);
                startActivity(intent);
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

        Thread thread = new Thread() {

            @Override
            public void run() {
                super.run();
                Looper.prepare();

                PatientDatabase database = Room.databaseBuilder(context, PatientDatabase.class, RoomNames.ROOM_PATIENT_DB_NAME).build();
                PatientDao patientDao = database.patientDao();
                List<Patient> patientList = patientDao.selectAllPatient();
                database.close();

                patientsAdapter.setPatients(patientList);

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        patientsAdapter.notifyDataSetChanged();
                    }
                });

                Looper.loop();
            }
        };

        thread.start();

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}