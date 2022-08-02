package kr.co.theresearcher.spirokitfortab.main.result.empty;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import kr.co.theresearcher.spirokitfortab.R;
import kr.co.theresearcher.spirokitfortab.SharedPreferencesManager;
import kr.co.theresearcher.spirokitfortab.db.SpiroKitDatabase;
import kr.co.theresearcher.spirokitfortab.db.cal_history.CalHistory;
import kr.co.theresearcher.spirokitfortab.db.operator.Operator;
import kr.co.theresearcher.spirokitfortab.db.patient.Patient;
import kr.co.theresearcher.spirokitfortab.patient_input.PatientInsertActivity;
import kr.co.theresearcher.spirokitfortab.setting.operator.OperatorActivity;

public class EmptyResultFragment extends Fragment {

    private ImageView emptyImage;
    private Button emptyButton;
    private TextView emptyText;

    private Context context;

    private Handler handler = new Handler(Looper.getMainLooper());

    public EmptyResultFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_empty_result, container, false);
        emptyImage = view.findViewById(R.id.img_empty_fragment);
        emptyButton = view.findViewById(R.id.btn_empty_fragment);
        emptyText = view.findViewById(R.id.tv_empty_fragment);


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

                SpiroKitDatabase db = SpiroKitDatabase.getInstance(context);
                List<Operator> operators = db.operatorDao().selectAllOperator(SharedPreferencesManager.getOfficeHash(context));
                if (operators.size() == 0) {

                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            emptyImage.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.empty_operator));
                            emptyText.setText(getString(R.string.empty_operators));

                            emptyButton.setText(getString(R.string.go_to_add_operator));
                            emptyButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(context, OperatorActivity.class);
                                    startActivity(intent);
                                }
                            });


                        }
                    });

                    return;
                }

                List<Patient> patients = db.patientDao().selectAll(SharedPreferencesManager.getOfficeHash(context));
                if (patients.size() == 0) {

                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            emptyImage.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.empty_patient));
                            emptyText.setText(getString(R.string.empty_patients));

                            emptyButton.setText(getString(R.string.go_to_add_patient));
                            emptyButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(context, PatientInsertActivity.class);
                                    startActivity(intent);
                                }
                            });

                        }
                    });

                    return;
                }

                if (SharedPreferencesManager.getPatientHashed(context) == null) {

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            emptyImage.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.empty_selected));
                            emptyText.setText(getString(R.string.empty_patient_select));
                            emptyButton.setVisibility(View.GONE);


                        }
                    });

                    return;
                }

                List<CalHistory> histories = db.calHistoryDao().selectHistoryByPatient(SharedPreferencesManager.getPatientHashed(context));
                if (histories.size() == 0) {

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            emptyImage.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.empty_cal));
                            emptyText.setText(getString(R.string.empty_cal));
                            emptyButton.setVisibility(View.GONE);
                        }
                    });

                    return;
                }

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