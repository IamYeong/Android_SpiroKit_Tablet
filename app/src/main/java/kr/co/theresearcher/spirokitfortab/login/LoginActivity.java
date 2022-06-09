
package kr.co.theresearcher.spirokitfortab.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import kr.co.theresearcher.spirokitfortab.R;
import kr.co.theresearcher.spirokitfortab.join.ConditionAgreeActivity;
import kr.co.theresearcher.spirokitfortab.join.JoinUserActivity;

public class LoginActivity extends AppCompatActivity {

    private Button loginButton, moveToJoinButton;
    private EditText emailField, passwordField;

    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailField = findViewById(R.id.et_email_login);
        passwordField = findViewById(R.id.et_password_login);
        loginButton = findViewById(R.id.btn_user_login);
        moveToJoinButton = findViewById(R.id.btn_to_register_from_login);

        moveToJoinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(LoginActivity.this, ConditionAgreeActivity.class);
                startActivity(intent);

            }
        });

    }
}