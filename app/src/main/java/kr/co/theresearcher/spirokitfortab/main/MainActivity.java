package kr.co.theresearcher.spirokitfortab.main;

import androidx.appcompat.app.AppCompatActivity;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import kr.co.theresearcher.spirokitfortab.R;
import kr.co.theresearcher.spirokitfortab.main.information.PatientInformationFragment;
import kr.co.theresearcher.spirokitfortab.main.patients.PatientsFragment;
import kr.co.theresearcher.spirokitfortab.main.result.ResultFragment;

public class MainActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();

        setFragment(R.id.fragment_container_patients_main, new PatientsFragment());
        setFragment(R.id.fragment_container_patient_info_main, new PatientInformationFragment());
        setFragment(R.id.fragment_container_result_main, new ResultFragment());

    }

    private void setFragment(int id, Fragment fragment) {

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(id, fragment);
        fragmentTransaction.commit();

    }

}