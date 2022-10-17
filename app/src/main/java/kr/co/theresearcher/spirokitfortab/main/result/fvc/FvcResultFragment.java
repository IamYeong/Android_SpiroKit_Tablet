package kr.co.theresearcher.spirokitfortab.main.result.fvc;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
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
import kr.co.theresearcher.spirokitfortab.PdfCreator;
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
                    Paint paint = new Paint();

                    //Size : pt 단위(== 1/72 inch == 1/72 * 2.54 mm)
                    float pageWidth = PdfCreator.getA4Width();
                    float pageHeight = PdfCreator.getA4Height();

                    float accWidth = 0f;
                    float accHeight = 0f;

                    PdfDocument.PageInfo info1 = new PdfDocument.PageInfo.Builder((int)pageWidth, (int)pageHeight, 1).create();
                    PdfDocument.Page page = pdfDocument.startPage(info1);

                    Canvas pageCanvas = page.getCanvas();

                    //그리기 영역

                    Bitmap logoImage = BitmapFactory.decodeResource(getResources(), R.drawable.tr_logo); // 이미지 비트맵화
                    Bitmap fitImage = Bitmap.createScaledBitmap(logoImage, 50, 40, false); // 사이즈 조정

                    float leftMargin = 20f;
                    float topMargin = 20f;
                    float rightMargin = 20f;
                    float bottomMargin = 20f;

                    accWidth += leftMargin;
                    accHeight += topMargin;

                    paint.setAntiAlias(true);

                    pageCanvas.drawBitmap(fitImage, leftMargin, topMargin, paint); // 이미지 그리기
                    accWidth += 40f;
                    accHeight += 30f;

                    paint.setColor(Color.BLACK); // line color
                    paint.setTextSize(10f);
                    paint.setTypeface(Typeface.DEFAULT);


                    //pageCanvas.drawLine(20f,20f, 20f, pageHeight - 20f, paint);
                    //pageCanvas.drawLine(20f, 20f, pageWidth - 20f, 20f, paint);
                    //pageCanvas.drawLine(pageWidth - 20f, 20f, pageWidth - 20f, pageHeight - 20f, paint);
                    //pageCanvas.drawLine(20f, pageHeight - 20f, pageWidth - 20f, pageHeight - 20f, paint);

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    long printDate = Calendar.getInstance().getTime().getTime();
                    pageCanvas.drawText(rawData.getCalDate().substring(0, 19), pageWidth - rightMargin - 100f, topMargin + 10f, paint);
                    pageCanvas.drawText(simpleDateFormat.format(printDate), pageWidth - rightMargin - 100f, topMargin + 22f, paint);

                    accHeight += 10f;
                    //가로줄
                    pageCanvas.drawLine(leftMargin, accHeight, pageWidth - rightMargin, accHeight, paint);
                    pageCanvas.drawLine(leftMargin, accHeight + 20f, pageWidth - rightMargin, accHeight + 20f, paint);
                    pageCanvas.drawLine(leftMargin, accHeight + 40f, pageWidth - rightMargin, accHeight + 40f, paint);
                    pageCanvas.drawLine(leftMargin, accHeight + 60f, pageWidth - rightMargin, accHeight + 60f, paint);
                    pageCanvas.drawLine(leftMargin, accHeight + 80f, pageWidth - rightMargin, accHeight + 80f, paint);
                    pageCanvas.drawLine(leftMargin, accHeight + 100f, pageWidth - rightMargin, accHeight + 100f, paint);

                    //세로줄
                    float horizontalGap = (pageWidth - leftMargin - rightMargin) / 4f;

                    pageCanvas.drawLine(leftMargin, accHeight, leftMargin, accHeight + 100f, paint);
                    pageCanvas.drawLine(leftMargin + (horizontalGap), accHeight, leftMargin + (horizontalGap), accHeight + 100f, paint);
                    pageCanvas.drawLine(leftMargin + (horizontalGap * 2f), accHeight, leftMargin + (horizontalGap * 2f), accHeight + 100f, paint);
                    pageCanvas.drawLine(leftMargin + (horizontalGap * 3f), accHeight, leftMargin + (horizontalGap * 3f), accHeight + 100f, paint);
                    pageCanvas.drawLine(leftMargin + (horizontalGap * 4f), accHeight, leftMargin + (horizontalGap * 4f), accHeight + 100f, paint);

                    //표 안의 내용
                    pageCanvas.drawText("Name", leftMargin + 5f, accHeight + 20f - 5f, paint);
                    pageCanvas.drawText("Age", leftMargin + 5f, accHeight + 40f - 5f, paint);
                    pageCanvas.drawText("Gender", leftMargin + 5f, accHeight + 60f - 5f, paint);
                    pageCanvas.drawText("Height", leftMargin + 5f, accHeight + 80f - 5f, paint);
                    pageCanvas.drawText("Weight", leftMargin + 5f, accHeight + 100f - 5f, paint);

                    pageCanvas.drawText(patient.getName(), leftMargin + (horizontalGap) + 5f, accHeight + 20f - 5f, paint);
                    pageCanvas.drawText("Age", leftMargin + (horizontalGap) + 5f, accHeight + 40f - 5f, paint);
                    pageCanvas.drawText("Gender", leftMargin + (horizontalGap) + 5f, accHeight + 60f - 5f, paint);
                    pageCanvas.drawText(patient.getHeight() + " cm", leftMargin + (horizontalGap) + 5f, accHeight + 80f - 5f, paint);
                    pageCanvas.drawText(patient.getWeight() + " kg", leftMargin + (horizontalGap) + 5f, accHeight + 100f - 5f, paint);

                    pageCanvas.drawText("Smoking", leftMargin + (horizontalGap * 2f) + 5f, accHeight + 20f - 5f, paint);
                    pageCanvas.drawText("Not smoking", leftMargin + (horizontalGap * 2f) + 5f, accHeight + 40f - 5f, paint);
                    pageCanvas.drawText("Smoke date", leftMargin + (horizontalGap * 2f) + 5f, accHeight + 60f - 5f, paint);
                    pageCanvas.drawText("Not smoke date", leftMargin + (horizontalGap * 2f) + 5f, accHeight + 80f - 5f, paint);
                    pageCanvas.drawText("Smoke period", leftMargin + (horizontalGap * 2f) + 5f, accHeight + 100f - 5f, paint);

                    pageCanvas.drawText("Name", leftMargin + (horizontalGap * 3f) + 5f, accHeight + 20f - 5f, paint);
                    pageCanvas.drawText("Age", leftMargin + (horizontalGap * 3f) + 5f, accHeight + 40f - 5f, paint);
                    pageCanvas.drawText("Gender", leftMargin + (horizontalGap * 3f) + 5f, accHeight + 60f - 5f, paint);
                    pageCanvas.drawText("Height", leftMargin + (horizontalGap * 3f) + 5f, accHeight + 80f - 5f, paint);
                    pageCanvas.drawText("Weight", leftMargin + (horizontalGap * 3f) + 5f, accHeight + 100f - 5f, paint);

                    accHeight += 100f;

                    //타이틀박스
                    paint.setColor(0xFFECECEC);
                    pageCanvas.drawRect(leftMargin, accHeight + 20f, pageWidth - rightMargin, accHeight + 40f, paint);

                    paint.setColor(Color.BLACK);
                    paint.setTextAlign(Paint.Align.CENTER);
                    pageCanvas.drawText("Forced Vital Capacity(FVC)", pageWidth / 2f, accHeight + 40f - 5f, paint);

                    if (rawData.getIsPost() == 1) {
                        pageCanvas.drawText("POST", pageWidth - rightMargin - 50f, accHeight + 40f - 5f, paint);
                    } else {
                        pageCanvas.drawText("PRE", pageWidth - rightMargin - 50f, accHeight + 40f - 5f, paint);
                    }

                    accHeight += 40f;


                    //그래프 영역
                    paint.setColor(Color.WHITE);
                    pageCanvas.drawRect(pageWidth / 2f, accHeight + 40f, pageWidth - rightMargin, accHeight + 20f + 200f, paint);
                    pageCanvas.drawRect(leftMargin, accHeight + 20f + 200f + 20f, pageWidth - rightMargin, accHeight + 20f + 200f + 20f + 200f, paint);

                    //그래프 넣기
                    SpiroKitHandler spiroKitHandler = new DataHandlerE();
                    List<Integer> data = spiroKitHandler.convertAll(rawData.getData());
                    List<Coordinate> values = spiroKitHandler.getValues(spiroKitHandler.convertAll(rawData.getData()));
                    VolumeFlowGraphView volumeFlowGraphView = createVolumeFlowGraphPrint(values, (int)((pageWidth / 2f) - leftMargin - rightMargin), 200);
                    SlowVolumeTimeGraphView volumeTimeGraphView = createSlowVolumeTimeGraphPrint(values, (int)(pageWidth - leftMargin - rightMargin), 200);
                    //VolumeFlowGraphView volumeFlowGraphView = volumeFlowResultViews.get(adapter.getSelectedOrdinal());
                    //SlowVolumeTimeGraphView volumeTimeGraphView = slowVolumeTimeGraphViews.get(adapter.getSelectedOrdinal());

                    //비트맵 영역 잡아주기
                    Bitmap vf = Bitmap.createBitmap((int)volumeFlowGraphView.getCanvasWidth(), (int)volumeFlowGraphView.getCanvasHeight(), Bitmap.Config.ARGB_8888);
                    Bitmap vt = Bitmap.createBitmap((int)volumeTimeGraphView.getCanvasWidth(), (int)volumeTimeGraphView.getCanvasHeight(), Bitmap.Config.ARGB_8888);

                    Canvas viewCanvas = new Canvas(vf);
                    volumeFlowGraphView.draw(viewCanvas);

                    viewCanvas = new Canvas(vt);
                    volumeTimeGraphView.draw(viewCanvas);

                    //Bitmap scaledVF = Bitmap.createScaledBitmap(vf, (int)((pageWidth / 2f) - leftMargin - rightMargin), 200, false);
                    //Bitmap scaledVT = Bitmap.createScaledBitmap(vt, (int)(pageWidth - leftMargin - rightMargin), 200, false);

                    pageCanvas.drawBitmap(vf, (pageWidth / 2f) + 20f, accHeight + 40f, paint);
                    pageCanvas.drawBitmap(vt, leftMargin, accHeight + 20f + 200f + 20f, paint);

                    //결과값 넣기
                    double fvc = spiroKitHandler.getVC(data);
                    double fev1 = spiroKitHandler.getEV1(data);
                    double fev1per = (fev1 / fvc) * 100d;
                    double pef = spiroKitHandler.getPEF(data);
                    double fef = spiroKitHandler.getFEF_25to75(data);
                    double mef25 = spiroKitHandler.getMEF25(data);
                    double mef50 = spiroKitHandler.getMEF50(data);
                    double mef75 = spiroKitHandler.getMEF75(data);

                    double fvcPred = spiroKitHandler.getPredictFVC(20, patient.getHeight(), patient.getWeight(), patient.getGender());
                    double fev1Pred = spiroKitHandler.getPredictFEV1(20, patient.getHeight(), patient.getWeight(), patient.getGender());
                    double pefPred = 10d;

                    //DIV Column
                    paint.setTextAlign(Paint.Align.LEFT);
                    paint.setColor(Color.BLACK);
                    pageCanvas.drawText("DIV", leftMargin, accHeight + 40f,paint);
                    pageCanvas.drawLine(leftMargin, accHeight + 45f, pageWidth / 2f, accHeight + 45f, paint);
                    pageCanvas.drawText("FVC", leftMargin, accHeight + 70f, paint);
                    pageCanvas.drawText("FEV1", leftMargin, accHeight + 90f, paint);
                    pageCanvas.drawText("FEV1%", leftMargin, accHeight + 110f, paint);
                    pageCanvas.drawText("PEF", leftMargin, accHeight + 130f, paint);
                    pageCanvas.drawText("FEF25-75%", leftMargin, accHeight + 150f, paint);
                    pageCanvas.drawText("MEF25%", leftMargin, accHeight + 170f, paint);
                    pageCanvas.drawText("MEF50%", leftMargin, accHeight + 190f, paint);
                    pageCanvas.drawText("MEF75%", leftMargin, accHeight + 210f, paint);

                    //UNIT Column
                    paint.setTextAlign(Paint.Align.CENTER);
                    pageCanvas.drawText("UNIT", leftMargin + 80f, accHeight + 40f,paint);
                    //pageCanvas.drawLine(leftMargin, accHeight + 45f, pageWidth - rightMargin, accHeight + 45f, paint);
                    pageCanvas.drawText("L", leftMargin + 80f, accHeight + 70f, paint);
                    pageCanvas.drawText("L", leftMargin + 80f, accHeight + 90f, paint);
                    pageCanvas.drawText("%", leftMargin + 80f, accHeight + 110f, paint);
                    pageCanvas.drawText("L/s", leftMargin + 80f, accHeight + 130f, paint);
                    pageCanvas.drawText("L/s", leftMargin + 80f, accHeight + 150f, paint);
                    pageCanvas.drawText("L/s", leftMargin + 80f, accHeight + 170f, paint);
                    pageCanvas.drawText("L/s", leftMargin + 80f, accHeight + 190f, paint);
                    pageCanvas.drawText("L/s", leftMargin + 80f, accHeight + 210f, paint);

                    //MEAS Column
                    pageCanvas.drawText("MEAS", leftMargin + 130f, accHeight + 40f,paint);
                    //pageCanvas.drawLine(leftMargin, accHeight + 45f, pageWidth - rightMargin, accHeight + 45f, paint);
                    pageCanvas.drawText(getString(R.string.round_2, fvc), leftMargin + 130f, accHeight + 70f, paint);
                    pageCanvas.drawText(getString(R.string.round_2, fev1), leftMargin + 130f, accHeight + 90f, paint);
                    pageCanvas.drawText(getString(R.string.round_2, fev1per), leftMargin + 130f, accHeight + 110f, paint);
                    pageCanvas.drawText(getString(R.string.round_2, pef), leftMargin + 130f, accHeight + 130f, paint);
                    pageCanvas.drawText(getString(R.string.round_2, fef), leftMargin + 130f, accHeight + 150f, paint);
                    pageCanvas.drawText(getString(R.string.round_2, mef25), leftMargin + 130f, accHeight + 170f, paint);
                    pageCanvas.drawText(getString(R.string.round_2, mef50), leftMargin + 130f, accHeight + 190f, paint);
                    pageCanvas.drawText(getString(R.string.round_2, mef75), leftMargin + 130f, accHeight + 210f, paint);

                    //PRED Column
                    pageCanvas.drawText("PRED", leftMargin + 180f, accHeight + 40f,paint);
                    //pageCanvas.drawLine(leftMargin, accHeight + 45f, pageWidth - rightMargin, accHeight + 45f, paint);
                    pageCanvas.drawText(getString(R.string.round_2, fvcPred), leftMargin + 180f, accHeight + 70f, paint);
                    pageCanvas.drawText(getString(R.string.round_2, fev1Pred), leftMargin + 180f, accHeight + 90f, paint);
                    pageCanvas.drawText("-", leftMargin + 180f, accHeight + 110f, paint);
                    pageCanvas.drawText("-", leftMargin + 180f, accHeight + 130f, paint);
                    pageCanvas.drawText("-", leftMargin + 180f, accHeight + 150f, paint);
                    pageCanvas.drawText("-", leftMargin + 180f, accHeight + 170f, paint);
                    pageCanvas.drawText("-", leftMargin + 180f, accHeight + 190f, paint);
                    pageCanvas.drawText("-", leftMargin + 180f, accHeight + 210f, paint);

                    //Percentage Column
                    pageCanvas.drawText("PERCENT", leftMargin + 230f, accHeight + 40f,paint);
                    //pageCanvas.drawLine(leftMargin, accHeight + 45f, pageWidth - rightMargin, accHeight + 45f, paint);
                    pageCanvas.drawText(getString(R.string.round_2, fvc / fvcPred), leftMargin + 230f, accHeight + 70f, paint);
                    pageCanvas.drawText(getString(R.string.round_2, fev1 / fev1Pred), leftMargin + 230f, accHeight + 90f, paint);
                    pageCanvas.drawText("-", leftMargin + 230f, accHeight + 110f, paint);
                    pageCanvas.drawText("-", leftMargin + 230f, accHeight + 130f, paint);
                    pageCanvas.drawText("-", leftMargin + 230f, accHeight + 150f, paint);
                    pageCanvas.drawText("-", leftMargin + 230f, accHeight + 170f, paint);
                    pageCanvas.drawText("-", leftMargin + 230f, accHeight + 190f, paint);
                    pageCanvas.drawText("-", leftMargin + 230f, accHeight + 210f, paint);

                    //그리기 영역

                    pdfDocument.finishPage(page);

                    File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                            Calendar.getInstance().getTime().getTime() + "_result.pdf");

                    pdfDocument.writeTo(new FileOutputStream(file));

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
            volumeTimeResultView.setValue((float)coordinate.getTime(), (float)coordinate.getLps(), (float)coordinate.getVolume());

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

        volumeFlowResultView.commit();

        for (int i = 0; i < coordinates.size(); i++) {

            Coordinate coordinate = coordinates.get(i);
            volumeFlowResultView.setValue((float)coordinate.getTime(), (float)coordinate.getLps(), (float)coordinate.getVolume());

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
        graphView.setMargin(30,30,60,30);

        graphView.commit();

        for (int i = 0; i < coordinates.size(); i++) {

            Coordinate coordinate = coordinates.get(i);

            graphView.setValue((float)coordinate.getTime(), (float)coordinate.getLps(), (float)coordinate.getVolume());

        }

        return graphView;

    }

    private VolumeFlowGraphView createVolumeFlowGraphPrint(List<Coordinate> coordinates, int width, int height) {
        //Log.e(getClass().getSimpleName(), "WIDTH / HEIGHT : " + width + ", " + height);

        VolumeFlowGraphView volumeFlowResultView = new VolumeFlowGraphView(context);
        volumeFlowResultView.setId(View.generateViewId());
        volumeFlowResultView.setCanvasSize(width, height);
        volumeFlowResultView.setX(1.6f * (((float)width / (float)height)), 0f);
        volumeFlowResultView.setY(1.4f, -0.8f);
        volumeFlowResultView.setGraphWidth(1f);
        volumeFlowResultView.setLineWidth(0.5f);
        volumeFlowResultView.setLinesColor(0xFF383838);
        volumeFlowResultView.setGraphColor(0xFFFF3333);
        volumeFlowResultView.setLabelColor(0xFF383838);

        volumeFlowResultView.commit();

        for (int i = 0; i < coordinates.size(); i++) {

            Coordinate coordinate = coordinates.get(i);
            volumeFlowResultView.setValue((float)coordinate.getTime(), (float)coordinate.getLps(), (float)coordinate.getVolume());

        }

        return volumeFlowResultView;

    }

    private SlowVolumeTimeGraphView createSlowVolumeTimeGraphPrint(List<Coordinate> coordinates, int width, int height) {

        //Log.e(getClass().getSimpleName(), "WIDTH / HEIGHT : " + width + ", " + height);

        SlowVolumeTimeGraphView graphView = new SlowVolumeTimeGraphView(context);
        graphView.setId(View.generateViewId());
        graphView.setCanvasSize(width, height);
        graphView.setX(60f, 0f);
        graphView.setY(0.5f * ((float)height / (float)width), -0.5f * ((float)height / (float)width));
        graphView.setGraphWidth(1f);
        graphView.setLineWidth(0.5f);
        graphView.setLinesColor(0xFF383838);
        graphView.setGraphColor(0xFFFF3333);
        graphView.setLabelColor(0xFF383838);

        graphView.commit();

        for (int i = 0; i < coordinates.size(); i++) {

            Coordinate coordinate = coordinates.get(i);

            graphView.setValue((float)coordinate.getTime(), (float)coordinate.getLps(), (float)coordinate.getVolume());

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