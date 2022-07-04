package kr.co.theresearcher.spirokitfortab.measurement.fvc;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import kr.co.theresearcher.spirokitfortab.OnItemChangedListener;
import kr.co.theresearcher.spirokitfortab.R;
import kr.co.theresearcher.spirokitfortab.SharedPreferencesManager;
import kr.co.theresearcher.spirokitfortab.dialog.OnSelectedInDialogListener;
import kr.co.theresearcher.spirokitfortab.dialog.SelectionDialog;
import kr.co.theresearcher.spirokitfortab.main.result.OnOrderSelectedListener;


public class FvcResultAdapter extends RecyclerView.Adapter<FvcResultViewHolder> {

    private Context context;
    private List<ResultFVC> fvcResults;
    private OnOrderSelectedListener orderSelectedListener;
    private OnItemChangedListener changedListener;
    private long rootTimestamp;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

    private boolean nothing = true;

    public FvcResultAdapter(Context context) {
        this.fvcResults = new ArrayList<>();
        this.context = context;
    }

    public void setRootTimestamp(long timestamp) {
        this.rootTimestamp = timestamp;
    }

    public void setChangedListener(OnItemChangedListener listener) {
        changedListener = listener;
    }

    public void setOnOrderSelectedListener(OnOrderSelectedListener listener) {
        this.orderSelectedListener = listener;
    }

    public void setFvcResults(List<ResultFVC> results) {
        if (nothing) this.fvcResults.clear();
        nothing = false;
        this.fvcResults.addAll(results);
    }


    public void addFvcResult(ResultFVC resultFVC) {
        if (nothing) this.fvcResults.clear();
        nothing = false;
        this.fvcResults.add(resultFVC);
    }

    public void addEmptyObject(ResultFVC resultFVC) {
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
            //holder.getFvcCardView().setCardBackgroundColor(ColorStateList.valueOf(context.getColor(R.color.primary_color)));
            //holder.setAllTextsColor(context.getColor(R.color.white));

            holder.getLinearLayout().setBackground(AppCompatResources.getDrawable(context, R.drawable.item_selected_result));

        } else {
            holder.getLinearLayout().setBackgroundResource(0);

        }

        if (resultFVC.isPost()) {
            holder.getOrderText().setText(context.getString(R.string.n_order_meas, holder.getAdapterPosition() + 1, context.getString(R.string.post)));
        } else {
            holder.getOrderText().setText(context.getString(R.string.n_order_meas, holder.getAdapterPosition() + 1, context.getString(R.string.pre)));
        }

        holder.getDateText().setText(context.getString(R.string.time_colon_what, simpleDateFormat.format(resultFVC.getTimestamp())));

        holder.getFvcText().setText(context.getString(R.string.with_L, resultFVC.getFvc()));
        holder.getFvcPredText().setText(context.getString(R.string.with_L, resultFVC.getFvcPredict()));
        holder.getFvcCompareText().setText(context.getString(R.string.with_per, (resultFVC.getFvc() / resultFVC.getFvcPredict()) * 100f, "%"));

        holder.getFev1Text().setText(context.getString(R.string.with_L, resultFVC.getFev1()));
        holder.getFev1PredText().setText(context.getString(R.string.with_L, resultFVC.getFev1Predict()));
        holder.getFev1CompareText().setText(context.getString(R.string.with_per, (resultFVC.getFev1() / resultFVC.getFev1Predict()) * 100f, "%"));

        holder.getFev1PercentText().setText(context.getString(R.string.with_per, resultFVC.getFev1percent(), "%"));
        holder.getFev1PercentPredText().setText(context.getString(R.string.with_per, resultFVC.getFev1PercentPredict(), "%"));
        holder.getFev1PercentCompareText().setText(context.getString(R.string.with_per, (resultFVC.getFev1percent() / resultFVC.getFev1PercentPredict()) * 100f , "%"));

        holder.getPefText().setText(context.getString(R.string.with_lps, resultFVC.getPef()));
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

        holder.getDeleteButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //File path = context.getExternalFilesDir("data/")

                SelectionDialog selectionDialog = new SelectionDialog(context);
                selectionDialog.setTitle(context.getString(R.string.question_delete_history));
                selectionDialog.setSelectedListener(new OnSelectedInDialogListener() {
                    @Override
                    public void onSelected(boolean select) {
                        if (select) {
                            removeThisData(rootTimestamp, holder.getAdapterPosition() + 1);
                            changedListener.onChanged();
                        }
                    }
                });

                selectionDialog.show();

            }
        });

    }

    @Override
    public int getItemCount() {
        return (fvcResults != null ? fvcResults.size() : 0);
    }

    private void removeThisData(long timestamp, int order) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmssSS", Locale.getDefault());

        String dirName = dateFormat.format(timestamp);
        File dir = context.getExternalFilesDir("data/"
                + SharedPreferencesManager.getOfficeID(context) + "/"
                + SharedPreferencesManager.getPatientId(context) + "/"
                + dirName + "/" + order);

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

class FvcResultViewHolder extends RecyclerView.ViewHolder {

    private List<TextView> defaultTexts;
    private List<TextView> texts;

    private CardView fvcCardView;
    private LinearLayout linearLayout;

    private ImageButton deleteButton;

    public FvcResultViewHolder(@NonNull View itemView) {
        super(itemView);

        texts = new ArrayList<>();
        defaultTexts = new ArrayList<>();

        defaultTexts.add(itemView.findViewById(R.id.tv_fvc_table_row));
        defaultTexts.add(itemView.findViewById(R.id.tv_fev1_table_row));
        defaultTexts.add(itemView.findViewById(R.id.tv_fev1_per_table_row));
        defaultTexts.add(itemView.findViewById(R.id.tv_pef_table_row));

        defaultTexts.add(itemView.findViewById(R.id.tv_fvc_meas_table_column));
        defaultTexts.add(itemView.findViewById(R.id.tv_fvc_pred_table_column));
        defaultTexts.add(itemView.findViewById(R.id.tv_fvc_percent_table_column));

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

        linearLayout = itemView.findViewById(R.id.linear_fvc_result);
        deleteButton = itemView.findViewById(R.id.img_btn_delete_fvc_result);

    }

    public void setAllTextsColor(int color) {

        for (int i = 0; i < texts.size(); i++) {
            texts.get(i).setTextColor(color);

        }

        for (int i = 0; i < defaultTexts.size(); i++) {
            defaultTexts.get(i).setTextColor(color);
        }

    }

    public ImageButton getDeleteButton() {
        return deleteButton;
    }

    public void setDeleteButton(ImageButton deleteButton) {
        this.deleteButton = deleteButton;
    }

    public LinearLayout getLinearLayout() {
        return linearLayout;
    }

    public void setLinearLayout(LinearLayout linearLayout) {
        this.linearLayout = linearLayout;
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
