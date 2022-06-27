package kr.co.theresearcher.spirokitfortab.main.patients;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import kr.co.theresearcher.spirokitfortab.R;
import kr.co.theresearcher.spirokitfortab.SharedPreferencesManager;
import kr.co.theresearcher.spirokitfortab.db.patient.Patient;

public class PatientsAdapter extends RecyclerView.Adapter<PatientsViewHolder> {

    private List<Patient> patients;
    private List<Patient> searchResults;
    private Context context;
    private OnItemSimpleSelectedListener simpleSelectedListener;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd", Locale.getDefault());

    public PatientsAdapter(Context context) {
        this.context = context;
        patients = new ArrayList<>();
        searchResults = new ArrayList<>();
    }

    public void setSimpleSelectedListener(OnItemSimpleSelectedListener listener) {
        this.simpleSelectedListener = listener;
    }

    public void setPatients(List<Patient> patientList) {
        this.patients.clear();
        this.searchResults.clear();
        this.patients.addAll(patientList);
        this.searchResults.addAll(patientList);
    }

    public void search(String keyword) {

        searchResults.clear();

        if (keyword.length() == 0) {
            searchResults.addAll(patients);
        } else {

            searchResults = patients.stream()
                    .filter(patient -> patient.getName().toLowerCase(Locale.ROOT).contains(keyword.toLowerCase(Locale.ROOT)))
                    .collect(Collectors.toList());

        }

        notifyDataSetChanged();

    }

    public void sortById() {

        for (int i = searchResults.size() - 1; i >= 0; i--) {

            for (int j = 1; j <= i; j++) {

                Patient patient = searchResults.get(j);
                if (searchResults.get(j - 1).getId() < patient.getId()) {

                    searchResults.set(j, searchResults.get(j - 1));
                    searchResults.set(j - 1, patient);

                }


            }

        }

    }


    @NonNull
    @Override
    public PatientsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_patient_box, parent, false);
        return new PatientsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PatientsViewHolder holder, int position) {

        Patient patient = searchResults.get(holder.getAdapterPosition());

        holder.getNameText().setText(patient.getName());
        holder.getChartNumberText().setText(patient.getChartNumber());
        holder.getBirthText().setText(simpleDateFormat.format(patient.getBirthDate()));

        holder.getLayout().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(getClass().getSimpleName(), patient.getName());
                //선택 UI 처리 후 리스너로 전달, 리사이클러뷰 닫고 환자 정보 보여주면 됨.

                SharedPreferencesManager.setPatientId(context, patient.getId());
                SharedPreferencesManager.setPatientName(context, patient.getName());
                SharedPreferencesManager.setPatientBirth(context, patient.getBirthDate());
                SharedPreferencesManager.setPatientChartNumber(context, patient.getChartNumber());
                SharedPreferencesManager.setPatientHeight(context, patient.getHeight());
                SharedPreferencesManager.setPatientGender(context, patient.isGender());
                SharedPreferencesManager.setPatientWeight(context, patient.getWeight());
                SharedPreferencesManager.setPatientIsSmoking(context, patient.isSmoke());
                SharedPreferencesManager.setPatientStartSmokingDate(context, patient.getStartSmokeDate());
                SharedPreferencesManager.setPatientNoSmokingDate(context, patient.getStopSmokeDate());
                SharedPreferencesManager.setPatientSmokingAmount(context, patient.getSmokeAmountPack());
                SharedPreferencesManager.setPatientHumanRaceId(context, patient.getHumanRaceId());
                SharedPreferencesManager.setPatientMatchDoctor(context, patient.getDoctorName());
                //주치의 입력

                simpleSelectedListener.onSimpleSelected();

            }
        });

        holder.getRemoveButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //삭제 후 업데이트

            }
        });


    }

    @Override
    public int getItemCount() {
        return (searchResults != null ? searchResults.size() : 0);
    }
}

class PatientsViewHolder extends RecyclerView.ViewHolder {

    private TextView nameText, chartNumberText, birthText;
    private ImageButton removeButton;
    private ConstraintLayout layout;


    public PatientsViewHolder(@NonNull View itemView) {
        super(itemView);

        nameText = itemView.findViewById(R.id.tv_name_patient_box);
        birthText = itemView.findViewById(R.id.tv_birth_patient_box);
        chartNumberText = itemView.findViewById(R.id.tv_chart_number_patient_box);

        removeButton = itemView.findViewById(R.id.img_btn_remove_patient_box);
        layout = itemView.findViewById(R.id.constraint_layout_patient_box);
    }

    public TextView getNameText() {
        return nameText;
    }

    public void setNameText(TextView nameText) {
        this.nameText = nameText;
    }

    public TextView getChartNumberText() {
        return chartNumberText;
    }

    public void setChartNumberText(TextView chartNumberText) {
        this.chartNumberText = chartNumberText;
    }

    public TextView getBirthText() {
        return birthText;
    }

    public void setBirthText(TextView birthText) {
        this.birthText = birthText;
    }

    public ImageButton getRemoveButton() {
        return removeButton;
    }

    public void setRemoveButton(ImageButton removeButton) {
        this.removeButton = removeButton;
    }

    public ConstraintLayout getLayout() {
        return layout;
    }

    public void setLayout(ConstraintLayout layout) {
        this.layout = layout;
    }
}
