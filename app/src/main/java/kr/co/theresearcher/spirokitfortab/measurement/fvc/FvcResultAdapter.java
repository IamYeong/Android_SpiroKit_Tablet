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
            holder.setAllTextsColor(context.getColor(R.color.white));

        } else {
            holder.getFvcCardView().setCardBackgroundColor(ColorStateList.valueOf(context.getColor(R.color.white)));
            holder.setAllTextsColor(context.getColor(R.color.gray_dark));
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

    private List<TextView> defaultTexts;
    private List<TextView> texts;

    private CardView fvcCardView;

    public FvcResultViewHolder(@NonNull View itemView) {
        super(itemView);

        texts = new ArrayList<>();
        defaultTexts = new ArrayList<>();

        defaultTexts.add(itemView.findViewById(R.id.tv_fvc_table_row));
        defaultTexts.add(itemView.findViewById(R.id.tv_fev1_table_row));
        defaultTexts.add(itemView.findViewById(R.id.tv_fev1_per_table_row));
        defaultTexts.add(itemView.findViewById(R.id.tv_pef_table_row));

        defaultTexts.add(itemView.findViewById(R.id.tv_meas_table_column));
        defaultTexts.add(itemView.findViewById(R.id.tv_pred_table_column));
        defaultTexts.add(itemView.findViewById(R.id.tv_percent_table_column));

        fvcCardView = itemView.findViewById(R.id.card_fvc_result);

        texts.add(itemView.findViewById(R.id.tv_fvc_order_table));
        texts.add(itemView.findViewById(R.id.tv_fvc_date_table));

        texts.add(itemView.findViewById(R.id.tv_fvc_meas_result_table));
        texts.add(itemView.findViewById(R.id.tv_fvc_pred_result_table));
        texts.add(itemView.findViewById(R.id.tv_fvc_compare_result_table));

        texts.add(itemView.findViewById(R.id.tv_fev1_meas_result_table));
        texts.add(itemView.findViewById(R.id.tv_fev1_pred_result_table));
        texts.add(itemView.findViewById(R.id.tv_fev1_compare_result_table));

        texts.add(itemView.findViewById(R.id.tv_fev1_per_meas_result_table));
        texts.add(itemView.findViewById(R.id.tv_fev1_per_pred_result_table));
        texts.add(itemView.findViewById(R.id.tv_fev1_per_compare_result_table));

        texts.add(itemView.findViewById(R.id.tv_pef_meas_result_table));
        texts.add(itemView.findViewById(R.id.tv_pef_pred_result_table));
        texts.add(itemView.findViewById(R.id.tv_pef_compare_result_table));

    }

    public void setAllTextsColor(int color) {

        for (int i = 0; i < texts.size(); i++) {
            texts.get(i).setTextColor(color);

        }

        for (int i = 0; i < defaultTexts.size(); i++) {
            defaultTexts.get(i).setTextColor(color);
        }

    }

    public CardView getFvcCardView() {
        return fvcCardView;
    }

    public void setFvcCardView(CardView fvcCardView) {
        this.fvcCardView = fvcCardView;
    }

    public TextView getFvcText() {
        return texts.get(2);
    }


    public TextView getFev1Text() {
        return texts.get(5);
    }


    public TextView getFev1PercentText() {
        return texts.get(8);
    }


    public TextView getPefText() {
        return texts.get(11);
    }


    public TextView getFvcPredText() {
        return texts.get(3);
    }


    public TextView getFev1PredText() {
        return texts.get(6);
    }


    public TextView getFev1PercentPredText() {
        return texts.get(9);
    }

    public TextView getPefPredtext() {
        return texts.get(12);
    }

    public TextView getFvcCompareText() {
        return texts.get(4);
    }

    public TextView getFev1CompareText() {
        return texts.get(7);
    }

    public TextView getFev1PercentCompareText() {
        return texts.get(10);
    }

    public TextView getPefCompareText() {
        return texts.get(13);
    }


    public TextView getOrderText() {
        return texts.get(0);
    }

    public TextView getDateText() {
        return texts.get(1);
    }

}