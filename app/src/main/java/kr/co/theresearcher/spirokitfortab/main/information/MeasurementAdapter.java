package kr.co.theresearcher.spirokitfortab.main.information;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MeasurementAdapter extends RecyclerView.Adapter<MeasurementViewHolder> {

    private List<String> measurements;


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
        return 0;
    }
}

class MeasurementViewHolder extends RecyclerView.ViewHolder {

    public MeasurementViewHolder(@NonNull View itemView) {
        super(itemView);
    }
}
