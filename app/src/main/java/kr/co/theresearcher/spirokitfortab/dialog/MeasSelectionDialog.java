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

import java.sql.Array;
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
    private AppCompatSpinner workSpinnerCheckupDoctor,
    operatorSpinnerFamilyDoctor, operatorSpinnerCheckupDoctor;
    private ImageButton closeButton;

    private ArrayAdapter<String> jobArrayAdapter;
    private ArrayAdapter<String> checkupArrayAdapter, familyArrayAdapter;

    private List<Work> works;
    private String[] workNames;

    private Handler handler = new Handler(Looper.getMainLooper());
    private List<Operator> checkupOperators = new ArrayList<>();
    private List<Operator> familyDoctorOperators = new ArrayList<>();

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

        SharedPreferencesManager.setFamilyDoctorHash(getContext(), null);
        SharedPreferencesManager.setOperatorHash(getContext(), null);

        workSpinnerCheckupDoctor = findViewById(R.id.spinner_work_group_checkup_doctor_dialog);
        operatorSpinnerFamilyDoctor = findViewById(R.id.spinner_operator_group_family_doctor_dialog);
        operatorSpinnerCheckupDoctor = findViewById(R.id.spinner_operator_group_checkup_doctor_dialog);
        
        fvcButton = findViewById(R.id.btn_fvc_meas_selection_dialog);
        svcButton = findViewById(R.id.btn_svc_meas_selection_dialog);
        closeButton = findViewById(R.id.img_btn_close_meas_selection_dialog);

        works = SpiroKitDatabase.getInstance(getContext()).workDao().selectAllWork();

        workNames = getContext().getResources().getStringArray(R.array.works);

        jobArrayAdapter = new ArrayAdapter<String>(
                getContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, workNames
        );

        workSpinnerCheckupDoctor.setAdapter(jobArrayAdapter);

        checkupArrayAdapter = new ArrayAdapter<String>(
                getContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, new String[]{}
        );

        familyDoctorOperators = SpiroKitDatabase.getInstance(getContext()).operatorDao().selectOperatorByWork(SharedPreferencesManager.getOfficeHash(getContext()), "doctor");
        List<String> familyOperatorNames = new ArrayList<>();
        for (Operator operator : familyDoctorOperators) familyOperatorNames.add(operator.getName());
        familyArrayAdapter = new ArrayAdapter<String>(
                getContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, familyOperatorNames
        );

        operatorSpinnerFamilyDoctor.setAdapter(familyArrayAdapter);
        operatorSpinnerCheckupDoctor.setAdapter(checkupArrayAdapter);


        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dismiss();
            }
        });

        operatorSpinnerFamilyDoctor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Log.e(getClass().getSimpleName(), position + " SELECTED");
                SharedPreferencesManager.setFamilyDoctorHash(getContext(), familyDoctorOperators.get(position).getHashed());

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        
        operatorSpinnerCheckupDoctor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Log.e(getClass().getSimpleName(), position + " SELECTED");
                SharedPreferencesManager.setOperatorHash(getContext(), checkupOperators.get(position).getHashed());

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        
        workSpinnerCheckupDoctor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String selectedWork = works.get(position).getWork();
                checkupOperators = SpiroKitDatabase.getInstance(getContext()).operatorDao().selectOperatorByWork(SharedPreferencesManager.getOfficeHash(getContext()), selectedWork);

                List<String> checkupOperatorNames = new ArrayList<>();
                for (Operator operator : checkupOperators) checkupOperatorNames.add(operator.getName());
                checkupArrayAdapter = new ArrayAdapter<String>(
                        getContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, checkupOperatorNames
                );
                operatorSpinnerCheckupDoctor.setAdapter(checkupArrayAdapter);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        fvcButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (SharedPreferencesManager.getOperatorHash(getContext()) == null) {

                    ConfirmDialog confirmDialog = new ConfirmDialog(getContext());
                    confirmDialog.setTitle(getContext().getString(R.string.not_selected_checkup_doctor));
                    confirmDialog.show();

                    return;
                }

                Intent intent = new Intent(getContext(), MeasurementFvcActivity.class);
                getContext().startActivity(intent);
                dismiss();

            }
        });

        svcButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (SharedPreferencesManager.getOperatorHash(getContext()) == null) {

                    ConfirmDialog confirmDialog = new ConfirmDialog(getContext());
                    confirmDialog.setTitle(getContext().getString(R.string.not_selected_checkup_doctor));
                    confirmDialog.show();

                    return;
                }

                Intent intent = new Intent(getContext(), MeasurementSvcActivity.class);
                getContext().startActivity(intent);
                dismiss();

            }
        });

    }



}
