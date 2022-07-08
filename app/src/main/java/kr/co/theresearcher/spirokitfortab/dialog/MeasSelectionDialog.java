package kr.co.theresearcher.spirokitfortab.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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

import kr.co.theresearcher.spirokitfortab.db.operator.Operator;
import kr.co.theresearcher.spirokitfortab.db.work.Work;
import kr.co.theresearcher.spirokitfortab.measurement.fvc.MeasurementFvcActivity;
import kr.co.theresearcher.spirokitfortab.measurement.svc.MeasurementSvcActivity;

public class MeasSelectionDialog extends Dialog {

    private Button fvcButton, svcButton;
    private AppCompatSpinner jobSpinner, operatorSpinner;
    private TextView matchDoctorText;
    private ImageButton closeButton;

    private ArrayAdapter<String> jobArrayAdapter;
    private ArrayAdapter<String> operatorArrayAdapter;

    private Handler handler = new Handler(Looper.getMainLooper());
    private List<Operator> operators;
    private List<Operator> sortedOperators = new ArrayList<>();

    public MeasSelectionDialog(@NonNull Context context) {
        super(context);

        Thread thread = new Thread() {

            @Override
            public void run() {
                super.run();
                Looper.prepare();

                //operators = OperatorDatabase.getInstance(getContext()).operatorDao().selectAllOperator(SharedPreferencesManager.getOfficeID(getContext()));

                Looper.loop();
            }
        };
        thread.start();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawableResource(R.color.transparent);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setContentView(R.layout.dialog_meas_selection);
        setCancelable(false);

        fvcButton = findViewById(R.id.btn_fvc_meas_selection_dialog);
        svcButton = findViewById(R.id.btn_svc_meas_selection_dialog);
        jobSpinner = findViewById(R.id.spinner_operate_group);
        operatorSpinner = findViewById(R.id.spinner_operator_group);
        closeButton = findViewById(R.id.img_btn_close_meas_selection_dialog);

        //Work[] works = Work.values();
        String[] workNames = new String[0];
        //for (int i = 0; i < works.length; i++) workNames[i] = works[i].name();

        jobArrayAdapter = new ArrayAdapter<String>(
                getContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, workNames
        );

        jobSpinner.setAdapter(jobArrayAdapter);




        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dismiss();
            }
        });

        operatorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                SharedPreferencesManager.setOperatorHash(getContext(), sortedOperators.get(position).getHashed());

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        jobSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                //Work selectedWork = Work.values()[position];
                //updateOperators(selectedWork.ordinal());

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

    private void updateOperators(int workID) {

        Thread thread = new Thread() {

            @Override
            public void run() {
                super.run();
                Looper.prepare();

                sortedOperators.clear();
                for (int i = 0; i < operators.size(); i++) {
                    //if (operators.get(i).getWorkID() == workID) sortedOperators.add(operators.get(i));
                }

                String[] operatorNames = new String[sortedOperators.size()];
                for (int i = 0; i < sortedOperators.size(); i++) operatorNames[i] = sortedOperators.get(i).getName();
                operatorArrayAdapter = new ArrayAdapter<String>(
                        getContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, operatorNames
                );

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        operatorSpinner.setAdapter(operatorArrayAdapter);
                    }
                });

                Looper.loop();
            }
        };
        thread.start();

    }



}
