package kr.co.theresearcher.spirokitfortab.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import kr.co.theresearcher.spirokitfortab.R;

public class TextInputDialog extends Dialog {

    private EditText inputFileNameField;
    private Button rejectButton, agreeButton;
    private OnTextInputListener mTextListener;

    public TextInputDialog(@NonNull Context context) {
        super(context);
    }

    public void setTextInputListener(OnTextInputListener listener) {
        this.mTextListener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawableResource(R.color.transparent);
        getWindow().setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        setContentView(R.layout.dialog_text_input);

        inputFileNameField = findViewById(R.id.et_input_file_name_result);
        rejectButton = findViewById(R.id.btn_reject_text_input_dialog);
        agreeButton = findViewById(R.id.btn_agree_text_input_dialog);

        rejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dismiss();

            }
        });

        agreeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = inputFileNameField.getText().toString();
                mTextListener.onTextInput(name);
                dismiss();

            }
        });

    }
}
