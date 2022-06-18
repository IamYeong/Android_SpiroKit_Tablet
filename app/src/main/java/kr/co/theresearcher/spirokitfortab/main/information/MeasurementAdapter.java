package kr.co.theresearcher.spirokitfortab.main.information;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import kr.co.theresearcher.spirokitfortab.db.measurement.Measurement;

public class MeasurementAdapter extends RecyclerView.Adapter<MeasurementViewHolder> {

    private Context context;
    private List<Measurement> measurements;
    private List<Measurement> searchResults;

    public MeasurementAdapter(Context context) {
        this.context = context;
        measurements = new ArrayList<>();
        searchResults = new ArrayList<>();
    }

    public void setMeasurements(List<Measurement> measurementList) {
        this.measurements.addAll(measurementList);
        this.searchResults.addAll(measurementList);


    }


    @NonNull
    @Override
    public MeasurementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull MeasurementViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return (searchResults != null ? searchResults.size() : 0);
    }
}

class MeasurementViewHolder extends RecyclerView.ViewHolder {



    public MeasurementViewHolder(@NonNull View itemView) {
        super(itemView);
    }
}
