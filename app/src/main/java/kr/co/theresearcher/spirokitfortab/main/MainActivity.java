package kr.co.theresearcher.spirokitfortab.main;

import androidx.appcompat.app.AppCompatActivity;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import kr.co.theresearcher.spirokitfortab.R;
import kr.co.theresearcher.spirokitfortab.main.information.PatientInformationFragment;
import kr.co.theresearcher.spirokitfortab.main.patients.PatientsFragment;
import kr.co.theresearcher.spirokitfortab.main.result.ResultFragment;
import kr.co.theresearcher.spirokitfortab.setting.SettingActivity;

public class MainActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;
    private ImageButton settingButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        settingButton = findViewById(R.id.img_btn_setting_main);

        fragmentManager = getSupportFragmentManager();

        setFragment(R.id.fragment_container_patients_main, new PatientsFragment());
        setFragment(R.id.fragment_container_patient_info_main, new PatientInformationFragment());
        setFragment(R.id.fragment_container_result_main, new ResultFragment());

        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });

    }

    private void setFragment(int id, Fragment fragment) {

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(id, fragment);
        fragmentTransaction.commit();

    }

}