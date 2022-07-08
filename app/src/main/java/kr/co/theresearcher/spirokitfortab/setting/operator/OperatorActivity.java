package kr.co.theresearcher.spirokitfortab.setting.operator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import kr.co.theresearcher.spirokitfortab.R;
import kr.co.theresearcher.spirokitfortab.SharedPreferencesManager;
import kr.co.theresearcher.spirokitfortab.db.operator.Operator;
import kr.co.theresearcher.spirokitfortab.db.work.Work;

public class OperatorActivity extends AppCompatActivity {

    private RecyclerView rv;
    private AppCompatSpinner spinner;
    private EditText nameTextField;
    private Button addButton;
    private OperatorAdapter adapter;
    private ImageButton backButton;

    private Handler handler = new Handler(Looper.getMainLooper());
    private boolean isSpinnerInit = false;
    private int workID = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operator);

        addButton = findViewById(R.id.btn_add_operator);
        nameTextField = findViewById(R.id.et_operator_name);
        spinner = findViewById(R.id.spinner_operator_work);
        rv = findViewById(R.id.rv_operator);
        backButton = findViewById(R.id.img_btn_back_operator);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        adapter = new OperatorAdapter(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(linearLayoutManager);
        rv.setAdapter(adapter);


        List<String> works = new ArrayList<>();
        //for (Work work : Work.values()) works.add(work.name().toUpperCase(Locale.ROOT));
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, works
        );

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                workID = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner.setAdapter(arrayAdapter);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Thread thread = new Thread() {

                    @Override
                    public void run() {
                        super.run();
                        Looper.prepare();

                        String name = nameTextField.getText().toString();

                        if (name.length() == 0) {


                            return;
                        }

                        if (workID < 0) {

                            return;
                        }

                        Operator operator = new Operator();

                        //OperatorDatabase.getInstance(OperatorActivity.this).operatorDao().updateOperator(operator);

                        adapter.addOperator(operator);

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
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
    protected void onPause() {
        super.onPause();


    }

    @Override
    protected void onResume() {
        super.onResume();

        updateOperators();

    }

    private void updateOperators() {

        Thread thread = new Thread() {

            @Override
            public void run() {
                super.run();
                Looper.prepare();

                List<Operator> operators = new ArrayList<>();

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        adapter.setOperators(operators);
                        adapter.notifyDataSetChanged();
                    }
                });


                Looper.loop();
            }
        };

        thread.start();

    }
}