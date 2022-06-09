package kr.co.theresearcher.spirokitfortab.splash;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import kr.co.theresearcher.spirokitfortab.R;
import kr.co.theresearcher.spirokitfortab.login.LoginActivity;

public class SplashActivity extends AppCompatActivity {

    private TextView logo1, logo2, logo3, logo4, logo5, logo6, logo7, logo8, logo9, logo10, logo11;
    private List<TextView> logoList;
    private List<Animation> alphas;
    private Animation alpha1, alpha2, alpha3, alpha4, alpha5, alpha6, alpha7, alpha8, alpha9, alpha10, alpha11;
    private Thread thread;

    private String[] permissions;

    private List<String> deniedPermissions = new ArrayList<>();

    private ActivityResultLauncher<String[]> activityResultLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.RequestMultiplePermissions(),
                    new ActivityResultCallback<Map<String, Boolean>>() {
                        @Override
                        public void onActivityResult(Map<String, Boolean> result) {

                            for (String permission : result.keySet()) {

                                //해당 권한이 미동의 상태라면?
                                if (ContextCompat.checkSelfPermission(SplashActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {
                                    shouldShowRequestPermissionRationale(permission);
                                }

                            }

                            if (deniedPermissions.isEmpty()) {
                                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                //Toast.makeText(SignActivity.this, deniedPermissions.size() + "개의 권한 미동의", Toast.LENGTH_SHORT).show();
                                for (String permission : deniedPermissions) {
                                    shouldShowRequestPermissionRationale(permission);
                                }
                            }


                        }
                    }
            );


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        logo1 = findViewById(R.id.tv_logo_1);
        logo2 = findViewById(R.id.tv_logo_2);
        logo3 = findViewById(R.id.tv_logo_3);
        logo4 = findViewById(R.id.tv_logo_4);
        logo5 = findViewById(R.id.tv_logo_5);
        logo6 = findViewById(R.id.tv_logo_6);
        logo7 = findViewById(R.id.tv_logo_7);
        logo8 = findViewById(R.id.tv_logo_8);
        logo9 = findViewById(R.id.tv_logo_9);
        logo10 = findViewById(R.id.tv_logo_10);
        logo11 = findViewById(R.id.tv_logo_11);

        logoList = new ArrayList<>();

        logoList.add(logo1);
        logoList.add(logo2);
        logoList.add(logo3);
        logoList.add(logo4);
        logoList.add(logo5);
        logoList.add(logo6);
        logoList.add(logo7);
        logoList.add(logo8);
        logoList.add(logo9);
        logoList.add(logo10);
        logoList.add(logo11);

        alpha1 = AnimationUtils.loadAnimation(this, R.anim.logo_alpha);
        alpha2 = AnimationUtils.loadAnimation(this, R.anim.logo_alpha);
        alpha3 = AnimationUtils.loadAnimation(this, R.anim.logo_alpha);
        alpha4 = AnimationUtils.loadAnimation(this, R.anim.logo_alpha);
        alpha5 = AnimationUtils.loadAnimation(this, R.anim.logo_alpha);
        alpha6 = AnimationUtils.loadAnimation(this, R.anim.logo_alpha);
        alpha7 = AnimationUtils.loadAnimation(this, R.anim.logo_alpha);
        alpha8 = AnimationUtils.loadAnimation(this, R.anim.logo_alpha);
        alpha9 = AnimationUtils.loadAnimation(this, R.anim.logo_alpha);
        alpha10 = AnimationUtils.loadAnimation(this, R.anim.logo_alpha);
        alpha11 = AnimationUtils.loadAnimation(this, R.anim.logo_alpha);

        //setAlphaZero();
        alphas = new ArrayList<>();
        alphas.add(alpha1);
        alphas.add(alpha2);
        alphas.add(alpha3);
        alphas.add(alpha4);
        alphas.add(alpha5);
        alphas.add(alpha6);
        alphas.add(alpha7);
        alphas.add(alpha8);
        alphas.add(alpha9);
        alphas.add(alpha10);
        alphas.add(alpha11);


        alpha11.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                int userVersion = Build.VERSION.SDK_INT;

                if (userVersion <= 28) {

                    permissions = new String[] {
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.BLUETOOTH_ADMIN,
                            Manifest.permission.BLUETOOTH,

                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE

                    };

                } else if (userVersion <= 30) {

                    permissions = new String[] {
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.BLUETOOTH_ADMIN,
                            Manifest.permission.BLUETOOTH,

                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE

                    };

                } else {

                    permissions = new String[] {

                            //31부터는 FINE LOCATION 이 필요없다고 읽었었는데 재확인 필요.
                            //안드로이드 12 에서 스캔이 안 되는 현상 확인
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.BLUETOOTH_ADMIN,
                            Manifest.permission.BLUETOOTH,
                            Manifest.permission.BLUETOOTH_CONNECT,
                            Manifest.permission.BLUETOOTH_SCAN,

                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE

                    };

                }

                activityResultLauncher.launch(permissions);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        for (TextView tv : logoList) {

            tv.setVisibility(View.INVISIBLE);

        }

        timeThread();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (thread != null) {
            thread.interrupt();
        }
    }

    private void timeThread() {

        final Handler handler = new Handler();

        thread = new Thread() {

            @Override
            public void run() {
                super.run();

                try {

                    for (int i = 0; i < logoList.size(); i++) {

                        TextView textView = logoList.get(i);
                        Animation alpha = alphas.get(i);

                        handler.post(new Runnable() {
                            @Override
                            public void run() {

                                textView.setVisibility(View.VISIBLE);
                                textView.startAnimation(alpha);

                            }
                        });

                        Thread.sleep(50);

                    }

                } catch(Exception e) {

                }



            }
        };

        thread.start();


    }
}