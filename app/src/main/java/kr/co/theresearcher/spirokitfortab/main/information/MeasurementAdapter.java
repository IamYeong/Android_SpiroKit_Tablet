package kr.co.theresearcher.spirokitfortab.main.information;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import kr.co.theresearcher.spirokitfortab.R;
import kr.co.theresearcher.spirokitfortab.db.meas_group.MeasGroup;
import kr.co.theresearcher.spirokitfortab.db.measurement.Measurement;

public class MeasurementAdapter extends RecyclerView.Adapter<MeasurementViewHolder> {

    private Context context;
    private List<Measurement> measurements;
    private List<Measurement> searchResults;
    private OnMeasSelectedListener selectedListener;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault());

    public MeasurementAdapter(Context context) {
        this.context = context;
        measurements = new ArrayList<>();
        searchResults = new ArrayList<>();
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

    }

    @Override
    public int getItemCount() {
        return (searchResults != null ? searchResults.size() : 0);
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
