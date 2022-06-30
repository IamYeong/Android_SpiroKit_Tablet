package kr.co.theresearcher.spirokitfortab.main.result.empty;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import kr.co.theresearcher.spirokitfortab.R;

public class EmptyResultFragment extends Fragment {

    public EmptyResultFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_empty_result, container, false);





        return view;
    }
}