package kr.co.theresearcher.spirokitfortab.join;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;

import kr.co.theresearcher.spirokitfortab.R;

public class ConditionAgreeActivity extends AppCompatActivity {

    private CheckBox conditionCheckBox, personalInfoCheckBox;
    private ImageButton backButton;
    private WebView agreeWebView;

    private boolean agree = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_condition_agree);

        conditionCheckBox = findViewById(R.id.check_box_terms_conditions);
        backButton = findViewById(R.id.img_btn_back_terms_conditions);

        agreeWebView = findViewById(R.id.web_condition_agree);

        agreeWebView.loadUrl("file:///android_asset/www/agree.html");
        Button button = findViewById(R.id.btn_next_terms_conditions);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!agree) {

                    /*
                    ConfirmDialog confirmDialog = new ConfirmDialog(TermsConditionsActivity.this);
                    confirmDialog.setTitle(getString(R.string.request_must_be_agree));
                    confirmDialog.show();

                     */

                    return;
                }

                Intent intent = new Intent(ConditionAgreeActivity.this, JoinUserActivity.class);
                startActivity(intent);
                finish();
            }
        });

        conditionCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                agree = isChecked;
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}