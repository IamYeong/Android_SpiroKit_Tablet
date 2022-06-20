package kr.co.theresearcher.spirokitfortab.measurement.fvc;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import kr.co.theresearcher.spirokitfortab.R;
import kr.co.theresearcher.spirokitfortab.main.result.OnOrderSelectedListener;


public class FvcResultAdapter extends RecyclerView.Adapter<FvcResultViewHolder> {

    private Context context;
    private List<ResultFVC> fvcResults;
    private OnOrderSelectedListener orderSelectedListener;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd (HH:mm)", Locale.getDefault());


    public FvcResultAdapter(Context context) {
        this.fvcResults = new ArrayList<>();
        this.context = context;
    }

    public void setOnOrderSelectedListener(OnOrderSelectedListener listener) {
        this.orderSelectedListener = listener;
    }

    public void setFvcResults(List<ResultFVC> results) {
        this.fvcResults.addAll(results);
    }

    public void addFvcResult(ResultFVC resultFVC) {
        this.fvcResults.add(resultFVC);
    }

    public void clear() {
        fvcResults.clear();
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

        if (resultFVC.isSelected()) {
            holder.getFvcCardView().setCardBackgroundColor(ColorStateList.valueOf(context.getColor(R.color.primary_color)));
        } else {
            holder.getFvcCardView().setCardBackgroundColor(ColorStateList.valueOf(context.getColor(R.color.white)));
        }

        if (resultFVC.isPost()) {
            holder.getOrderText().setText(context.getString(R.string.result_order, holder.getAdapterPosition() + 1, context.getString(R.string.post)));
        } else {
            holder.getOrderText().setText(context.getString(R.string.result_order, holder.getAdapterPosition() + 1, context.getString(R.string.pre)));
        }

        holder.getDateText().setText(context.getString(R.string.date_is, simpleDateFormat.format(resultFVC.getTimestamp())));

        holder.getFvcText().setText(context.getString(R.string.liter_with_parameter, resultFVC.getFvc()));
        holder.getFvcPredText().setText(context.getString(R.string.liter_with_parameter, resultFVC.getFvcPredict()));
        holder.getFvcCompareText().setText(context.getString(R.string.percent_with_parameter, (resultFVC.getFvc() / resultFVC.getFvcPredict()) * 100f, "%"));

        holder.getFev1Text().setText(context.getString(R.string.liter_with_parameter, resultFVC.getFev1()));
        holder.getFev1PredText().setText(context.getString(R.string.liter_with_parameter, resultFVC.getFev1Predict()));
        holder.getFev1CompareText().setText(context.getString(R.string.percent_with_parameter, (resultFVC.getFev1() / resultFVC.getFev1Predict()) * 100f, "%"));

        holder.getFev1PercentText().setText(context.getString(R.string.percent_with_parameter, resultFVC.getFev1percent(), "%"));
        holder.getFev1PercentPredText().setText(context.getString(R.string.percent_with_parameter, resultFVC.getFev1PercentPredict(), "%"));
        holder.getFev1PercentCompareText().setText(context.getString(R.string.percent_with_parameter, (resultFVC.getFev1percent() / resultFVC.getFev1PercentPredict()) * 100f , "%"));

        holder.getPefText().setText(context.getString(R.string.lps_with_parameter, resultFVC.getPef()));
        holder.getPefPredtext().setText("-");
        holder.getPefCompareText().setText("-");

        holder.getFvcCardView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (ResultFVC result : fvcResults) result.setSelected(false);
                fvcResults.get(holder.getAdapterPosition()).setSelected(true);
                orderSelectedListener.onOrderSelected(holder.getAdapterPosition());
                notifyDataSetChanged();

            }
        });

    }

    @Override
    public int getItemCount() {
        return (fvcResults != null ? fvcResults.size() : 0);
    }
}

class FvcResultViewHolder extends RecyclerView.ViewHolder {

    private TextView fvcText, fev1Text, fev1PercentText, pefText
            , fvcPredText, fev1PredText, fev1PercentPredText, pefPredtext
            , fvcCompareText, fev1CompareText, fev1PercentCompareText, pefCompareText
            , orderText, dateText;

    private CardView fvcCardView;

    public FvcResultViewHolder(@NonNull View itemView) {
        super(itemView);

        orderText = itemView.findViewById(R.id.tv_fvc_order_table);
        dateText = itemView.findViewById(R.id.tv_fvc_date_table);
        fvcCardView = itemView.findViewById(R.id.card_fvc_result);

        fvcText = itemView.findViewById(R.id.tv_fvc_meas_result_table);
        fvcPredText = itemView.findViewById(R.id.tv_fvc_pred_result_table);
        fvcCompareText = itemView.findViewById(R.id.tv_fvc_compare_result_table);

        fev1Text = itemView.findViewById(R.id.tv_fev1_meas_result_table);
        fev1PredText = itemView.findViewById(R.id.tv_fev1_pred_result_table);
        fev1CompareText = itemView.findViewById(R.id.tv_fev1_compare_result_table);

        fev1PercentText = itemView.findViewById(R.id.tv_fev1_per_meas_result_table);
        fev1PercentPredText = itemView.findViewById(R.id.tv_fev1_per_pred_result_table);
        fev1PercentCompareText = itemView.findViewById(R.id.tv_fev1_per_compare_result_table);

        pefText = itemView.findViewById(R.id.tv_pef_meas_result_table);
        pefPredtext = itemView.findViewById(R.id.tv_pef_pred_result_table);
        pefCompareText = itemView.findViewById(R.id.tv_pef_compare_result_table);

    }

    public CardView getFvcCardView() {
        return fvcCardView;
    }

    public void setFvcCardView(CardView fvcCardView) {
        this.fvcCardView = fvcCardView;
    }

    public TextView getFvcText() {
        return fvcText;
    }

    public void setFvcText(TextView fvcText) {
        this.fvcText = fvcText;
    }

    public TextView getFev1Text() {
        return fev1Text;
    }

    public void setFev1Text(TextView fev1Text) {
        this.fev1Text = fev1Text;
    }

    public TextView getFev1PercentText() {
        return fev1PercentText;
    }

    public void setFev1PercentText(TextView fev1PercentText) {
        this.fev1PercentText = fev1PercentText;
    }

    public TextView getPefText() {
        return pefText;
    }

    public void setPefText(TextView pefText) {
        this.pefText = pefText;
    }

    public TextView getFvcPredText() {
        return fvcPredText;
    }

    public void setFvcPredText(TextView fvcPredText) {
        this.fvcPredText = fvcPredText;
    }

    public TextView getFev1PredText() {
        return fev1PredText;
    }

    public void setFev1PredText(TextView fev1PredText) {
        this.fev1PredText = fev1PredText;
    }

    public TextView getFev1PercentPredText() {
        return fev1PercentPredText;
    }

    public void setFev1PercentPredText(TextView fev1PercentPredText) {
        this.fev1PercentPredText = fev1PercentPredText;
    }

    public TextView getPefPredtext() {
        return pefPredtext;
    }

    public void setPefPredtext(TextView pefPredtext) {
        this.pefPredtext = pefPredtext;
    }

    public TextView getFvcCompareText() {
        return fvcCompareText;
    }

    public void setFvcCompareText(TextView fvcCompareText) {
        this.fvcCompareText = fvcCompareText;
    }

    public TextView getFev1CompareText() {
        return fev1CompareText;
    }

    public void setFev1CompareText(TextView fev1CompareText) {
        this.fev1CompareText = fev1CompareText;
    }

    public TextView getFev1PercentCompareText() {
        return fev1PercentCompareText;
    }

    public void setFev1PercentCompareText(TextView fev1PercentCompareText) {
        this.fev1PercentCompareText = fev1PercentCompareText;
    }

    public TextView getPefCompareText() {
        return pefCompareText;
    }

    public void setPefCompareText(TextView pefCompareText) {
        this.pefCompareText = pefCompareText;
    }

    public TextView getOrderText() {
        return orderText;
    }

    public void setOrderText(TextView orderText) {
        this.orderText = orderText;
    }

    public TextView getDateText() {
        return dateText;
    }

    public void setDateText(TextView dateText) {
        this.dateText = dateText;
    }
}
