package kr.co.theresearcher.spirokitfortab.main.result.svc;

import android.content.Context;
import android.icu.util.Measure;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import kr.co.theresearcher.spirokitfortab.R;
import kr.co.theresearcher.spirokitfortab.SharedPreferencesManager;
import kr.co.theresearcher.spirokitfortab.calc.CalcSpiroKitE;
import kr.co.theresearcher.spirokitfortab.calc.CalcSvcSpiroKitE;
import kr.co.theresearcher.spirokitfortab.db.measurement.Measurement;
import kr.co.theresearcher.spirokitfortab.graph.ResultCoordinate;
import kr.co.theresearcher.spirokitfortab.graph.SlowVolumeTimeRunView;
import kr.co.theresearcher.spirokitfortab.main.result.OnOrderSelectedListener;
import kr.co.theresearcher.spirokitfortab.measurement.fvc.ResultFVC;
import kr.co.theresearcher.spirokitfortab.measurement.svc.MeasurementSvcActivity;
import kr.co.theresearcher.spirokitfortab.measurement.svc.ResultSVC;
import kr.co.theresearcher.spirokitfortab.measurement.svc.SvcResultAdapter;

public class SvcResultFragment extends Fragment {

    private Measurement measurement;
    private RecyclerView rv;
    private FrameLayout graphLayout;
    private List<SlowVolumeTimeRunView> graphViews = new ArrayList<>();
    private SvcResultAdapter svcResultAdapter;
    private Context context;

    private Handler handler = new Handler(Looper.getMainLooper());

    public SvcResultFragment(Measurement measurement) {
        // Required empty public constructor
        this.measurement = measurement;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_svc_result, container, false);

        rv = view.findViewById(R.id.rv_result_svc_fragment);
        graphLayout = view.findViewById(R.id.frame_svc_graph_result_fragment);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(container.getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        svcResultAdapter = new SvcResultAdapter(container.getContext());
        svcResultAdapter.setOrderSelectedListener(new OnOrderSelectedListener() {
            @Override
            public void onOrderSelected(int order) {
                selectResult(order);
            }
        });
        svcResultAdapter.addEmptyResult(new ResultSVC());

        rv.setLayoutManager(linearLayoutManager);
        rv.setAdapter(svcResultAdapter);

        graphLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                graphLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                int width = graphLayout.getWidth();
                int height = graphLayout.getHeight();

                startDrawing(width, height);


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

    private void startDrawing(int width, int height) {

        //svcResultAdapter.clear();

        Thread thread = new Thread() {

            @Override
            public void run() {
                super.run();
                Looper.prepare();


                File root = context.getExternalFilesDir("data/"
                        + SharedPreferencesManager.getOfficeID(context) + "/"
                        + SharedPreferencesManager.getPatientId(context) + "/"
                        + measurement.getPath());

                File[] tests = root.listFiles();

                System.out.println(tests.length);

                for (int i = 0; i < tests.length; i++) {

                    int n = Integer.parseInt(tests[i].getName());
                    File jsonFile = new File(tests[i], n + ".json");
                    File csvFile = new File(tests[i], n + ".csv");

                    JSONObject jsonObject = readJsonFile(jsonFile);
                    List<Integer> pulseWidth = readCsvFile(csvFile);

                    try {

                        System.out.println(jsonObject.toString());
                        System.out.println(pulseWidth.size());


                        int pid = jsonObject.getInt("pid");
                        long timestamp = jsonObject.getLong("ts");
                        String measGroup = jsonObject.getString("meas");
                        int order = jsonObject.getInt("order");
                        boolean isPost = jsonObject.getBoolean("isPost");

                        CalcSvcSpiroKitE calc = new CalcSvcSpiroKitE(pulseWidth);
                        calc.measure();

                        double vc = calc.getVC();

                        graphViews.add(createVolumeTimeGraph(calc.getVolumeTimeGraph()));

                        ResultSVC resultSVC = new ResultSVC();
                        resultSVC.setTimestamp(timestamp);
                        resultSVC.setVc(vc);
                        resultSVC.setSelected(false);

                        if (i == 0) resultSVC.setSelected(true);
                        else resultSVC.setSelected(false);

                        svcResultAdapter.addResult(resultSVC);

                        handler.post(new Runnable() {
                            @Override
                            public void run() {

                                svcResultAdapter.notifyDataSetChanged();
                                graphLayout.removeAllViews();
                                graphLayout.addView(graphViews.get(0));
                            }
                        });


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }



                Looper.loop();

            }
        };

        thread.start();

    }

    private JSONObject readJsonFile(File file) {

        try {

            BufferedReader bufferedReader = new BufferedReader(
                    new FileReader(file)
            );

            StringBuilder stringBuilder = new StringBuilder();
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {

                stringBuilder.append(line);

            }

            return new JSONObject(stringBuilder.toString());

        } catch (IOException e) {

        } catch (JSONException e) {

        }

        return null;
    }

    private List<Integer> readCsvFile(File file) {

        List<Integer> list = new ArrayList<>();

        try {

            BufferedReader bufferedReader = new BufferedReader(
                    new FileReader(file)
            );

            StringBuilder stringBuilder = new StringBuilder();
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {

                list.add(Integer.parseInt(line));

            }

            return list;

        } catch (IOException e) {

        }

        return null;

    }

    private SlowVolumeTimeRunView createVolumeTimeGraph(List<ResultCoordinate> coordinates) {

        SlowVolumeTimeRunView graphView = new SlowVolumeTimeRunView(context);
        graphView.setId(View.generateViewId());
        graphView.setCanvasSize(graphLayout.getWidth(), graphLayout.getHeight());
        graphView.setMarkingCount(10, 8);
        graphView.setX(60f, 0f);
        graphView.setY(0.1f, -0.1f);

        graphView.commit();

        for (int i = 0; i < coordinates.size(); i++) {

            double x = coordinates.get(i).getX();
            double y = coordinates.get(i).getY();

            graphView.setValue((float)x, (float)y);

        }

        return graphView;

    }

    private void selectResult(int order) {
        graphLayout.removeAllViews();
        graphLayout.addView(graphViews.get(order));
    }

}