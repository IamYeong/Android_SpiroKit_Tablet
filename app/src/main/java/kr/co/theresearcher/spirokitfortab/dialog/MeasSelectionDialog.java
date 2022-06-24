package kr.co.theresearcher.spirokitfortab.dialog;

import android.app.Dialog;
import android.content.AbstractThreadedSyncAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.room.Room;

import com.google.android.material.navigation.NavigationView;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import kr.co.theresearcher.spirokitfortab.R;
import kr.co.theresearcher.spirokitfortab.SharedPreferencesManager;
import kr.co.theresearcher.spirokitfortab.db.RoomNames;
import kr.co.theresearcher.spirokitfortab.db.operator.Operator;
import kr.co.theresearcher.spirokitfortab.db.operator.OperatorDao;
import kr.co.theresearcher.spirokitfortab.db.operator.OperatorDatabase;
import kr.co.theresearcher.spirokitfortab.db.work.Work;
import kr.co.theresearcher.spirokitfortab.measurement.fvc.MeasurementFvcActivity;
import kr.co.theresearcher.spirokitfortab.measurement.svc.MeasurementSvcActivity;

public class MeasSelectionDialog extends Dialog {

    private Button fvcButton, svcButton;
    private AppCompatSpinner jobSpinner, operatorSpinner;
    private TextView matchDoctorText;

    private ArrayAdapter<String> jobArrayAdapter;
    private ArrayAdapter<String> operatorArrayAdapter;

    private Handler handler = new Handler(Looper.getMainLooper());
    private List<String> operatorNames = new ArrayList<>();
    private List<Operator> operators = new ArrayList<>();

    public MeasSelectionDialog(@NonNull Context context) {
        super(context);

        List<String> jobsName = new ArrayList<>();
        for (Work work : Work.values()) jobsName.add(work.name().toUpperCase(Locale.ROOT));

        jobArrayAdapter = new ArrayAdapter<String>(
                getContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, jobsName
        );

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_meas_selection);

        fvcButton = findViewById(R.id.btn_fvc_meas_selection_dialog);
        svcButton = findViewById(R.id.btn_svc_meas_selection_dialog);
        jobSpinner = findViewById(R.id.spinner_operate_group);
        operatorSpinner = findViewById(R.id.spinner_operator_group);

        jobSpinner.setAdapter(jobArrayAdapter);

        operatorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                SharedPreferencesManager.setOperatorID(getContext(), operators.get(position).getOfficeID());

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        jobSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Thread thread = new Thread() {

                    @Override
                    public void run() {
                        super.run();
                        Looper.prepare();

                        OperatorDatabase database = Room.databaseBuilder(getContext(), OperatorDatabase.class, RoomNames.ROOM_OPERATOR_DB_NAME).build();
                        OperatorDao operatorDao = database.operatorDao();

                        operatorNames.clear();
                        for (Operator operator : operatorDao.selectByOfficeID(SharedPreferencesManager.getOfficeID(getContext()))) {
                            if (operator.getWorkID() == position) {
                                operators.add(operator);
                                operatorNames.add(operator.getName());
                            }
                        }

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
