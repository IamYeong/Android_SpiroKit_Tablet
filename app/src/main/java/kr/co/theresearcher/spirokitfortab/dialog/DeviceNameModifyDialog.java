package kr.co.theresearcher.spirokitfortab.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import kr.co.theresearcher.spirokitfortab.R;

public class DeviceNameModifyDialog extends Dialog {

    private TextView currentFullNameText, currentFixNameText;
    private EditText modifyNumberField;
    private Button confirmButton, rejectButton;
    private String currentName;
    private OnTextInputListener inputListener;

    public DeviceNameModifyDialog(@NonNull Context context) {
        super(context);

    }

    public void setCurrentDeviceName(String currentName) {
        this.currentName = currentName;
    }

    public void setInputListener(OnTextInputListener listener) {
        this.inputListener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawableResource(R.color.transparent);
        setContentView(R.layout.dialog_device_name_modify);

        currentFullNameText = findViewById(R.id.tv_current_device_name_dialog);
        currentFixNameText = findViewById(R.id.tv_modify_device_name_dialog);
        modifyNumberField = findViewById(R.id.et_modify_device_name_number_dialog);
        confirmButton = findViewById(R.id.btn_agree_name_modify_dialog);
        rejectButton = findViewById(R.id.btn_reject_name_modify_dialog);

        currentFullNameText.setText(currentName);
        currentFixNameText.setText(currentName.substring(0, 10));
        modifyNumberField.setHint(currentName.substring(currentName.length() - 2));

        rejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String modifyName = "";
                modifyName += modifyNumberField.getText().toString();
                if (modifyName.equals("")) {

                    return;
                }

                try {

                    int number = Integer.parseInt(modifyNumberField.getText().toString());
                    if ((number < 0) || (number > 99)) {

                        return;
                    }

                } catch (NumberFormatException e) {
                    return;
                }

                inputListener.onTextInput(modifyNumberField.getText().toString());

                dismiss();

            }
        });

        modifyNumberField.requestFocus();

    }
}
