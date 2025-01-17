package kr.co.theresearcher.spirokitfortab.measurement.svc;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
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
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import kr.co.theresearcher.spirokitfortab.OnItemChangedListener;
import kr.co.theresearcher.spirokitfortab.OnItemDeletedListener;
import kr.co.theresearcher.spirokitfortab.R;
import kr.co.theresearcher.spirokitfortab.SharedPreferencesManager;
import kr.co.theresearcher.spirokitfortab.db.SpiroKitDatabase;
import kr.co.theresearcher.spirokitfortab.dialog.OnSelectedInDialogListener;
import kr.co.theresearcher.spirokitfortab.dialog.SelectionDialog;
import kr.co.theresearcher.spirokitfortab.main.result.OnOrderSelectedListener;
import kr.co.theresearcher.spirokitfortab.measurement.fvc.ResultFVC;

public class SvcResultAdapter extends RecyclerView.Adapter<SvcResultViewHolder> {

    private List<ResultSVC> results;
    private Context context;
    private boolean isNothing = true;
    private OnOrderSelectedListener orderSelectedListener;
    private OnItemChangedListener onItemChangedListener;
    private OnItemDeletedListener deletedListener;
    private int selectedOrdinal = 0;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd (HH:mm)", Locale.getDefault());

    private long rootTimestamp;
    private Handler handler = new Handler(Looper.getMainLooper());

    public SvcResultAdapter(Context context) {

        this.context = context;
        results = new ArrayList<>();

    }

    public void setDeletedListener(OnItemDeletedListener deletedListener) {
        this.deletedListener = deletedListener;
    }

    public void setOnItemChangedListener(OnItemChangedListener listener) {
        this.onItemChangedListener = listener;
    }

    public void setRootTimestamp(long timestamp) {
        this.rootTimestamp = timestamp;
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
        setSelection(results.size() - 1);

    }

    public void addEmptyResult(ResultSVC resultSVC) {
        isNothing = true;
        this.results.add(resultSVC);
    }

    public void clear() {
        results.clear();
    }

    public void setSelection(int index) {

        if (results.size() == 0) return;

        for (ResultSVC result : results) result.setSelected(false);
        results.get(index).setSelected(true);

    }

    public int getSelectedOrdinal() {
        return selectedOrdinal;
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

        holder.getVcText().setText(context.getString(R.string.with_L, resultSVC.getVc()));
        holder.getDateText().setText(simpleDateFormat.format(resultSVC.getTimestamp()));

        if (resultSVC.isPost()) {
            holder.getTitleText().setText(context.getString(R.string.n_order_meas, resultSVC.getOrder(), context.getString(R.string.post)));
        } else {
            holder.getTitleText().setText(context.getString(R.string.n_order_meas, resultSVC.getOrder(), context.getString(R.string.pre)));
        }

        if (resultSVC.isSelected()) {

            holder.getLinearLayout().setBackground(AppCompatResources.getDrawable(context, R.drawable.item_selected_result));

        } else {
            holder.getLinearLayout().setBackgroundResource(0);
        }

        holder.getSvcCard().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isNothing) {
                    return;
                }

                int selected = holder.getAdapterPosition();
                if (selected == -1) return;

                selectedOrdinal = selected;
                for (ResultSVC result : results) result.setSelected(false);
                results.get(selected).setSelected(true);
                orderSelectedListener.onOrderSelected(selected);
                notifyDataSetChanged();
            }
        });

        holder.getDeleteButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (holder.getAdapterPosition() == -1) return;

                SelectionDialog selectionDialog = new SelectionDialog(context);
                selectionDialog.setTitle(context.getString(R.string.question_delete_history));
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

                                    //Log.d(getClass().getSimpleName(), "DELETE HASH : " + resultSVC.getHashed());
                                    SpiroKitDatabase.getInstance(context)
                                            .calHistoryRawDataDao().delete(resultSVC.getHashed(), simpleDateFormat.format(time));

                                    int deleteIndex = holder.getAdapterPosition();
                                    results.remove(deleteIndex);
                                    setSelection(results.size() - 1);

                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {

                                            notifyDataSetChanged();
                                            deletedListener.onDeleted(deleteIndex);
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
        return (results != null ? results.size() : 0);
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

class SvcResultViewHolder extends RecyclerView.ViewHolder {

    private TextView titleText, dateText, vcText;
    private CardView svcCard;
    private LinearLayout linearLayout;
    private ImageButton deleteButton;

    public SvcResultViewHolder(@NonNull View itemView) {
        super(itemView);

        titleText = itemView.findViewById(R.id.tv_svc_order_title);
        dateText = itemView.findViewById(R.id.tv_svc_date_table);
        vcText = itemView.findViewById(R.id.tv_vc_svc_result);

        svcCard = itemView.findViewById(R.id.card_svc_result);
        linearLayout = itemView.findViewById(R.id.linear_svc_result);
        deleteButton = itemView.findViewById(R.id.img_btn_delete_svc_result);

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
