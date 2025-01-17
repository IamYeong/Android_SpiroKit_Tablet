package kr.co.theresearcher.spirokitfortab.main.information;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import kr.co.theresearcher.spirokitfortab.OnItemChangedListener;
import kr.co.theresearcher.spirokitfortab.R;
import kr.co.theresearcher.spirokitfortab.SharedPreferencesManager;

import kr.co.theresearcher.spirokitfortab.db.SpiroKitDatabase;
import kr.co.theresearcher.spirokitfortab.db.cal_history.CalHistory;
import kr.co.theresearcher.spirokitfortab.dialog.OnSelectedInDialogListener;
import kr.co.theresearcher.spirokitfortab.dialog.SelectionDialog;

public class MeasurementAdapter extends RecyclerView.Adapter<MeasurementViewHolder> {

    private Context context;
    private List<CalHistory> histories;
    private List<CalHistory> searchResults;
    private OnCalHistorySelectedListener selectedListener;
    private OnItemChangedListener onItemChangedListener;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

    private Handler handler = new Handler(Looper.getMainLooper());

    public MeasurementAdapter(Context context) {
        this.context = context;
        histories = new ArrayList<>();
        searchResults = new ArrayList<>();
    }

    public void setOnItemChangedListener(OnItemChangedListener listener) {
        this.onItemChangedListener = listener;
    }

    public boolean searchMeasInRange(long from, long to) {

        if (from > to) return false;
        if (from < 0) return false;


        searchResults.clear();

        for (CalHistory history : histories) {

            if ((history.getTimestamp() >= from) && (history.getTimestamp() <= to)) {
                searchResults.add(history);
            }

        }

        sortByDate();

        return true;

    }

    private void sortByDate() {

        //Bubble
        for (int i = searchResults.size() - 1; i >= 0; i--) {

            for (int j = 1; j <= i; j++) {

                long date = searchResults.get(j - 1).getTimestamp();
                long compareDate = searchResults.get(j).getTimestamp();

                if (compareDate > date) {

                    //swap
                    CalHistory temp = searchResults.get(j);
                    searchResults.set(j, searchResults.get(j - 1));
                    searchResults.set(j - 1, temp);

                }

            }


        }

    }


    public void initializing() {

        searchResults.clear();
        searchResults.addAll(histories);
        sortByDate();

    }

    public void setSelectedListener(OnCalHistorySelectedListener listener) {
        this.selectedListener = listener;
    }

    public void setCalHistories(List<CalHistory> CalHistoryList) {

        this.histories.clear();
        this.searchResults.clear();
        this.histories.addAll(CalHistoryList);
        this.searchResults.addAll(CalHistoryList);

        sortByDate();

        if (searchResults.size() > 0) {
            searchResults.get(0).setSelected(true);

            SharedPreferencesManager.setHistoryHash(context, searchResults.get(0).getHashed());
            selectedListener.onHistorySelected(searchResults.get(0));


        } else {
            selectedListener.onHistorySelected(null);
        }

    }

    public void addCalHistory(CalHistory CalHistory) {
        this.histories.add(CalHistory);
        this.searchResults.add(CalHistory);
    }

    @NonNull
    @Override
    public MeasurementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_measurement_box, parent, false);
        return new MeasurementViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MeasurementViewHolder holder, int position) {

        if (holder.getAdapterPosition() == -1) return;
        CalHistory calHistory = searchResults.get(holder.getAdapterPosition());

        if (calHistory.isSelected()) {

            holder.getConstraintLayout().setBackground(AppCompatResources.getDrawable(context, R.drawable.item_selected_meas));
            holder.getMeasTitle().setTextColor(context.getColor(R.color.white));
            holder.getGroupText().setTextColor(context.getColor(R.color.white));
            holder.getRemoveButton().setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_baseline_close_30_white));


        } else {

            holder.getConstraintLayout().setBackgroundResource(0);
            holder.getMeasTitle().setTextColor(context.getColor(R.color.black));
            holder.getGroupText().setTextColor(context.getColor(R.color.secondary_color));
            holder.getRemoveButton().setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_baseline_close_30_black));
        }

        String dateString = calHistory.getFinishDate();
        dateString = dateString.substring(0, dateString.length() - 10);

        try {

            long time = simpleDateFormat.parse(dateString).getTime();
            holder.getMeasTitle().setText(simpleDateFormat.format(time));

        } catch (ParseException e) {

            holder.getMeasTitle().setText(simpleDateFormat.format(0));

        }

        if (calHistory.getMeasDiv().equals("f")) holder.getGroupText().setText(context.getString(R.string.fvc));
        else if (calHistory.getMeasDiv().equals("s")) holder.getGroupText().setText(context.getString(R.string.svc));
        //else if (calHistory.getMeasDiv().equals("m")) holder.getGroupText().setText(context.getString(R.string.mvv));
        else holder.getGroupText().setText(context.getString(R.string.not_applicable));


        holder.getConstraintLayout().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (holder.getAdapterPosition() == -1) return;

                for (CalHistory history : searchResults) history.setSelected(false);
                searchResults.get(holder.getAdapterPosition()).setSelected(true);

                SharedPreferencesManager.setHistoryHash(context, calHistory.getHashed());

                Log.d(getClass().getSimpleName(), "HISTORY HASH : " + calHistory.getHashed());

                selectedListener.onHistorySelected(calHistory);
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

                        if (select) {
                            Thread thread = new Thread() {

                                @Override
                                public void run() {
                                    super.run();
                                    Looper.prepare();

                                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                                    long time = Calendar.getInstance().getTime().getTime();

                                    SpiroKitDatabase database = SpiroKitDatabase.getInstance(context);
                                    database.calHistoryRawDataDao().deleteReference(calHistory.getHashed());
                                    database.calHistoryDao().delete(calHistory.getHashed(), simpleDateFormat.format(time));

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
