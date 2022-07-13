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
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import kr.co.theresearcher.spirokitfortab.OnItemChangedListener;
import kr.co.theresearcher.spirokitfortab.R;
import kr.co.theresearcher.spirokitfortab.SharedPreferencesManager;
import kr.co.theresearcher.spirokitfortab.db.SpiroKitDatabase;
import kr.co.theresearcher.spirokitfortab.db.operator.Operator;
import kr.co.theresearcher.spirokitfortab.db.work.Work;

public class OperatorAdapter extends RecyclerView.Adapter<OperatorViewHolder> {

    private Context context;
    private List<Operator> operators;
    private List<Operator> searchResults;

    private Handler handler = new Handler(Looper.getMainLooper());
    private OnItemChangedListener changedListener;
    private List<Work> works;

    public OperatorAdapter(Context context) {
        this.context = context;
        operators = new ArrayList<>();
        searchResults = new ArrayList<>();
        works = SpiroKitDatabase.getInstance(context).workDao().selectAllWork();
        SpiroKitDatabase.removeInstance();
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

    public void filterByWork(String work) {

        searchResults.clear();

        for (Operator operator : operators) {
            if (operator.getWork().equals(work)) {

                searchResults.add(operator);
            }
        }

        notifyDataSetChanged();

    }

    public void setChangedListener(OnItemChangedListener listener) {
        this.changedListener = listener;
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

        holder.getDeleteButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        Looper.prepare();

                        SpiroKitDatabase.getInstance(context)
                                .operatorDao().delete(operator.getHashed());

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                changedListener.onChanged();
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

    private TextView nameText;
    private ImageButton deleteButton;

    public OperatorViewHolder(@NonNull View itemView) {
        super(itemView);


        nameText = itemView.findViewById(R.id.tv_operator_name_operator);
        deleteButton = itemView.findViewById(R.id.img_btn_delete_operator);

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
