package kr.co.theresearcher.spirokitfortab.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import kr.co.theresearcher.spirokitfortab.R;

public class SyncLoadingDialog extends Dialog {

    private ProgressBar loadingProgressBar;
    private TextView titleText;
    private Button cancelButton;
    private OnCancelListener cancelListener;
    private Handler handler = new Handler(Looper.getMainLooper());

    public SyncLoadingDialog(@NonNull Context context) {
        super(context);
    }

    public void setCancelListener(OnCancelListener listener) {
        this.cancelListener = listener;
    }

    public void setTitle(String title) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                titleText.setText(title);
            }
        });
    }

    public void setMaxProgress(int max) {

        loadingProgressBar.setMax(max);

    }

    public void incrementProgress() {
        loadingProgressBar.setProgress(loadingProgressBar.getProgress() + 1);
    }

    public void setProgress(int progress) {

        loadingProgressBar.setProgress(progress);

    }

    public void setCancelableUseButton(boolean cancelable) {

        if (cancelable) enableButton(cancelButton);
        else disableButton(cancelButton);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_sync_loading);
        setCancelable(false);
        getWindow().setBackgroundDrawableResource(R.color.transparent);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        loadingProgressBar = findViewById(R.id.progressbar_sync_loading);
        titleText = findViewById(R.id.tv_sync_progress);
        cancelButton = findViewById(R.id.btn_cancel_sync_progress);

        titleText.setText(getContext().getString(R.string.server_sync_writing));
        loadingProgressBar.setProgress(0);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        disableButton(cancelButton);

    }

    private void disableButton(Button button) {

        button.setBackgroundTintList(ColorStateList.valueOf(getContext().getColor(R.color.gray_background)));
        button.setTextColor(ColorStateList.valueOf(getContext().getColor(R.color.gray_dark)));
        button.setClickable(false);

    }

    private void enableButton(Button button) {

        button.setBackgroundTintList(ColorStateList.valueOf(getContext().getColor(R.color.white)));
        button.setTextColor(ColorStateList.valueOf(getContext().getColor(R.color.black)));
        button.setClickable(true);

    }

}
