package kr.co.theresearcher.spirokitfortab.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import kr.co.theresearcher.spirokitfortab.R;

public class SyncLoadingDialog extends Dialog {

    private ProgressBar loadingProgressBar;
    private TextView titleText;
    private Handler handler = new Handler(Looper.getMainLooper());

    public SyncLoadingDialog(@NonNull Context context) {
        super(context);
    }

    public void setTitle(String title) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                titleText.setText(title);
            }
        });
    }

    public void setProgress(float progress) {

        handler.post(new Runnable() {
            @Override
            public void run() {
                loadingProgressBar.setProgress((int)((float)loadingProgressBar.getMax() * progress));
            }
        });

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
        titleText.setText(getContext().getString(R.string.server_sync_writing));
        loadingProgressBar.setProgress(0);


    }
}
