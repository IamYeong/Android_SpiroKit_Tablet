package kr.co.theresearcher.spirokitfortab;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import kr.co.theresearcher.spirokitfortab.IntentAttributes;

public class ExceptionActivity extends AppCompatActivity {

    //private TextView exceptionMessage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exception);

        Intent intent = getIntent();
        String message = intent.getStringExtra(IntentAttributes.INTENT_NAME_EXCEPTION);
        //Intent lastActivityIntent = intent.getParcelableExtra(IntentAttribues.INTENT_NAME_LAST_ACTIVITY);
        //Intent lastActivityIntent = new Intent(getApplicationContext(), TemposisMainActivity.class);

        TextView errorText = findViewById(R.id.tv_exception_message);
        TextView messageText = findViewById(R.id.tv_exception_user_message);
        Button button = findViewById(R.id.btn_confirm_exception);
        //ImageView exceptionImage = findViewById(R.id.img_exception_icon);

        messageText.setVisibility(View.INVISIBLE);
        errorText.setVisibility(View.VISIBLE);
        //Button button = findViewById(R.id.btn_error_back);
        System.out.println(message);
        errorText.setText(message);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(intent);
                finish();

            }
        });


        //============
        //String errorMessage = intent.getStringExtra("ERROR");
        //errorText.setText(errorMessage);

    }
}