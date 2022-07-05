package kr.co.theresearcher.spirokitfortab.setting.operator;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import kr.co.theresearcher.spirokitfortab.R;
import kr.co.theresearcher.spirokitfortab.SharedPreferencesManager;
import kr.co.theresearcher.spirokitfortab.db.RoomNames;
import kr.co.theresearcher.spirokitfortab.db.work.Work;

public class OperatorAdapter extends RecyclerView.Adapter<OperatorViewHolder> {

    private Context context;
    private List<Operator> operators;
    private List<Operator> searchResults;

    private Handler handler = new Handler(Looper.getMainLooper());

    public OperatorAdapter(Context context) {
        this.context = context;
        operators = new ArrayList<>();
        searchResults = new ArrayList<>();
    }

    public void setOperators(List<Operator> operators) {
        this.operators.clear();
        this.searchResults.clear();

        this.operators.addAll(operators);
        this.searchResults.addAll(operators);
    }

    public void addOperator(Operator operator) {
        operators.add(operator);
        searchResults.add(operator);

    }

    @NonNull
    @Override
    public OperatorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_operator_box, parent, false);
        return new OperatorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OperatorViewHolder holder, int position) {

        Operator operator = searchResults.get(holder.getAdapterPosition());

        holder.getNameText().setText(operator.getName());
        Work[] works = Work.values();
        holder.getWorkText().setText(works[operator.getWorkID()].toString().toUpperCase(Locale.ROOT));

        holder.getDeleteButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        Looper.prepare();

                        OperatorDatabase operatorDatabase = Room.databaseBuilder(context, OperatorDatabase.class, RoomNames.ROOM_OPERATOR_DB_NAME).build();
                        OperatorDao operatorDao = operatorDatabase.operatorDao();
                        operatorDao.deleteOperator(operator);

                        setOperators(operatorDao.selectByOfficeID(SharedPreferencesManager.getOfficeID(context)));

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                notifyDataSetChanged();
                            }
                        });

                        Looper.loop();
                    }
                };

                thread.start();

            }
        });

    }

    @Override
    public int getItemCount() {
        return (searchResults != null ? searchResults.size() : 0);
    }
}


class OperatorViewHolder extends RecyclerView.ViewHolder {

    private TextView workText, nameText;
    private ImageButton deleteButton;

    public OperatorViewHolder(@NonNull View itemView) {
        super(itemView);

        workText = itemView.findViewById(R.id.tv_operator_work_operator);
        nameText = itemView.findViewById(R.id.tv_operator_name_operator);
        deleteButton = itemView.findViewById(R.id.img_btn_delete_operator);

    }

    public TextView getWorkText() {
        return workText;
    }

    public void setWorkText(TextView workText) {
        this.workText = workText;
    }

    public TextView getNameText() {
        return nameText;
    }

    public void setNameText(TextView nameText) {
        this.nameText = nameText;
    }

    public ImageButton getDeleteButton() {
        return deleteButton;
    }

    public void setDeleteButton(ImageButton deleteButton) {
        this.deleteButton = deleteButton;
    }
}
