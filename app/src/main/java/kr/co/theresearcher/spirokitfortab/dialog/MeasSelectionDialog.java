package kr.co.theresearcher.spirokitfortab.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.room.Room;

import java.util.ArrayList;
import java.util.List;

import kr.co.theresearcher.spirokitfortab.R;
import kr.co.theresearcher.spirokitfortab.SharedPreferencesManager;

import kr.co.theresearcher.spirokitfortab.db.SpiroKitDatabase;
import kr.co.theresearcher.spirokitfortab.db.operator.Operator;
import kr.co.theresearcher.spirokitfortab.db.work.Work;
import kr.co.theresearcher.spirokitfortab.measurement.fvc.MeasurementFvcActivity;
import kr.co.theresearcher.spirokitfortab.measurement.svc.MeasurementSvcActivity;
import kr.co.theresearcher.spirokitfortab.setting.operator.OperatorActivity;

public class MeasSelectionDialog extends Dialog {

    private Button fvcButton, svcButton;
    private AppCompatSpinner workSpinnerFamilyDoctor, workSpinnerCheckupDoctor,
    operatorSpinnerFamilyDoctor, operatorSpinnerCheckupDoctor;
    private TextView matchDoctorText;
    private ImageButton closeButton;

    private ArrayAdapter<String> jobArrayAdapter;
    private ArrayAdapter<String> operatorArrayAdapter;

    private List<Work> works;

    private Handler handler = new Handler(Looper.getMainLooper());
    private List<Operator> operators;
    private List<Operator> familyDoctorOperators;

    public MeasSelectionDialog(@NonNull Context context) {
        super(context);


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawableResource(R.color.transparent);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setContentView(R.layout.dialog_meas_selection);
        setCancelable(false);

        workSpinnerFamilyDoctor = findViewById(R.id.spinner_work_group_family_doctor_dialog);
        workSpinnerCheckupDoctor = findViewById(R.id.spinner_work_group_checkup_doctor_dialog);
        operatorSpinnerFamilyDoctor = findViewById(R.id.spinner_operator_group_family_doctor_dialog);
        operatorSpinnerCheckupDoctor = findViewById(R.id.spinner_operator_group_checkup_doctor_dialog);
        
        fvcButton = findViewById(R.id.btn_fvc_meas_selection_dialog);
        svcButton = findViewById(R.id.btn_svc_meas_selection_dialog);
        closeButton = findViewById(R.id.img_btn_close_meas_selection_dialog);

        works = SpiroKitDatabase.getInstance(getContext()).workDao().selectAllWork();
        List<String> workNames = new ArrayList<>();
        for (Work work : works) workNames.add(work.getWork());

        jobArrayAdapter = new ArrayAdapter<String>(
                getContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, workNames
        );
        
        workSpinnerFamilyDoctor.setAdapter(jobArrayAdapter);
        workSpinnerCheckupDoctor.setAdapter(jobArrayAdapter);


        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dismiss();
            }
        });

        operatorSpinnerFamilyDoctor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Log.d(getClass().getSimpleName(), familyDoctorOperators.get(position).getName() + " SELECTED");
                SharedPreferencesManager.setFamilyDoctorHash(getContext(), familyDoctorOperators.get(position).getHashed());

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        
        operatorSpinnerCheckupDoctor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Log.d(getClass().getSimpleName(), operators.get(position).getName() + " SELECTED");
                SharedPreferencesManager.setOperatorHash(getContext(), operators.get(position).getHashed());

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        workSpinnerFamilyDoctor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String selectedWork = works.get(position).getWork();
                familyDoctorOperators = SpiroKitDatabase.getInstance(getContext()).operatorDao().selectOperatorByWork(SharedPreferencesManager.getOfficeHash(getContext()), selectedWork);

                List<String> operatorNames = new ArrayList<>();
                for (Operator operator : familyDoctorOperators) operatorNames.add(operator.getName());
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                        getContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, operatorNames
                );
                operatorSpinnerFamilyDoctor.setAdapter(arrayAdapter);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        
        workSpinnerCheckupDoctor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String selectedWork = works.get(position).getWork();
                operators = SpiroKitDatabase.getInstance(getContext()).operatorDao().selectOperatorByWork(SharedPreferencesManager.getOfficeHash(getContext()), selectedWork);

                List<String> operatorNames = new ArrayList<>();
                for (Operator operator : operators) operatorNames.add(operator.getName());
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                        getContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, operatorNames
                );
                operatorSpinnerCheckupDoctor.setAdapter(arrayAdapter);


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        fvcButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getContext(), MeasurementFvcActivity.class);
                getContext().startActivity(intent);
                dismiss();

            }
        });

        svcButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getContext(), MeasurementSvcActivity.class);
                getContext().startActivity(intent);
                dismiss();

            }
        });

    }



}
