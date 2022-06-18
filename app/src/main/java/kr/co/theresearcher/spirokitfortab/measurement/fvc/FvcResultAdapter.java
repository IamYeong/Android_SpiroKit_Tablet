package kr.co.theresearcher.spirokitfortab.measurement.fvc;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import kr.co.theresearcher.spirokitfortab.R;


public class FvcResultAdapter extends RecyclerView.Adapter<FvcResultViewHolder> {

    private Context context;
    private List<ResultFVC> fvcResults;

    public FvcResultAdapter(Context context) {
        this.fvcResults = new ArrayList<>();
        this.context = context;
    }

    public void setFvcResults(List<ResultFVC> results) {
        this.fvcResults.addAll(results);
    }

    public void addFvcResult(ResultFVC resultFVC) {
        this.fvcResults.add(resultFVC);
    }

    @NonNull
    @Override
    public FvcResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_result, parent, false);
        return new FvcResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FvcResultViewHolder holder, int position) {

        ResultFVC resultFVC = fvcResults.get(holder.getAdapterPosition());
        //holder.getFvcText().setText(context.getString(R.string.fvc_with_unit, resultFVC.getFvc()));
        //...
    }

    @Override
    public int getItemCount() {
        return (fvcResults != null ? fvcResults.size() : 0);
    }
}

class FvcResultViewHolder extends RecyclerView.ViewHolder {

    private TextView fvcText, fev1Text, fev1PercentText, pefText
            , fvcPredText, fev1PredText, fev1PercentPredText, pefPredtext
            , fvcCompareText, fev1CompareText, fev1PercentCompareText, pefCompareText;

    private CardView fvcCardView;

    public FvcResultViewHolder(@NonNull View itemView) {
        super(itemView);

        //fvcText = itemView.findViewById(R.id.tv_fvc_result_table);

    }

    public TextView getFvcText() {
        return fvcText;
    }

    public void setFvcText(TextView fvcText) {
        this.fvcText = fvcText;
    }
}
