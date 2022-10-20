package kr.co.theresearcher.spirokitfortab.main.result.svc;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

import kr.co.theresearcher.spirokitfortab.OnItemChangedListener;
import kr.co.theresearcher.spirokitfortab.OnItemDeletedListener;

import kr.co.theresearcher.spirokitfortab.R;
import kr.co.theresearcher.spirokitfortab.SharedPreferencesManager;
import kr.co.theresearcher.spirokitfortab.calc.DataHandlerE;
import kr.co.theresearcher.spirokitfortab.calc.DataHandlerU;
import kr.co.theresearcher.spirokitfortab.calc.SpiroKitHandler;
import kr.co.theresearcher.spirokitfortab.db.SpiroKitDatabase;
import kr.co.theresearcher.spirokitfortab.db.cal_history.CalHistory;
import kr.co.theresearcher.spirokitfortab.db.cal_history_raw_data.CalHistoryRawData;
import kr.co.theresearcher.spirokitfortab.db.operator.Operator;
import kr.co.theresearcher.spirokitfortab.db.patient.Patient;
import kr.co.theresearcher.spirokitfortab.db.work.Work;

import kr.co.theresearcher.spirokitfortab.dialog.ConfirmDialog;
import kr.co.theresearcher.spirokitfortab.graph.Coordinate;
import kr.co.theresearcher.spirokitfortab.graph.SlowVolumeTimeGraphView;

import kr.co.theresearcher.spirokitfortab.graph.VolumeFlowGraphView;
import kr.co.theresearcher.spirokitfortab.main.result.OnOrderSelectedListener;
import kr.co.theresearcher.spirokitfortab.main.result.pdf.SvcPdfCreator;
import kr.co.theresearcher.spirokitfortab.measurement.svc.MeasurementSvcActivity;
import kr.co.theresearcher.spirokitfortab.measurement.svc.ResultSVC;
import kr.co.theresearcher.spirokitfortab.measurement.svc.SvcResultAdapter;

public class SvcResultFragment extends Fragment implements Observer {

    private CalHistory history;
    private RecyclerView rv;
    private FrameLayout graphLayout;
    private ImageButton exportPdfButton;
    private List<SlowVolumeTimeGraphView> graphViews = new ArrayList<>();
    private SvcResultAdapter svcResultAdapter;
    private Context context;
    private TextView doctorText;
    private OnItemChangedListener changedListener;

    private Handler handler = new Handler(Looper.getMainLooper());

    public SvcResultFragment(CalHistory history) {
        this.history = history;
    }

    public void setChangedListener(OnItemChangedListener listener) {
        this.changedListener = listener;
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
        exportPdfButton = view.findViewById(R.id.img_btn_export_pdf_svc);
        graphLayout = view.findViewById(R.id.frame_svc_graph_result_fragment);

        doctorText = view.findViewById(R.id.tv_match_doctor_main_svc_result);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(container.getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        svcResultAdapter = new SvcResultAdapter(container.getContext());
        svcResultAdapter.setOrderSelectedListener(new OnOrderSelectedListener() {
            @Override
            public void onOrderSelected(int order) {
                selectResult(order);
            }
        });

        svcResultAdapter.setDeletedListener(new OnItemDeletedListener() {
            @Override
            public void onDeleted(int index) {

                graphViews.remove(index);
                selectResult(svcResultAdapter.getItemCount() - 1);
            }
        });

        svcResultAdapter.setOnItemChangedListener(new OnItemChangedListener() {
            @Override
            public void onChanged() {



            }
        });

        svcResultAdapter.addEmptyResult(new ResultSVC(""));

        rv.setLayoutManager(linearLayoutManager);
        rv.setAdapter(svcResultAdapter);

        rv.setVisibility(View.INVISIBLE);

        graphLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                graphLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                int width = graphLayout.getWidth();
                int height = graphLayout.getHeight();

                startDrawing(width, height);

            }
        });

        exportPdfButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    SpiroKitDatabase database = SpiroKitDatabase.getInstance(context);

                    Patient patient = database.patientDao()
                            .selectPatientByHash(SharedPreferencesManager.getPatientHashed(context));

                    CalHistoryRawData rawData = database.calHistoryRawDataDao().selectRawDataByHistory(
                            SharedPreferencesManager.getCalHistoryHash(context)
                    ).get(svcResultAdapter.getSelectedOrdinal());

                    PdfDocument pdfDocument = new PdfDocument();

                    //Size : pt 단위(== 1/72 inch == 1/72 * 2.54 mm)
                    float pageWidth = 545f;
                    float pageHeight = 841f;

                    PdfDocument.PageInfo info1 = new PdfDocument.PageInfo.Builder((int)pageWidth, (int)pageHeight, 1).create();
                    PdfDocument.Page page = pdfDocument.startPage(info1);

                    Canvas pageCanvas = page.getCanvas();

                    //그리기 영역
                    SvcPdfCreator pdfCreator = new SvcPdfCreator(context, patient, rawData);
                    pdfCreator.drawPDF(context, pageCanvas);

                    //그리기 영역

                    pdfDocument.finishPage(page);

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());

                    String fileName = patient.getName().replace('.', '_');
                    long time = Calendar.getInstance().getTime().getTime();
                    fileName += simpleDateFormat.format(time);
                    fileName += time + "";

                    File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                            fileName + ".pdf");

                    pdfDocument.writeTo(new FileOutputStream(file));

                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            ConfirmDialog dialog = new ConfirmDialog(context);
                            dialog.setTitle(getString(R.string.success_pdf_export));
                            dialog.show();
                        }
                    });

                } catch (IOException e) {

                    Log.e(getClass().getSimpleName(), "PDF WRITE Exception : " + e.toString());

                }

            }
        });

        setDoctors();

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

        Thread thread = new Thread() {

            @Override
            public void run() {
                super.run();
                Looper.prepare();

                SpiroKitDatabase database = SpiroKitDatabase.getInstance(context);

                List<CalHistoryRawData> rawData = database.calHistoryRawDataDao().selectRawDataByHistory(
                        history.getHashed()
                );

                CalHistory calHistory = database.calHistoryDao().select(SharedPreferencesManager.getCalHistoryHash(context));
                String version = calHistory.getDeviceDiv();


                for (int i = 0; i < rawData.size(); i++) {

                    CalHistoryRawData raw = rawData.get(i);

                    String[] data = raw.getData().split(" ");

                    SpiroKitHandler spiroKitHandler = null;

                    if (version.equals("e")) {
                        spiroKitHandler = new DataHandlerE();
                    } else if (version.equals("u")) {
                        spiroKitHandler = new DataHandlerU();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                exportPdfButton.setVisibility(View.INVISIBLE);
                            }
                        });
                    } else {

                    }

                    List<Integer> dataList = spiroKitHandler.convertAll(data);

                    double vc = spiroKitHandler.getVC(dataList);

                    graphViews.add(createVolumeTimeGraph(spiroKitHandler.getValues(dataList), width, height));

                    ResultSVC resultSVC = new ResultSVC(rawData.get(i).getHashed());

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
                    resultSVC.setOrder(rawData.get(i).getOrderNumber());

                    svcResultAdapter.addResult(resultSVC);


                }

                svcResultAdapter.setSelection(0);

                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        svcResultAdapter.notifyDataSetChanged();
                        graphLayout.removeAllViews();
                        graphLayout.addView(graphViews.get(0));

                        rv.setVisibility(View.VISIBLE);
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

    private SlowVolumeTimeGraphView createVolumeTimeGraph(List<Coordinate> coordinates, int width, int height) {

        SlowVolumeTimeGraphView graphView = new SlowVolumeTimeGraphView(context);
        graphView.setId(View.generateViewId());
        graphView.setCanvasSize(width, height);
        graphView.setX(60f, 0f);
        graphView.setY(0.5f * ((float)height / (float)width), -0.5f * ((float)height / (float)width));
        graphView.setMargin(30,30,60,30);

        graphView.commit();

        for (int i = 0; i < coordinates.size(); i++) {

            Coordinate coordinate = coordinates.get(i);

            graphView.setValue((float)coordinate.getTime(), (float)coordinate.getVolume(), (float)coordinate.getLps());

        }

        return graphView;

    }

    private VolumeFlowGraphView createVolumeFlowGraph(List<Coordinate> coordinates, int width, int height) {

        VolumeFlowGraphView volumeFlowResultView = new VolumeFlowGraphView(context);
        volumeFlowResultView.setId(View.generateViewId());
        volumeFlowResultView.setCanvasSize(width, height);
        volumeFlowResultView.setX(1.6f * (((float)width / (float)height)), 0f);
        volumeFlowResultView.setY(1.4f, -0.8f);
        volumeFlowResultView.setMargin(30,30,60,30);

        volumeFlowResultView.commit();

        for (int i = 0; i < coordinates.size(); i++) {

            Coordinate coordinate = coordinates.get(i);
            volumeFlowResultView.setValue((float)coordinate.getVolume(), (float)coordinate.getLps(), (float)coordinate.getTime());

        }

        return volumeFlowResultView;

    }

    private void selectResult(int order) {

        if (svcResultAdapter.getItemCount() == 0) {

            handler.post(new Runnable() {
                @Override
                public void run() {
                    changedListener.onChanged();
                }
            });

            return;
        }

        graphLayout.removeAllViews();
        graphLayout.addView(graphViews.get(order));
    }

    private void setDoctors() {

        SpiroKitDatabase database = SpiroKitDatabase.getInstance(context);

        List<Work> works = database.workDao().selectAllWork();
        String[] workNames = context.getResources().getStringArray(R.array.works);

        Operator familyDoctor = database.operatorDao().selectOperatorByHash(history.getFamilyDoctorHash());
        Operator checkupDoctor = database.operatorDao().selectOperatorByHash(history.getOperatorHashed());

        for (int i = 0; i < works.size(); i++) {
            if (works.get(i).getWork().equals(checkupDoctor.getWork())) {

                if (familyDoctor == null) {
                    doctorText.setText(getString(R.string.doctor_is, getString(R.string.not_applicable), getString(R.string.not_applicable), checkupDoctor.getName(), workNames[i]));

                } else {
                    doctorText.setText(getString(R.string.doctor_is, familyDoctor.getName(), getString(R.string.doctor), checkupDoctor.getName(), workNames[i]));
                }

                break;
            }
        }


    }

}