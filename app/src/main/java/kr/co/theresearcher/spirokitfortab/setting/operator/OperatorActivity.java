package kr.co.theresearcher.spirokitfortab.setting.operator;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Looper;

import kr.co.theresearcher.spirokitfortab.R;

public class OperatorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operator);



    }

    @Override
    protected void onPause() {
        super.onPause();


    }

    @Override
    protected void onResume() {
        super.onResume();

        Thread thread = new Thread() {

            @Override
            public void run() {
                super.run();
                Looper.prepare();


                Looper.loop();
            }
        };

        thread.start();
    }
}