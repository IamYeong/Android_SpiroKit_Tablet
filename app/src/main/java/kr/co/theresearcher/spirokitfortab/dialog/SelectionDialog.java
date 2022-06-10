package kr.co.theresearcher.spirokitfortab.dialog;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import kr.co.theresearcher.spirokitfortab.R;

public class SelectionDialog extends Dialog {

    private Button rejectButton, agreeButton;
    private TextView titleText;
    private String title = "";
    private OnSelectedInDialogListener mListener;

    public SelectionDialog(@NonNull Context context) {
        super(context);

    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSelectedListener(OnSelectedInDialogListener listener) {
        this.mListener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawableResource(R.color.transparent);
        setContentView(R.layout.dialog_selection);

        titleText = findViewById(R.id.tv_title_selection_dialog);
        rejectButton = findViewById(R.id.btn_reject_selection_dialog);
        agreeButton = findViewById(R.id.btn_agree_selection_dialog);

        titleText.setText(title);

        rejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mListener.onSelected(false);
                dismiss();

            }
        });

        agreeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mListener.onSelected(true);
                dismiss();

            }
        });

    }
}
