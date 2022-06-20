package kr.co.theresearcher.spirokitfortab.main.result.fvc;

import android.content.Context;
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
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import kr.co.theresearcher.spirokitfortab.R;
import kr.co.theresearcher.spirokitfortab.SharedPreferencesManager;
import kr.co.theresearcher.spirokitfortab.calc.CalcSpiroKitE;
import kr.co.theresearcher.spirokitfortab.db.measurement.Measurement;
import kr.co.theresearcher.spirokitfortab.graph.ResultCoordinate;
import kr.co.theresearcher.spirokitfortab.graph.VolumeFlowResultView;
import kr.co.theresearcher.spirokitfortab.graph.VolumeFlowRunView;
import kr.co.theresearcher.spirokitfortab.graph.VolumeTimeResultView;
import kr.co.theresearcher.spirokitfortab.graph.VolumeTimeResultView;
import kr.co.theresearcher.spirokitfortab.main.result.OnOrderSelectedListener;
import kr.co.theresearcher.spirokitfortab.measurement.fvc.FvcResultAdapter;
import kr.co.theresearcher.spirokitfortab.measurement.fvc.MeasurementFvcActivity;
import kr.co.theresearcher.spirokitfortab.measurement.fvc.ResultFVC;

public class FvcResultFragment extends Fragment {

    private Context context;

    private RecyclerView rv;
    private FvcResultAdapter adapter;
    private FrameLayout volumeFlowLayout, volumeTimeLayout;

    private List<VolumeFlowResultView> volumeFlowResultViews = new ArrayList<>();
    private List<VolumeTimeResultView> volumeTimeResultViews = new ArrayList<>();
    private Measurement measurement;
    private Handler handler = new Handler(Looper.getMainLooper());

    public FvcResultFragment() {
        // Required empty public constructor
    }

    public void setMeasurement(Measurement measurement) {
        this.measurement = measurement;
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
        adapter.setOnOrderSelectedListener(new OnOrderSelectedListener() {
            @Override
            public void onOrderSelected(int order) {

                selectData(order);

            }
        });

        adapter.addFvcResult(new ResultFVC());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rv.setLayoutManager(linearLayoutManager);
        rv.setAdapter(adapter);

        volumeFlowLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                volumeFlowLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                System.out.println("View tree observer ***************");

                int width = volumeFlowLayout.getWidth();
                int height = volumeFlowLayout.getHeight();

                startDrawing(width, height);


            }
        });

        volumeTimeLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                volumeTimeLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                int width = volumeTimeLayout.getWidth();
                int height = volumeTimeLayout.getHeight();

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

    //어댑터에서 index 정보 받아와서 아래 함수 호출하면 됨
    private void selectData(int order) {

        volumeFlowLayout.removeAllViews();
        volumeTimeLayout.removeAllViews();

        volumeFlowLayout.addView(volumeFlowResultViews.get(order));
        volumeTimeLayout.addView(volumeTimeResultViews.get(order));

    }


    private VolumeTimeResultView createVolumeTimeGraph(List<ResultCoordinate> coordinates, int width, int height) {

        VolumeTimeResultView volumeTimeResultView = new VolumeTimeResultView(context);
        volumeTimeResultView.setId(View.generateViewId());
        volumeTimeResultView.setCanvasSize(width, height);
        volumeTimeResultView.setX(0.8f, 0f);
        volumeTimeResultView.setY(0.4f, 0f);
        volumeTimeResultView.setMarkingCount(6, 8);
        volumeTimeResultView.commit();

        for (int i = 0; i < coordinates.size(); i++) {

            double x = coordinates.get(i).getX();
            double y = coordinates.get(i).getY();

            //여기서 flow 는 호기일 때만 그려지기 때문에 판단용이라서 없애도 되지만
            //다른 그래프에 쓰일 가능성도 있기 때문에 메서드를 수정하진 않았음.
            volumeTimeResultView.setValue((float)x, (float)y, 1f);

        }

        return volumeTimeResultView;
    }

    private VolumeFlowResultView createVolumeFlowGraph(List<ResultCoordinate> coordinates, int width, int height) {

        System.out.println("createVolumeFlowGraph()");

        VolumeFlowResultView volumeFlowResultView = new VolumeFlowResultView(context);
        volumeFlowResultView.setId(View.generateViewId());
        volumeFlowResultView.setCanvasSize(width, height);
        volumeFlowResultView.setX(2f, 0f);
        volumeFlowResultView.setY(1.25f, -1.25f);
        //startPosition 없어도 됨.
        volumeFlowResultView.setMarkingCount(6, 8);
        volumeFlowResultView.commit();

        for (int i = 0; i < coordinates.size(); i++) {

            double x = coordinates.get(i).getX();
            double y = coordinates.get(i).getY();

            volumeFlowResultView.setValue((float)x, (float)y);

        }

        return volumeFlowResultView;

    }

    private void startDrawing(int width, int height) {

        adapter.clear();

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

                        CalcSpiroKitE calc = new CalcSpiroKitE(pulseWidth);
                        calc.measure();

                        double fvc = calc.getFVC();
                        double fev1 = calc.getFev1();
                        double pef = calc.getPef();

                        double fvcP = calc.getFVCp(
                                SharedPreferencesManager.getPatientBirth(context),
                                SharedPreferencesManager.getPatientHeight(context),
                                SharedPreferencesManager.getPatientWeight(context),
                                SharedPreferencesManager.getPatientGender(context)
                        );

                        double fev1P = calc.getFEV1p(
                                SharedPreferencesManager.getPatientBirth(context),
                                SharedPreferencesManager.getPatientHeight(context),
                                SharedPreferencesManager.getPatientWeight(context),
                                SharedPreferencesManager.getPatientGender(context)
                        );

                        System.out.println(fvc + ", " + fev1 + ", " + pef);

                        List<ResultCoordinate> volumeFlowGraph = calc.getVolumeFlowGraph();
                        List<ResultCoordinate> volumeTimeGraph = calc.getForcedVolumeTimeGraph();

                        System.out.println(volumeFlowGraph.size());
                        System.out.println(volumeTimeGraph.size());

                        volumeFlowResultViews.add(createVolumeFlowGraph(volumeFlowGraph, width, height));
                        volumeTimeResultViews.add(createVolumeTimeGraph(volumeTimeGraph, width, height));

                        System.out.println(volumeFlowResultViews.size());
                        System.out.println(volumeTimeResultViews.size());
                        Log.d(getClass().getSimpleName(), volumeFlowResultViews.size() + ", " + volumeTimeResultViews.size());

                        ResultFVC resultFVC = new ResultFVC();
                        resultFVC.setFvc(fvc);
                        resultFVC.setFvcPredict(fvcP);

                        resultFVC.setFev1(fev1);
                        resultFVC.setFev1Predict(fev1P);

                        resultFVC.setFev1percent((fev1 / fvc) * 100f);
                        resultFVC.setFev1PercentPredict((fev1P / fvcP) * 100f);

                        resultFVC.setPef(pef);

                        resultFVC.setTimestamp(timestamp);
                        resultFVC.setPost(isPost);

                        if (i == 0) resultFVC.setSelected(true);
                        else resultFVC.setSelected(false);

                        adapter.addFvcResult(resultFVC);

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
                                volumeFlowLayout.removeAllViews();
                                volumeTimeLayout.removeAllViews();
                                volumeFlowLayout.addView(volumeFlowResultViews.get(0));
                                volumeTimeLayout.addView(volumeTimeResultViews.get(0));
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

}