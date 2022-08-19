package kr.co.theresearcher.spirokitfortab.main.patients;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import kr.co.theresearcher.spirokitfortab.OnItemChangedListener;
import kr.co.theresearcher.spirokitfortab.R;
import kr.co.theresearcher.spirokitfortab.SharedPreferencesManager;

import kr.co.theresearcher.spirokitfortab.db.SpiroKitDatabase;
import kr.co.theresearcher.spirokitfortab.db.patient.Patient;
import kr.co.theresearcher.spirokitfortab.dialog.OnSelectedInDialogListener;
import kr.co.theresearcher.spirokitfortab.dialog.SelectionDialog;

public class PatientsAdapter extends RecyclerView.Adapter<PatientsViewHolder> {

    private List<Patient> patients;
    private List<Patient> searchResults;
    private Context context;
    private OnItemSimpleSelectedListener simpleSelectedListener;
    private OnItemChangedListener onItemChangedListener;

    private Handler handler = new Handler(Looper.getMainLooper());
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd", Locale.getDefault());

    public PatientsAdapter(Context context) {
        this.context = context;
        patients = new ArrayList<>();
        searchResults = new ArrayList<>();
    }

    public void setOnItemChangedListener(OnItemChangedListener listener) {
        this.onItemChangedListener = listener;
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

        if (holder.getAdapterPosition() == -1) return;

        Patient patient = searchResults.get(holder.getAdapterPosition());

        /*
        Log.d(getClass().getSimpleName(), "==============================\n" +
                patient.getChartNumber() + "\n"
                + patient.getHashed() + "\n"
                + patient.getGender() + "\n"
                + patient.getStartSmokingDay() + "\n"
                + patient.getStopSmokingDay() + "\n"
                + patient.getBirthDay() + "\n"
                + patient.getId() + "\n"
                + patient.getNowSmoking() + "\n"
                + patient.getWeight() + "\n"
                + patient.getHeight() + "\n"
                + patient.getName() + "\n"
                + patient.getHumanRace() + "\n"
                + patient.getOs() + "\n"
                + patient.getSmokingPeriod() + "\n"
                + patient.getOfficeHashed() + "\n"
                + patient.getIsDeleted() +
                "\n======================");

         */

        holder.getNameText().setText(patient.getName());
        holder.getChartNumberText().setText(context.getString(R.string._chart_number_is, patient.getChartNumber()));
        holder.getBirthText().setText(patient.getBirthDay().substring(0, patient.getBirthDay().length() - 9));

        holder.getLayout().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.e(getClass().getSimpleName(), patient.getName());
                SharedPreferencesManager.setPatientHash(context, patient.getHashed());
                simpleSelectedListener.onSimpleSelected();

            }
        });

        holder.getRemoveButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SelectionDialog selectionDialog = new SelectionDialog(context);
                selectionDialog.setTitle(context.getString(R.string.question_delete_patient));
                selectionDialog.setSelectedListener(new OnSelectedInDialogListener() {
                    @Override
                    public void onSelected(boolean select) {

                        if (select) {
                            Thread thread = new Thread() {
                                @Override
                                public void run() {
                                    super.run();
                                    Looper.prepare();

                                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                                    long time = Calendar.getInstance().getTime().getTime();

                                    SpiroKitDatabase database = SpiroKitDatabase.getInstance(context);
                                    database.calHistoryDao().deleteReference(patient.getHashed());
                                    database.patientDao().deletePatient(patient.getHashed(), simpleDateFormat.format(time));

                                    SpiroKitDatabase.removeInstance();

                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {

                                            onItemChangedListener.onChanged();

                                        }
                                    });

                                    Looper.loop();
                                }
                            };
                            thread.start();
                        }
                    }
                });
                selectionDialog.show();



            }
        });


    }

    @Override
    public int getItemCount() {
        return (searchResults != null ? searchResults.size() : 0);
    }

    private void removeThisData(int pid) {

        File dir = context.getExternalFilesDir("data/"
                + SharedPreferencesManager.getOfficeID(context) + "/"
                + pid);

        deleteFileWithChildren(dir);

    }

    private void deleteFileWithChildren(File fileOrDirectory) {

        if (fileOrDirectory.isDirectory()) {
            File[] files = fileOrDirectory.listFiles();
            for (File file : files) {
                deleteFileWithChildren(file);
            }
        }

        fileOrDirectory.delete();

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
