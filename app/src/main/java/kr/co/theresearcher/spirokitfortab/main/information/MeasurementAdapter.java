package kr.co.theresearcher.spirokitfortab.main.information;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import kr.co.theresearcher.spirokitfortab.OnItemChangedListener;
import kr.co.theresearcher.spirokitfortab.R;
import kr.co.theresearcher.spirokitfortab.SharedPreferencesManager;
import kr.co.theresearcher.spirokitfortab.db.RoomNames;
import kr.co.theresearcher.spirokitfortab.db.meas_group.MeasGroup;
import kr.co.theresearcher.spirokitfortab.dialog.OnSelectedInDialogListener;
import kr.co.theresearcher.spirokitfortab.dialog.SelectionDialog;

public class MeasurementAdapter extends RecyclerView.Adapter<MeasurementViewHolder> {

    private Context context;
    private List<Measurement> measurements;
    private List<Measurement> searchResults;
    private OnMeasSelectedListener selectedListener;
    private OnItemChangedListener onItemChangedListener;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault());

    private Handler handler = new Handler(Looper.getMainLooper());

    public MeasurementAdapter(Context context) {
        this.context = context;
        measurements = new ArrayList<>();
        searchResults = new ArrayList<>();
    }

    public void setOnItemChangedListener(OnItemChangedListener listener) {
        this.onItemChangedListener = listener;
    }

    public boolean searchMeasInRange(long from, long to) {

        if (from > to) return false;
        if (from < 0) return false;

        searchResults.clear();

        for (Measurement measurement : measurements) {

            if ((measurement.getMeasDate() >= from) && (measurement.getMeasDate() <= to)) {
                searchResults.add(measurement);
            }

        }

        return true;

    }

    public void initializing() {

        searchResults.clear();
        searchResults.addAll(measurements);

    }

    public void setSelectedListener(OnMeasSelectedListener listener) {
        this.selectedListener = listener;
    }

    public void setMeasurements(List<Measurement> measurementList) {

        this.measurements.clear();
        this.searchResults.clear();
        this.measurements.addAll(measurementList);
        this.searchResults.addAll(measurementList);

        if (searchResults.size() > 0) {
            searchResults.get(searchResults.size() - 1).setSelected(true);
        }

    }

    public void addMeasurement(Measurement measurement) {
        this.measurements.add(measurement);
        this.searchResults.add(measurement);
    }

    @NonNull
    @Override
    public MeasurementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_measurement_box, parent, false);
        return new MeasurementViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MeasurementViewHolder holder, int position) {

        Measurement measurement = searchResults.get(holder.getAdapterPosition());

        if (measurement.isSelected()) {

            holder.getConstraintLayout().setBackground(AppCompatResources.getDrawable(context, R.drawable.item_selected_meas));

        } else {

            holder.getConstraintLayout().setBackgroundResource(0);

        }

        holder.getMeasTitle().setText(simpleDateFormat.format(measurement.getMeasDate()));
        MeasGroup[] measGroups = MeasGroup.values();
        holder.getGroupText().setText(measGroups[measurement.getMeasurementID()].toString().toUpperCase(Locale.ROOT));

        holder.getConstraintLayout().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (Measurement meas : searchResults) meas.setSelected(false);
                searchResults.get(holder.getAdapterPosition()).setSelected(true);
                selectedListener.onMeasSelected(measurement);
                notifyDataSetChanged();
            }
        });

        holder.getRemoveButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SelectionDialog selectionDialog = new SelectionDialog(context);
                selectionDialog.setTitle(context.getString(R.string.question_delete_history));
                selectionDialog.setSelectedListener(new OnSelectedInDialogListener() {
                    @Override
                    public void onSelected(boolean select) {

                        Thread thread = new Thread() {

                            @Override
                            public void run() {
                                super.run();
                                Looper.prepare();

                                removeThisData(measurement.getMeasDate());

                                MeasurementDatabase measurementDatabase = Room.databaseBuilder(context, MeasurementDatabase.class, RoomNames.ROOM_MEASUREMENT_DB_NAME)
                                        .build();
                                MeasurementDao measurementDao = measurementDatabase.measurementDao();
                                measurementDao.deleteMeasurement(measurement);
                                measurementDatabase.close();

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
                });

                selectionDialog.show();


            }
        });

    }

    @Override
    public int getItemCount() {
        return (searchResults != null ? searchResults.size() : 0);
    }

    private void removeThisData(long timestamp) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmssSS", Locale.getDefault());

        String dirName = dateFormat.format(timestamp);
        File dir = context.getExternalFilesDir("data/"
                + SharedPreferencesManager.getOfficeID(context) + "/"
                + SharedPreferencesManager.getPatientId(context) + "/"
                + dirName);

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

class MeasurementViewHolder extends RecyclerView.ViewHolder {

    private TextView measTitle, groupText;
    private ImageButton removeButton;
    private ConstraintLayout constraintLayout;

    public MeasurementViewHolder(@NonNull View itemView) {
        super(itemView);

        measTitle = itemView.findViewById(R.id.tv_measurement_box);
        removeButton = itemView.findViewById(R.id.img_btn_remove_meas_box);
        constraintLayout = itemView.findViewById(R.id.constraint_layout_meas_box);
        groupText = itemView.findViewById(R.id.tv_group_measurement_box);

    }

    public TextView getGroupText() {
        return groupText;
    }

    public void setGroupText(TextView groupText) {
        this.groupText = groupText;
    }

    public ConstraintLayout getConstraintLayout() {
        return constraintLayout;
    }

    public void setConstraintLayout(ConstraintLayout constraintLayout) {
        this.constraintLayout = constraintLayout;
    }

    public TextView getMeasTitle() {
        return measTitle;
    }

    public void setMeasTitle(TextView measTitle) {
        this.measTitle = measTitle;
    }

    public ImageButton getRemoveButton() {
        return removeButton;
    }

    public void setRemoveButton(ImageButton removeButton) {
        this.removeButton = removeButton;
    }
}
