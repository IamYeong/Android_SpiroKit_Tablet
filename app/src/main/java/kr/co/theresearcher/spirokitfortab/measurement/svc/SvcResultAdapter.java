package kr.co.theresearcher.spirokitfortab.measurement.svc;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import kr.co.theresearcher.spirokitfortab.R;
import kr.co.theresearcher.spirokitfortab.main.result.OnOrderSelectedListener;

public class SvcResultAdapter extends RecyclerView.Adapter<SvcResultViewHolder> {

    private List<ResultSVC> results;
    private Context context;
    private boolean isNothing = true;
    private OnOrderSelectedListener orderSelectedListener;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd (HH:mm)", Locale.getDefault());

    public SvcResultAdapter(Context context) {

        this.context = context;
        results = new ArrayList<>();

    }

    public void setOrderSelectedListener(OnOrderSelectedListener listener) {
        this.orderSelectedListener = listener;
    }

    public void setResults(List<ResultSVC> results) {
        this.results.clear();
        this.results.addAll(results);
    }

    public void addResult(ResultSVC resultSVC) {
        if (isNothing) {
            this.results.clear();
            isNothing = false;
        }

        results.add(resultSVC);
    }

    public void addEmptyResult(ResultSVC resultSVC) {
        this.results.add(resultSVC);
    }


    @NonNull
    @Override
    public SvcResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_result_svc , parent, false);
        return new SvcResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SvcResultViewHolder holder, int position) {

        ResultSVC resultSVC = results.get(holder.getAdapterPosition());

        holder.getVcText().setText(context.getString(R.string.liter_with_parameter, resultSVC.getVc()));
        holder.getDateText().setText(simpleDateFormat.format(resultSVC.getTimestamp()));

        if (resultSVC.isPost()) {
            holder.getTitleText().setText(context.getString(R.string.result_order, holder.getAdapterPosition() + 1, context.getString(R.string.post)));
        } else {
            holder.getTitleText().setText(context.getString(R.string.result_order, holder.getAdapterPosition() + 1, context.getString(R.string.pre)));
        }

        if (resultSVC.isSelected()) {

            holder.getLinearLayout().setBackground(AppCompatResources.getDrawable(context, R.drawable.item_selected_result));

        } else {
            holder.getLinearLayout().setBackgroundResource(0);
        }

        holder.getSvcCard().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (ResultSVC result : results) result.setSelected(false);
                results.get(holder.getAdapterPosition()).setSelected(true);
                orderSelectedListener.onOrderSelected(holder.getAdapterPosition());
                notifyDataSetChanged();
            }
        });


    }

    @Override
    public int getItemCount() {
        return (results != null ? results.size() : 0);
    }
}

class SvcResultViewHolder extends RecyclerView.ViewHolder {

    private TextView titleText, dateText, vcText;
    private CardView svcCard;
    private LinearLayout linearLayout;

    public SvcResultViewHolder(@NonNull View itemView) {
        super(itemView);

        titleText = itemView.findViewById(R.id.tv_svc_order_title);
        dateText = itemView.findViewById(R.id.tv_svc_date_table);
        vcText = itemView.findViewById(R.id.tv_vc_svc_result);

        svcCard = itemView.findViewById(R.id.card_svc_result);
        linearLayout = itemView.findViewById(R.id.linear_svc_result);

    }

    public LinearLayout getLinearLayout() {
        return linearLayout;
    }

    public void setLinearLayout(LinearLayout linearLayout) {
        this.linearLayout = linearLayout;
    }

    public CardView getSvcCard() {
        return svcCard;
    }

    public void setSvcCard(CardView svcCard) {
        this.svcCard = svcCard;
    }

    public TextView getTitleText() {
        return titleText;
    }

    public void setTitleText(TextView titleText) {
        this.titleText = titleText;
    }

    public TextView getDateText() {
        return dateText;
    }

    public void setDateText(TextView dateText) {
        this.dateText = dateText;
    }

    public TextView getVcText() {
        return vcText;
    }

    public void setVcText(TextView vcText) {
        this.vcText = vcText;
    }
}
