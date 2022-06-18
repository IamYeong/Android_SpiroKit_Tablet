package kr.co.theresearcher.spirokitfortab.main.result.fvc;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.TextView;

import kr.co.theresearcher.spirokitfortab.R;
import kr.co.theresearcher.spirokitfortab.graph.VolumeFlowResultView;
import kr.co.theresearcher.spirokitfortab.graph.VolumeFlowRunView;
import kr.co.theresearcher.spirokitfortab.graph.VolumeTimeResultView;
import kr.co.theresearcher.spirokitfortab.graph.VolumeTimeResultView;
import kr.co.theresearcher.spirokitfortab.measurement.fvc.FvcResultAdapter;
import kr.co.theresearcher.spirokitfortab.measurement.fvc.MeasurementFvcActivity;

public class FvcResultFragment extends Fragment {

    private Context context;

    private RecyclerView rv;
    private FvcResultAdapter adapter;
    private VolumeFlowResultView volumeFlowResultView;
    private VolumeTimeResultView volumeTimeResultView;
    private FrameLayout volumeFlowLayout, volumeTimeLayout;

    public FvcResultFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_result, container, false);

        rv = view.findViewById(R.id.rv_result_fvc_fragment);
        volumeFlowLayout = view.findViewById(R.id.frame_volume_flow_graph_result_fragment);
        volumeTimeLayout = view.findViewById(R.id.frame_volume_time_graph_result_fragment);

        adapter = new FvcResultAdapter(context);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rv.setLayoutManager(linearLayoutManager);
        rv.setAdapter(adapter);

        volumeFlowLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                volumeFlowLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                int width = volumeFlowLayout.getWidth();
                int height = volumeFlowLayout.getHeight();

                volumeFlowResultView = new VolumeFlowResultView(container.getContext());
                volumeFlowResultView.setId(View.generateViewId());
                volumeFlowResultView.setCanvasSize(width, height);
                volumeFlowResultView.setX(2f, 0f);
                volumeFlowResultView.setY(1.25f, -1.25f);
                volumeFlowResultView.setxStartPosition(0.5f);
                volumeFlowResultView.setMarkingCount(6, 10);

                volumeFlowResultView.commit();

                volumeFlowLayout.addView(volumeFlowResultView);

            }
        });

        volumeTimeLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                volumeTimeLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                int width = volumeTimeLayout.getWidth();
                int height = volumeTimeLayout.getHeight();

                volumeTimeResultView = new VolumeTimeResultView(container.getContext());
                volumeTimeResultView.setId(View.generateViewId());

                volumeTimeResultView.setCanvasSize(width, height);
                volumeTimeResultView.setX(0.8f, 0f);
                volumeTimeResultView.setY(0.4f, 0f);
                volumeTimeResultView.setMarkingCount(8, 6);
                volumeTimeResultView.setxStartPosition(0f);

                volumeTimeResultView.commit();

                volumeTimeLayout.addView(volumeTimeResultView);
            }
        });

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}