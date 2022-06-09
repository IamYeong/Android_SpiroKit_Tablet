package kr.co.theresearcher.spirokitfortab.join;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import kr.co.theresearcher.spirokitfortab.R;

public class JoinUserActivity extends AppCompatActivity {

    private Button joinButton;
    private ImageButton backButton;
    private EditText nickNameField, emailField, passwordField, passConfirmField;

    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_user);

        joinButton = findViewById(R.id.btn_join_user);
        backButton = findViewById(R.id.img_btn_back_user_join);


        nickNameField = findViewById(R.id.et_nickname_join);
        emailField = findViewById(R.id.et_email_join);
        passwordField = findViewById(R.id.et_password_join);
        passConfirmField = findViewById(R.id.et_password_confirm_join);

        nickNameField.setText("");
        emailField.setText("");
        passwordField.setText("");
        passConfirmField.setText("");



    }
}