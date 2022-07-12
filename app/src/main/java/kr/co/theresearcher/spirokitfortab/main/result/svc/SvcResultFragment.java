package kr.co.theresearcher.spirokitfortab.main.result.svc;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

import kr.co.theresearcher.spirokitfortab.OnItemChangedListener;
import kr.co.theresearcher.spirokitfortab.R;
import kr.co.theresearcher.spirokitfortab.SharedPreferencesManager;
import kr.co.theresearcher.spirokitfortab.calc.CalcSvcSpiroKitE;
import kr.co.theresearcher.spirokitfortab.db.SpiroKitDatabase;
import kr.co.theresearcher.spirokitfortab.db.cal_history.CalHistory;
import kr.co.theresearcher.spirokitfortab.db.cal_history_raw_data.CalHistoryRawData;
import kr.co.theresearcher.spirokitfortab.graph.ResultCoordinate;
import kr.co.theresearcher.spirokitfortab.graph.SlowVolumeTimeRunView;
import kr.co.theresearcher.spirokitfortab.main.result.OnOrderSelectedListener;
import kr.co.theresearcher.spirokitfortab.measurement.svc.ResultSVC;
import kr.co.theresearcher.spirokitfortab.measurement.svc.SvcResultAdapter;

public class SvcResultFragment extends Fragment implements Observer {

    private CalHistory history;
    private RecyclerView rv;
    private FrameLayout graphLayout;
    private List<SlowVolumeTimeRunView> graphViews = new ArrayList<>();
    private SvcResultAdapter svcResultAdapter;
    private Context context;
    private TextView matchDoctorText, measDoctorText;

    private Handler handler = new Handler(Looper.getMainLooper());

    public SvcResultFragment(CalHistory history) {
        this.history = history;
    }

    @Override
    public void update(Observable o, Object arg) {
        Log.d(getClass().getSimpleName(), "SVC FRAGMENT : TOUCH");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_svc_result, container, false);

        rv = view.findViewById(R.id.rv_result_svc_fragment);
        graphLayout = view.findViewById(R.id.frame_svc_graph_result_fragment);

        measDoctorText = view.findViewById(R.id.tv_meas_doctor_main_svc_result);
        matchDoctorText = view.findViewById(R.id.tv_match_doctor_main_svc_result);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(container.getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        svcResultAdapter = new SvcResultAdapter(container.getContext());
        svcResultAdapter.setOrderSelectedListener(new OnOrderSelectedListener() {
            @Override
            public void onOrderSelected(int order) {
                selectResult(order);
            }
        });

        svcResultAdapter.setOnItemChangedListener(new OnItemChangedListener() {
            @Override
            public void onChanged() {

                svcResultAdapter.clear();
                startDrawing(graphLayout.getWidth(), graphLayout.getHeight());

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

                SpiroKitDatabase database = SpiroKitDatabase.getInstance(context);

                List<CalHistoryRawData> rawData = database.calHistoryRawDataDao().selectRawDataByHistory(
                        history.getHashed()
                );

                List<CalHistoryRawData> allData = database.calHistoryRawDataDao().selectAll();
                for (CalHistoryRawData cal : allData) {
                    Log.d(getClass().getSimpleName(), "CAL HISTORY HASH : " + cal.getCalHistoryHashed() + "\nDATA : " + cal.getData());
                }

                SpiroKitDatabase.removeInstance();

                Log.d(getClass().getSimpleName(), "RAW DATA SIZE : " + rawData.size());

                if (rawData.size() == 0) return;

                for (int i = 0; i < rawData.size(); i++) {

                    String[] data = rawData.get(i).getData().split(" ");
                    List<Integer> pulseWidth = new ArrayList<>();

                    for (int j = 0; j < data.length; j++) {

                        pulseWidth.add(Integer.parseInt(data[j]));

                    }

                    CalcSvcSpiroKitE calc = new CalcSvcSpiroKitE(pulseWidth);
                    calc.measure();

                    double vc = calc.getVC();

                    graphViews.add(createVolumeTimeGraph(calc.getVolumeTimeGraph(), width, height));

                    ResultSVC resultSVC = new ResultSVC();

                    String dateString = rawData.get(i).getCalDate();
                    dateString = dateString.substring(0, dateString.length() - 7);
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

                    try {

                        long time = simpleDateFormat.parse(dateString).getTime();
                        resultSVC.setTimestamp(time);

                    } catch (ParseException e) {
                        resultSVC.setTimestamp(0);
                    }
                    resultSVC.setVc(vc);
                    resultSVC.setPost(rawData.get(i).getIsPost());

                    if (i == 0) resultSVC.setSelected(true);
                    else resultSVC.setSelected(false);

                    svcResultAdapter.addResult(resultSVC);


                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        svcResultAdapter.notifyDataSetChanged();
                        graphLayout.removeAllViews();
                        graphLayout.addView(graphViews.get(0));
                    }
                });

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

    private SlowVolumeTimeRunView createVolumeTimeGraph(List<ResultCoordinate> coordinates, int width, int height) {

        SlowVolumeTimeRunView graphView = new SlowVolumeTimeRunView(context);
        graphView.setId(View.generateViewId());
        graphView.setCanvasSize(width, height);
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

    private void setDoctors() {

    }

}