package kr.co.theresearcher.spirokitfortab.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;

import kr.co.theresearcher.spirokitfortab.R;

public class LoadingDialog extends Dialog {

    private TextView loadingDialogText;
    private String title = "";

    public void setTitle(String title) {
        this.title = title;
    }

    public LoadingDialog(@NonNull Context context) {
        super(context);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawableResource(R.color.transparent);
        setContentView(R.layout.dialog_loading);
        setCancelable(false);

        loadingDialogText = findViewById(R.id.tv_title_loading_dialog);
        loadingDialogText.setText(title);

    }
}
