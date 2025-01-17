package kr.co.theresearcher.spirokitfortab.main.result.fvc;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.fonts.FontFamily;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.room.RoomDatabase;

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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalField;
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

import kr.co.theresearcher.spirokitfortab.graph.VolumeTimeGraphView;

import kr.co.theresearcher.spirokitfortab.main.result.OnOrderSelectedListener;
import kr.co.theresearcher.spirokitfortab.main.result.pdf.FvcPdfCreator;
import kr.co.theresearcher.spirokitfortab.measurement.fvc.FvcResultAdapter;
import kr.co.theresearcher.spirokitfortab.measurement.fvc.ResultFVC;

public class FvcResultFragment extends Fragment implements Observer {

    private Context context;

    private RecyclerView rv;
    private FvcResultAdapter adapter;
    private FrameLayout volumeFlowLayout, volumeTimeLayout;
    private ImageButton exportPdfButton;

    private TextView doctorText, export;

    private List<VolumeFlowGraphView> volumeFlowResultViews = new ArrayList<>();
    private List<VolumeTimeGraphView> volumeTimeResultViews = new ArrayList<>();
    private List<SlowVolumeTimeGraphView> slowVolumeTimeGraphViews = new ArrayList<>();
    private CalHistory history;
    private Handler handler = new Handler(Looper.getMainLooper());
    private OnItemChangedListener changedListener;

    public FvcResultFragment(CalHistory history) {
        this.history = history;
    }

    @Override
    public void update(Observable o, Object arg) {

        Log.d(getClass().getSimpleName(), "FVC FRAGMENT : TOUCH");

    }

    public void setChangedListener(OnItemChangedListener listener) {
        this.changedListener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_result, container, false);

        rv = view.findViewById(R.id.rv_result_fvc_fragment);
        volumeFlowLayout = view.findViewById(R.id.frame_volume_flow_graph_result_fragment);
        volumeTimeLayout = view.findViewById(R.id.frame_volume_time_graph_result_fragment);
        exportPdfButton = view.findViewById(R.id.img_btn_export_pdf_fvc);

        doctorText = view.findViewById(R.id.tv_doctor_main_result);

        export = view.findViewById(R.id.tv_export_fvc);

        adapter = new FvcResultAdapter(context);
        //adapter.setRootTimestamp(measurement.getMeasDate());
        adapter.setOnOrderSelectedListener(new OnOrderSelectedListener() {
            @Override
            public void onOrderSelected(int order) {

                selectData(order);

            }
        });

        adapter.setDeletedListener(new OnItemDeletedListener() {
            @Override
            public void onDeleted(int index) {
                //adapter.clear();
                //startDrawing(volumeFlowLayout.getWidth(), volumeFlowLayout.getHeight());

                volumeFlowResultViews.remove(index);
                volumeTimeResultViews.remove(index);
                selectData(adapter.getItemCount() - 1);
            }
        });

        adapter.setChangedListener(new OnItemChangedListener() {
            @Override
            public void onChanged() {

                //adapter.clear();
                //startDrawing(volumeFlowLayout.getWidth(), volumeFlowLayout.getHeight());

            }
        });

        adapter.addEmptyObject(new ResultFVC(""));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rv.setLayoutManager(linearLayoutManager);
        rv.setAdapter(adapter);

        rv.setVisibility(View.INVISIBLE);

        volumeFlowLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                volumeFlowLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                System.out.println("View tree observer ***************");

                int width = volumeFlowLayout.getWidth();
                int height = volumeFlowLayout.getHeight();

                Log.e(getClass().getSimpleName(), "WIDTH : " + width + ", HEIGHT : " + height + ", RATIO : " + (float)((float)width / (float)height));

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

        exportPdfButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    SpiroKitDatabase database = SpiroKitDatabase.getInstance(context);

                    Patient patient = database.patientDao()
                            .selectPatientByHash(SharedPreferencesManager.getPatientHashed(context));

                    CalHistoryRawData rawData = database.calHistoryRawDataDao().selectRawDataByHistory(
                            SharedPreferencesManager.getCalHistoryHash(context)
                    ).get(adapter.getSelectedOrdinal());

                    PdfDocument pdfDocument = new PdfDocument();

                    //Size : pt 단위(== 1/72 inch == 1/72 * 2.54 mm)
                    PdfDocument.PageInfo info1 = new PdfDocument.PageInfo.Builder(545, 841, 1).create();
                    PdfDocument.Page page = pdfDocument.startPage(info1);

                    Canvas pageCanvas = page.getCanvas();

                    //그리기 영역
                    FvcPdfCreator creator = new FvcPdfCreator(context, patient, rawData);
                    creator.drawPDF(context, pageCanvas);

                    //그래프 넣기


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

        export.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        Looper.prepare();

                        SpiroKitDatabase database = SpiroKitDatabase.getInstance(context);

                        List<CalHistoryRawData> rawData = database.calHistoryRawDataDao().selectRawDataByHistory(
                                SharedPreferencesManager.getCalHistoryHash(context)
                        );

                        CalHistoryRawData calHistoryRawData = rawData.get(adapter.getSelectedOrdinal());
                        String[] data = calHistoryRawData.getData().split(" ");

                        try {

                            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                            File exportFile = new File(path, calHistoryRawData.getHashed().substring(0, 10) + "_" + calHistoryRawData.getOrderNumber() + ".csv");
                            //exportFile.createNewFile();

                            FileWriter fileWriter = new FileWriter(exportFile);
                            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

                            for (int j = 0; j < data.length; j++) {

                                bufferedWriter.write(data[j]);
                                bufferedWriter.write("\n");

                            }

                            bufferedWriter.close();

                            handler.post(new Runnable() {
                                @Override
                                public void run() {

                                    ConfirmDialog confirmDialog = new ConfirmDialog(context);
                                    confirmDialog.setTitle("EXPORT SUCCESS");
                                    confirmDialog.show();

                                }
                            });

                        } catch (IOException e) {
                            Log.e(getClass().getSimpleName(), e.toString());
                        }


                        Looper.loop();
                    }
                };
                thread.start();

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

    //어댑터에서 index 정보 받아와서 아래 함수 호출하면 됨
    private void selectData(int order) {

        if (adapter.getItemCount() == 0) {

            handler.post(new Runnable() {
                @Override
                public void run() {

                    changedListener.onChanged();

                }
            });

            return;
        }

        volumeFlowLayout.removeAllViews();
        volumeTimeLayout.removeAllViews();

        volumeFlowLayout.addView(volumeFlowResultViews.get(order));
        volumeTimeLayout.addView(volumeTimeResultViews.get(order));



    }

    private VolumeTimeGraphView createVolumeTimeGraph(List<Coordinate> coordinates, int width, int height) {
        //Log.e(getClass().getSimpleName(), "WIDTH / HEIGHT : " + width + ", " + height);

        VolumeTimeGraphView volumeTimeResultView = new VolumeTimeGraphView(context);
        volumeTimeResultView.setId(View.generateViewId());
        volumeTimeResultView.setCanvasSize(width, height);
        volumeTimeResultView.setX(1.5f * ((float)width / (float)height), 0f);
        volumeTimeResultView.setY(1f, 0f);
        volumeTimeResultView.setMargin(30,30,60,30);
        volumeTimeResultView.setFinalPath(true);

        volumeTimeResultView.commit();

        for (int i = 0; i < coordinates.size(); i++) {

            Coordinate coordinate = coordinates.get(i);
            volumeTimeResultView.setValue((float)coordinate.getTime(), (float)coordinate.getVolume(), (float)coordinate.getLps());

        }

        return volumeTimeResultView;
    }

    private VolumeFlowGraphView createVolumeFlowGraph(List<Coordinate> coordinates, int width, int height) {
        //Log.e(getClass().getSimpleName(), "WIDTH / HEIGHT : " + width + ", " + height);

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

    private SlowVolumeTimeGraphView createSlowVolumeTimeGraph(List<Coordinate> coordinates, int width, int height) {

        //Log.e(getClass().getSimpleName(), "WIDTH / HEIGHT : " + width + ", " + height);

        SlowVolumeTimeGraphView graphView = new SlowVolumeTimeGraphView(context);
        graphView.setId(View.generateViewId());
        graphView.setCanvasSize(width, height);
        graphView.setX(60f, 0f);
        graphView.setY(0.5f * ((float)height / (float)width), -0.5f * ((float)height / (float)width));
        graphView.setMargin(30,30,60,60);

        graphView.commit();

        for (int i = 0; i < coordinates.size(); i++) {

            Coordinate coordinate = coordinates.get(i);

            graphView.setValue((float)coordinate.getTime(), (float)coordinate.getVolume(), (float)coordinate.getLps());

        }

        return graphView;

    }

    private void startDrawing(int width, int height) {

        adapter.clear();

        Thread thread = new Thread() {

            @Override
            public void run() {
                super.run();
                Looper.prepare();

                SpiroKitDatabase database = SpiroKitDatabase.getInstance(context);

                Patient patient = database.patientDao()
                        .selectPatientByHash(SharedPreferencesManager.getPatientHashed(context));

                List<CalHistoryRawData> rawData = database.calHistoryRawDataDao().selectRawDataByHistory(
                        SharedPreferencesManager.getCalHistoryHash(context)
                );

                CalHistory calHistory = database.calHistoryDao().select(SharedPreferencesManager.getCalHistoryHash(context));
                String version = calHistory.getDeviceDiv();

                for (int i = 0; i < rawData.size(); i++) {

                    String[] data = rawData.get(i).getData().split(" ");

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

                    double fvc = spiroKitHandler.getVC(dataList);
                    double fev1 = spiroKitHandler.getEV1(dataList);
                    double pef = spiroKitHandler.getPEF(dataList);

                    double fvcP = spiroKitHandler.getPredictFVC(
                            patient.getAge(patient.getBirthDay()),
                            patient.getHeight(),
                            patient.getWeight(),
                            patient.getGender()
                    );

                    double fev1P = spiroKitHandler.getPredictFEV1(
                            patient.getAge(patient.getBirthDay()),
                            patient.getHeight(),
                            patient.getWeight(),
                            patient.getGender()
                    );


                    volumeFlowResultViews.add(createVolumeFlowGraph(spiroKitHandler.getValues(dataList), width, height));
                    volumeTimeResultViews.add(createVolumeTimeGraph(spiroKitHandler.getForcedValues(dataList), width, height));
                    slowVolumeTimeGraphViews.add(createSlowVolumeTimeGraph(spiroKitHandler.getValues(dataList), width, height));

                    Log.d(getClass().getSimpleName(), volumeFlowResultViews.size() + ", " + volumeTimeResultViews.size());

                    ResultFVC resultFVC = new ResultFVC(rawData.get(i).getHashed());
                    resultFVC.setFvc(fvc);
                    resultFVC.setFvcPredict(fvcP);

                    resultFVC.setFev1(fev1);
                    resultFVC.setFev1Predict(fev1P);

                    resultFVC.setFev1percent((fev1 / fvc) * 100f);
                    resultFVC.setFev1PercentPredict((fev1P / fvcP) * 100f);

                    resultFVC.setPef(pef);

                    String dateString = rawData.get(i).getCalDate();
                    dateString = dateString.substring(0, dateString.length() - 7);
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

                    try {

                        long time = simpleDateFormat.parse(dateString).getTime();
                        resultFVC.setTimestamp(time);

                    } catch (ParseException e) {
                        resultFVC.setTimestamp(0);
                    }

                    resultFVC.setPost(rawData.get(i).getIsPost());
                    resultFVC.setOrder(rawData.get(i).getOrderNumber());

                    adapter.addFvcResult(resultFVC);
                    Log.e(getClass().getSimpleName(), adapter.getItemCount() + "개 째 처리 중");

                }

                adapter.setSelection(0);

                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        adapter.notifyDataSetChanged();
                        volumeFlowLayout.removeAllViews();
                        volumeTimeLayout.removeAllViews();

                        volumeFlowLayout.addView(volumeFlowResultViews.get(0));
                        volumeTimeLayout.addView(volumeTimeResultViews.get(0));

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

    private List<String> readCsvFile(File file) {

        List<String> list = new ArrayList<>();

        try {

            BufferedReader bufferedReader = new BufferedReader(
                    new FileReader(file)
            );

            String line = "";
            while ((line = bufferedReader.readLine()) != null) {

                list.add(line);

            }

            return list;

        } catch (IOException e) {

        }

        return null;

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