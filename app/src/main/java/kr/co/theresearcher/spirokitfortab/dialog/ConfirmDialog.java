package kr.co.theresearcher.spirokitfortab.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import kr.co.theresearcher.spirokitfortab.R;

public class ConfirmDialog extends Dialog {

    private String title = "";
    private OnSelectedInDialogListener mListener;
    private boolean isSetListener = false;

    public void setTitle(String title) {
        this.title = title;
    }

    public ConfirmDialog(@NonNull Context context) {
        super(context);

    }

    public void setConfirmListener(OnSelectedInDialogListener listener) {
        this.mListener = listener;
        isSetListener = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawableResource(R.color.transparent);

        setContentView(R.layout.dialog_confirm);

        Button button = findViewById(R.id.btn_confirm_dialog);
        TextView titleText = findViewById(R.id.tv_title_confirm_dialog);

        titleText.setText(title);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isSetListener) mListener.onSelected(true);
                dismiss();
            }
        });

    }
}
