package kr.co.theresearcher.spirokitfortab.main.result.pdf;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.appcompat.content.res.AppCompatResources;

import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import kr.co.theresearcher.spirokitfortab.R;
import kr.co.theresearcher.spirokitfortab.calc.DataHandlerE;
import kr.co.theresearcher.spirokitfortab.calc.SpiroKitHandler;
import kr.co.theresearcher.spirokitfortab.db.cal_history_raw_data.CalHistoryRawData;
import kr.co.theresearcher.spirokitfortab.db.patient.Patient;
import kr.co.theresearcher.spirokitfortab.graph.Coordinate;
import kr.co.theresearcher.spirokitfortab.graph.SlowVolumeTimeGraphView;
import kr.co.theresearcher.spirokitfortab.graph.VolumeFlowGraphView;

public class FvcPdfCreator implements PdfCreator {

    private Context context;
    private Paint paint;
    private Path path;
    private List<Coordinate> values;
    private Patient patient;
    private SpiroKitHandler spiroKitHandler;
    private CalHistoryRawData rawData;

    private float outLineLength;

    private float leftMargin = 20f;
    private float topMargin = 20;
    private float rightMargin = 20;
    private float bottomMargin = 20;

    private float A4_WIDTH = 545f; //pt
    private float A4_HEIGHT = 841f; // pt

    public FvcPdfCreator(Context context,Patient patient, CalHistoryRawData rawData) {

        this.context = context;
        this.rawData = rawData;
        this.patient = patient;
        paint = new Paint();
        path = new Path();

        spiroKitHandler = new DataHandlerE();
        List<Integer> data = spiroKitHandler.convertAll(rawData.getData());
        values = new ArrayList<>();
        values.addAll(spiroKitHandler.getValues(data));

    }

    @Override
    public Canvas drawPDF(Context context, Canvas canvas) {

        SpiroKitHandler spiroKitHandler = new DataHandlerE();
        List<Integer> data = spiroKitHandler.convertAll(rawData.getData());
        List<Coordinate> values = spiroKitHandler.getValues(spiroKitHandler.convertAll(rawData.getData()));

        float pageWidth = canvas.getWidth();
        float pageHeight = canvas.getHeight();

        float accWidth = 0f;
        float accHeight = 0f;

        accWidth += leftMargin;
        accHeight += topMargin;

        Drawable logoDrawable = AppCompatResources.getDrawable(context, R.drawable.tr_logo);
        if (logoDrawable == null) Log.e(getClass().getSimpleName(), "DRAWABLE NULL");
        logoDrawable.setBounds((int)leftMargin - 5, (int)topMargin - 10, (int)(leftMargin + 50f), (int)(topMargin + 30f));
        logoDrawable.draw(canvas);

        //canvas.drawBitmap(fitImage, leftMargin, topMargin, paint); // 이미지 그리기
        accWidth += 40f;
        accHeight += 30f;

        paint.setColor(Color.BLACK); // line color
        paint.setTextSize(10f);
        paint.setTypeface(Typeface.DEFAULT);


        //canvas.drawLine(20f,20f, 20f, pageHeight - 20f, paint);
        //canvas.drawLine(20f, 20f, pageWidth - 20f, 20f, paint);
        //canvas.drawLine(pageWidth - 20f, 20f, pageWidth - 20f, pageHeight - 20f, paint);
        //canvas.drawLine(20f, pageHeight - 20f, pageWidth - 20f, pageHeight - 20f, paint);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        long printDate = Calendar.getInstance().getTime().getTime();
        canvas.drawText(context.getString(R.string.pdf_meas_at) + " " + rawData.getCalDate().substring(0, 19), pageWidth - rightMargin - 150f, topMargin + 10f, paint);
        canvas.drawText(context.getString(R.string.pdf_print_at) + " " + simpleDateFormat.format(printDate), pageWidth - rightMargin - 150f, topMargin + 22f, paint);

        accHeight += 10f;
        //가로줄
        canvas.drawLine(leftMargin, accHeight, pageWidth - rightMargin, accHeight, paint);
        canvas.drawLine(leftMargin, accHeight + 20f, pageWidth - rightMargin, accHeight + 20f, paint);
        canvas.drawLine(leftMargin, accHeight + 40f, pageWidth - rightMargin, accHeight + 40f, paint);
        canvas.drawLine(leftMargin, accHeight + 60f, pageWidth - rightMargin, accHeight + 60f, paint);
        canvas.drawLine(leftMargin, accHeight + 80f, pageWidth - rightMargin, accHeight + 80f, paint);
        canvas.drawLine(leftMargin, accHeight + 100f, pageWidth - rightMargin, accHeight + 100f, paint);

        //세로줄
        float horizontalGap = (pageWidth - leftMargin - rightMargin) / 4f;

        canvas.drawLine(leftMargin, accHeight, leftMargin, accHeight + 100f, paint);
        canvas.drawLine(leftMargin + (horizontalGap), accHeight, leftMargin + (horizontalGap), accHeight + 100f, paint);
        canvas.drawLine(leftMargin + (horizontalGap * 2f), accHeight, leftMargin + (horizontalGap * 2f), accHeight + 100f, paint);
        canvas.drawLine(leftMargin + (horizontalGap * 3f), accHeight, leftMargin + (horizontalGap * 3f), accHeight + 100f, paint);
        canvas.drawLine(leftMargin + (horizontalGap * 4f), accHeight, leftMargin + (horizontalGap * 4f), accHeight + 100f, paint);

        //표 안의 내용
        canvas.drawText(context.getString(R.string.pdf_name), leftMargin + 5f, accHeight + 20f - 5f, paint);
        canvas.drawText(context.getString(R.string.pdf_age), leftMargin + 5f, accHeight + 40f - 5f, paint);
        canvas.drawText(context.getString(R.string.pdf_gender), leftMargin + 5f, accHeight + 60f - 5f, paint);
        canvas.drawText(context.getString(R.string.pdf_height), leftMargin + 5f, accHeight + 80f - 5f, paint);
        canvas.drawText(context.getString(R.string.pdf_weight), leftMargin + 5f, accHeight + 100f - 5f, paint);

        canvas.drawText(patient.getName(), leftMargin + (horizontalGap) + 5f, accHeight + 20f - 5f, paint);
        canvas.drawText(patient.getAge(patient.getBirthDay()) + "", leftMargin + (horizontalGap) + 5f, accHeight + 40f - 5f, paint);
        if (patient.getGender().equals("m")) canvas.drawText(context.getString(R.string.pdf_male), leftMargin + (horizontalGap) + 5f, accHeight + 60f - 5f, paint);
        else if (patient.getGender().equals("f")) canvas.drawText(context.getString(R.string.pdf_female), leftMargin + (horizontalGap) + 5f, accHeight + 60f - 5f, paint);
        else canvas.drawText(context.getString(R.string.not_applicable), leftMargin + (horizontalGap) + 5f, accHeight + 60f - 5f, paint);
        canvas.drawText(patient.getHeight() + " cm", leftMargin + (horizontalGap) + 5f, accHeight + 80f - 5f, paint);
        canvas.drawText(patient.getWeight() + " kg", leftMargin + (horizontalGap) + 5f, accHeight + 100f - 5f, paint);

        canvas.drawText(context.getString(R.string.pdf_smoking), leftMargin + (horizontalGap * 2f) + 5f, accHeight + 20f - 5f, paint);
        canvas.drawText(context.getString(R.string.pdf_no_smoking), leftMargin + (horizontalGap * 2f) + 5f, accHeight + 40f - 5f, paint);
        canvas.drawText(context.getString(R.string.pdf_smoking_at), leftMargin + (horizontalGap * 2f) + 5f, accHeight + 60f - 5f, paint);
        canvas.drawText(context.getString(R.string.pdf_no_smoking_at), leftMargin + (horizontalGap * 2f) + 5f, accHeight + 80f - 5f, paint);
        canvas.drawText(context.getString(R.string.pdf_smoking_period), leftMargin + (horizontalGap * 2f) + 5f, accHeight + 100f - 5f, paint);

        //피운적 없으니 끊은적도 없음
        if (patient.getStartSmokingDay() == null) {
            canvas.drawText(context.getString(R.string.not_applicable), leftMargin + (horizontalGap * 3f) + 5f, accHeight + 20f - 5f, paint);
            canvas.drawText(context.getString(R.string.not_applicable), leftMargin + (horizontalGap * 3f) + 5f, accHeight + 40f - 5f, paint);
            canvas.drawText(context.getString(R.string.not_applicable), leftMargin + (horizontalGap * 3f) + 5f, accHeight + 60f - 5f, paint);
            canvas.drawText(context.getString(R.string.not_applicable), leftMargin + (horizontalGap * 3f) + 5f, accHeight + 80f - 5f, paint);
            canvas.drawText(context.getString(R.string.not_applicable), leftMargin + (horizontalGap * 3f) + 5f, accHeight + 100f - 5f, paint);
        } else {
            //피운적있으니 금연여부만 보면 됨

            if (patient.getStopSmokingDay() == null) {
                canvas.drawText(context.getString(R.string.pdf_yes), leftMargin + (horizontalGap * 3f) + 5f, accHeight + 20f - 5f, paint);
                canvas.drawText(context.getString(R.string.pdf_no), leftMargin + (horizontalGap * 3f) + 5f, accHeight + 40f - 5f, paint);
                canvas.drawText(patient.getStartSmokingDay(), leftMargin + (horizontalGap * 3f) + 5f, accHeight + 60f - 5f, paint);
                canvas.drawText(context.getString(R.string.not_applicable), leftMargin + (horizontalGap * 3f) + 5f, accHeight + 80f - 5f, paint);
                canvas.drawText(context.getString(R.string.not_applicable), leftMargin + (horizontalGap * 3f) + 5f, accHeight + 100f - 5f, paint);
            } else {

                try {

                    long from = Calendar.getInstance().getTime().getTime();
                    long to = simpleDateFormat.parse(patient.getStopSmokingDay()).getTime();

                    canvas.drawText(context.getString(R.string.pdf_no), leftMargin + (horizontalGap * 3f) + 5f, accHeight + 20f - 5f, paint);
                    canvas.drawText(context.getString(R.string.pdf_yes), leftMargin + (horizontalGap * 3f) + 5f, accHeight + 40f - 5f, paint);
                    canvas.drawText(patient.getStartSmokingDay(), leftMargin + (horizontalGap * 3f) + 5f, accHeight + 60f - 5f, paint);
                    canvas.drawText(patient.getStopSmokingDay(), leftMargin + (horizontalGap * 3f) + 5f, accHeight + 80f - 5f, paint);
                    canvas.drawText( dateDiff(from, to) + " " + context.getString(R.string.pdf_months), leftMargin + (horizontalGap * 3f) + 5f, accHeight + 100f - 5f, paint);

                } catch (ParseException e) {
                    Log.e(getClass().getSimpleName(), "PARSE EXCEPTION");
                }


            }

        }



        accHeight += 100f;

        //타이틀박스
        paint.setColor(0xFFECECEC);
        canvas.drawRect(leftMargin, accHeight + 20f, pageWidth - rightMargin, accHeight + 40f, paint);

        paint.setColor(Color.BLACK);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(context.getString(R.string.pdf_fvc_title), pageWidth / 2f, accHeight + 40f - 5f, paint);

        if (rawData.getIsPost() == 1) {
            canvas.drawText(context.getString(R.string.pdf_post), pageWidth - rightMargin - 50f, accHeight + 40f - 5f, paint);
        } else {
            canvas.drawText(context.getString(R.string.pdf_pre), pageWidth - rightMargin - 50f, accHeight + 40f - 5f, paint);
        }

        accHeight += 40f;


        //그래프 영역
        //canvas.drawRect(pageWidth / 2f, accHeight + 40f, pageWidth - rightMargin, accHeight + 20f + 200f, paint);
        //canvas.drawRect(leftMargin, accHeight + 20f + 200f + 20f, pageWidth - rightMargin, accHeight + 20f + 200f + 20f + 200f, paint);

        //결과값 넣기
        double fvc = spiroKitHandler.getVC(data);
        double fev1 = spiroKitHandler.getEV1(data);
        double fev1per = (fev1 / fvc) * 100d;
        double pef = spiroKitHandler.getPEF(data);
        double fef = spiroKitHandler.getFEF_25to75(data);
        double mef25 = spiroKitHandler.getMEF25(data);
        double mef50 = spiroKitHandler.getMEF50(data);
        double mef75 = spiroKitHandler.getMEF75(data);

        int age = patient.getAge(patient.getBirthDay());
        double fvcPred = spiroKitHandler.getPredictFVC(age, patient.getHeight(), patient.getWeight(), patient.getGender());
        double fev1Pred = spiroKitHandler.getPredictFEV1(age, patient.getHeight(), patient.getWeight(), patient.getGender());
        double fev1perPred = (fev1Pred / fvcPred) * 100d;
        double pefPred = spiroKitHandler.getPredictPEF(age, patient.getHeight(), patient.getWeight(), patient.getGender(), "");

        //DIV Column
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setColor(Color.BLACK);
        canvas.drawText(context.getString(R.string.pdf_div), leftMargin, accHeight + 40f,paint);
        canvas.drawLine(leftMargin, accHeight + 45f, pageWidth / 2f, accHeight + 45f, paint);
        canvas.drawText(context.getString(R.string.fvc), leftMargin, accHeight + 70f, paint);
        canvas.drawText(context.getString(R.string.fev1), leftMargin, accHeight + 90f, paint);
        canvas.drawText(context.getString(R.string.fev1per), leftMargin, accHeight + 110f, paint);
        canvas.drawText(context.getString(R.string.pef), leftMargin, accHeight + 130f, paint);
        canvas.drawText(context.getString(R.string.fef2575), leftMargin, accHeight + 150f, paint);
        canvas.drawText(context.getString(R.string.mef25), leftMargin, accHeight + 170f, paint);
        canvas.drawText(context.getString(R.string.mef50), leftMargin, accHeight + 190f, paint);
        canvas.drawText(context.getString(R.string.mef75), leftMargin, accHeight + 210f, paint);

        //UNIT Column
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(context.getString(R.string.pdf_unit), leftMargin + 80f, accHeight + 40f,paint);
        //canvas.drawLine(leftMargin, accHeight + 45f, pageWidth - rightMargin, accHeight + 45f, paint);
        canvas.drawText(context.getString(R.string.liter), leftMargin + 80f, accHeight + 70f, paint);
        canvas.drawText(context.getString(R.string.liter), leftMargin + 80f, accHeight + 90f, paint);
        canvas.drawText(context.getString(R.string.percentage), leftMargin + 80f, accHeight + 110f, paint);
        canvas.drawText(context.getString(R.string.liter_per_sec), leftMargin + 80f, accHeight + 130f, paint);
        canvas.drawText(context.getString(R.string.liter_per_sec), leftMargin + 80f, accHeight + 150f, paint);
        canvas.drawText(context.getString(R.string.liter_per_sec), leftMargin + 80f, accHeight + 170f, paint);
        canvas.drawText(context.getString(R.string.liter_per_sec), leftMargin + 80f, accHeight + 190f, paint);
        canvas.drawText(context.getString(R.string.liter_per_sec), leftMargin + 80f, accHeight + 210f, paint);

        //MEAS Column
        canvas.drawText(context.getString(R.string.pdf_meas), leftMargin + 130f, accHeight + 40f,paint);
        //canvas.drawLine(leftMargin, accHeight + 45f, pageWidth - rightMargin, accHeight + 45f, paint);
        canvas.drawText(context.getString(R.string.round_2, fvc), leftMargin + 130f, accHeight + 70f, paint);
        canvas.drawText(context.getString(R.string.round_2, fev1), leftMargin + 130f, accHeight + 90f, paint);
        canvas.drawText(context.getString(R.string.round_2, fev1per), leftMargin + 130f, accHeight + 110f, paint);
        canvas.drawText(context.getString(R.string.round_2, pef), leftMargin + 130f, accHeight + 130f, paint);
        canvas.drawText(context.getString(R.string.round_2, fef), leftMargin + 130f, accHeight + 150f, paint);
        canvas.drawText(context.getString(R.string.round_2, mef25), leftMargin + 130f, accHeight + 170f, paint);
        canvas.drawText(context.getString(R.string.round_2, mef50), leftMargin + 130f, accHeight + 190f, paint);
        canvas.drawText(context.getString(R.string.round_2, mef75), leftMargin + 130f, accHeight + 210f, paint);

        //PRED Column
        canvas.drawText(context.getString(R.string.pdf_pred), leftMargin + 180f, accHeight + 40f,paint);
        //canvas.drawLine(leftMargin, accHeight + 45f, pageWidth - rightMargin, accHeight + 45f, paint);
        canvas.drawText(context.getString(R.string.round_2, fvcPred), leftMargin + 180f, accHeight + 70f, paint);
        canvas.drawText(context.getString(R.string.round_2, fev1Pred), leftMargin + 180f, accHeight + 90f, paint);
        canvas.drawText(context.getString(R.string.round_2, fev1perPred), leftMargin + 180f, accHeight + 110f, paint);
        canvas.drawText(context.getString(R.string.round_2, pefPred), leftMargin + 180f, accHeight + 130f, paint);
        canvas.drawText("-", leftMargin + 180f, accHeight + 150f, paint);
        canvas.drawText("-", leftMargin + 180f, accHeight + 170f, paint);
        canvas.drawText("-", leftMargin + 180f, accHeight + 190f, paint);
        canvas.drawText("-", leftMargin + 180f, accHeight + 210f, paint);

        //Percentage Column
        canvas.drawText(context.getString(R.string.pdf_percentage), leftMargin + 230f, accHeight + 40f,paint);
        //canvas.drawLine(leftMargin, accHeight + 45f, pageWidth - rightMargin, accHeight + 45f, paint);
        canvas.drawText(context.getString(R.string.round_2, (fvc / fvcPred) * 100d), leftMargin + 230f, accHeight + 70f, paint);
        canvas.drawText(context.getString(R.string.round_2, (fev1 / fev1Pred) * 100d), leftMargin + 230f, accHeight + 90f, paint);
        canvas.drawText(context.getString(R.string.round_2, (fev1per / fev1perPred) * 100d), leftMargin + 230f, accHeight + 110f, paint);
        canvas.drawText(context.getString(R.string.round_2, (pef / pefPred) * 100d), leftMargin + 230f, accHeight + 130f, paint);
        canvas.drawText("-", leftMargin + 230f, accHeight + 150f, paint);
        canvas.drawText("-", leftMargin + 230f, accHeight + 170f, paint);
        canvas.drawText("-", leftMargin + 230f, accHeight + 190f, paint);
        canvas.drawText("-", leftMargin + 230f, accHeight + 210f, paint);

        //graph

        accHeight += 30f;
        drawArea(canvas, pageWidth / 2f, accHeight, pageWidth - rightMargin, accHeight + 200f);
        accHeight += 220f;

        drawAreaSVC(canvas, leftMargin, accHeight, pageWidth - rightMargin, accHeight + 200f);
        accHeight += 220f;

        return null;
    }

    @Override
    public float measureGap(float unit, int limit, float max, float min) {

        float newGap = unit;
        int count = 0;

        while (true) {

            count = (int)((max - min) / newGap);
            if (count > limit) {
                newGap += unit;
            } else {
                break;

            }

        }

        return newGap;
    }

    @Override
    public float gapToSizeX(float width,  float max, float min,float gap) {

        return width * (gap / (max - min));
    }

    @Override
    public float gapToSizeY(float height, float max, float min, float gap) {
        return height * ((max - min) / gap);
    }

    @Override
    public float xToPosition(float width, float max, float min, float x) {

        float result = width * ( (x) / (max - min) );
        //Log.e(getClass().getSimpleName(), "\nX : " + result);
        return result;
    }

    @Override
    public float yToPosition(float height, float max, float min, float y) {

        float result = height * ( (max - y) / (max - min) );
        //Log.e(getClass().getSimpleName(), "Y : " + result);
        return result;
    }

    private void setLabelPaint(Paint paint, float size) {
        paint.reset();
        paint.setColor(0xFF383838);
        paint.setTextSize(size);
        //paint.setTypeface(Typeface.DEFAULT);
        paint.setTextAlign(Paint.Align.CENTER);

    }

    private void setPathPaint(Paint paint) {
        paint.reset();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(0xFFFF2222);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(1f);
    }

    private void setLinePaint(Paint paint) {
        paint.reset();
        paint.setColor(0xFF383838);
        paint.setStrokeWidth(1f);
        paint.setStyle(Paint.Style.STROKE);
    }

    private void setBoldLinePaint(Paint paint) {
        paint.reset();
        paint.setColor(0xFF383838);
        paint.setStrokeWidth(1.5f);
        paint.setStyle(Paint.Style.STROKE);
    }

    private void setSubLinePaint(Paint paint) {
        paint.reset();
        paint.setColor(0xFFC2C2C2);
        paint.setStrokeWidth(0.5f);
        paint.setStyle(Paint.Style.STROKE);

    }

    private void drawAreaSVC(Canvas canvas, float left, float top, float right, float bottom) {
        //텍스트 사이즈는 계산하지말고 마진으로 퉁치기 작전
        float margin = 20f;
        left += margin;
        top += margin;
        right -= margin;
        bottom -= margin;

        float seriesLength = 10f;
        left += seriesLength;
        bottom -= seriesLength;

        setLinePaint(paint);
        canvas.drawLine(left, top, right, top, paint);
        canvas.drawLine(right, top, right, bottom, paint);

        setBoldLinePaint(paint);
        canvas.drawLine(left, top, left, bottom, paint);
        canvas.drawLine(left, bottom, right, bottom, paint);


        setLabelPaint(paint, 10f);
        canvas.drawText(context.getString(R.string.pdf_time_volume_graph), left + ((right - left) / 2f), top - 5f, paint);
        drawGraphSVC(values, canvas, left, top, right, bottom);
    }

    private void drawGraphSVC(List<Coordinate> values, Canvas canvas, float left, float top, float right, float bottom) {

        float width = right - left;
        float height = bottom - top;

        float maxX = 60f;
        float minX = 0f;
        float maxY = 0.5f;
        float minY = -0.5f;
        //float xOffset = 0f;

        float accX = 0f;
        float accY = 0f;

        float xGap = 0f;
        float yGap = 0f;

        path.reset();
        path.moveTo(xToPosition(width, maxX, minX, accX) + left, yToPosition(height, maxY, minY, accY) + top);

        for (int i = 0; i < values.size(); i++) {

            Coordinate coordinate = values.get(i);

            boolean isOver = false;
            float x = (float)coordinate.getTime();
            float y = (float)coordinate.getVolume();
            float lps = (float)coordinate.getLps();

            if (lps >= 0f) accY -= y;
            else accY += y;

            //누적된 Y가 maxY를 넘었는지
            //누적된 y가 minY를 넘었는지

            if ((maxY * 0.95f) < accY) {
                isOver = true;

                minY *= (accY / (maxY * 0.95f));
                maxY *= (accY / (maxY * 0.95f));

            }

            if ((minY * 0.95f) > accY) {
                isOver = true;

                maxY *= Math.abs((accY / (minY * 0.95f)));
                minY *= Math.abs((accY / (minY * 0.95f)));
            }

            if (!isOver) {

                //path.lineTo(xToPosition(width, maxX, minX, accX) + left, yToPosition(height, maxY, minY, y) + top);

            } else {

                /*
                accX = 0f;
                accY = 0;

                path.reset();
                path.moveTo(xToPosition(width, maxX, minX, accX) + left, yToPosition(height, maxY, minY, accY) + top);


                for (int j = 0; j <= i; j++) {

                    Coordinate coo = values.get(j);
                    float x2 = (float)coo.getVolume();
                    float y2 = (float)coo.getLps();

                    if (y2 >= 0f) accX += x2;
                    else accX -= x2;

                    path.lineTo(xToPosition(width, maxX, minX, accX + xOffset) + left, yToPosition(height, maxY, minY, y2) + top);

                }

                 */


            }

        }

        accX = 0f;
        accY = 0f;
        xGap = measureGap(5f, 13, maxX, minX);
        yGap = measureGap(0.25f, 10, maxY, minY);

        float xGapSize = gapToSizeX(width, maxX, minX, xGap);
        float yGapSize = gapToSizeY(height, maxY, minY, yGap);

        float length = 5f;

        for (int i = 0; i <= (int)(maxX / xGap); i++) {

            setSubLinePaint(paint);
            canvas.drawLine(left + (xGapSize * (float)i), top, left + (xGapSize * (float)i), bottom + length, paint);
            setLabelPaint(paint, 5f);
            canvas.drawText(context.getString(R.string.float_two, (xGap * (float)i)), left + (xGapSize * (float)i), bottom + 10f, paint);

        }

        for (int i = 0; i <= (int)(maxY / yGap); i++) {

            setSubLinePaint(paint);
            canvas.drawLine(left - length ,top + yToPosition(height, maxY, minY, (yGap * i)), right, top + yToPosition(height, maxY, minY, (yGap * i)), paint);

            setLabelPaint(paint, 5f);
            canvas.drawText(context.getString(R.string.float_two, (yGap * (float)i)), left - 10f, top + yToPosition(height, maxY, minY, (yGap * i)), paint);
        }


        for (int i = 1; i <= (int)(Math.abs((minY / yGap))); i++) {

            setSubLinePaint(paint);
            canvas.drawLine(left - length, top + yToPosition(height, maxY, minY, -(yGap * i)), right, top + yToPosition(height, maxY, minY, -(yGap * i)), paint);

            setLabelPaint(paint, 5f);
            canvas.drawText(context.getString(R.string.float_two, -(yGap * i)), left - 10f, top + yToPosition(height, maxY, minY, -(yGap * i)) ,paint);
        }

        setPathPaint(paint);

        path.reset();
        path.moveTo(xToPosition(width, maxX, minX, accX) + left, yToPosition(height, maxY, minY, accY) + top);


        for (int i = 0; i < values.size(); i++) {

            Coordinate coo = values.get(i);
            float x2 = (float)coo.getTime();
            float y2 = (float)coo.getVolume();
            float lps = (float) coo.getLps();

            if (lps >= 0f) accY -= y2;
            else accY += y2;
            accX += x2;

            path.lineTo(xToPosition(width, maxX, minX, accX) + left, yToPosition(height, maxY, minY, accY) + top);

        }

        canvas.drawPath(path, paint);
        Log.e(getClass().getSimpleName(), maxX + ", " + minX + ", " + maxY + ", " + minY + ", ");

    }

    private void drawArea(Canvas canvas, float left, float top, float right, float bottom) {

        //텍스트 사이즈는 계산하지말고 마진으로 퉁치기 작전
        float margin = 20f;
        left += margin;
        top += margin;
        right -= margin;
        bottom -= margin;

        float seriesLength = 10f;
        left += seriesLength;
        bottom -= seriesLength;

        setLinePaint(paint);
        canvas.drawLine(left, top, right, top, paint);
        canvas.drawLine(right, top, right, bottom, paint);

        setBoldLinePaint(paint);
        canvas.drawLine(left, top, left, bottom, paint);
        canvas.drawLine(left, bottom, right, bottom, paint);


        setLabelPaint(paint, 10f);
        canvas.drawText(context.getString(R.string.pdf_volume_flow_graph), left + ((right - left) / 2f), top - 5f, paint);
        drawGraph(values, canvas, left, top, right, bottom);

    }

    private void drawGraph(List<Coordinate> values, Canvas canvas, float left, float top, float right, float bottom) {

        float width = right - left;
        float height = bottom - top;

        float maxX = 1.6f * (width / height);
        float minX = 0f;
        float maxY = 1.4f;
        float minY = -0.8f;
        float xOffset = 0f;

        float accX = 0f;
        float accY = 0f;

        float xGap = 0f;
        float yGap = 0f;

        path.reset();
        path.moveTo(xToPosition(width, maxX, minX, accX) + left, yToPosition(height, maxY, minY, accY) + top);

        for (int i = 0; i < values.size(); i++) {

            Coordinate coordinate = values.get(i);

            boolean isOver = false;
            float x = (float)coordinate.getVolume();
            float y = (float)coordinate.getLps();

            if (y >= 0f) accX += x;
            else accX -= x;

            //누적된 X 가 최대값을 넘지 않았는지
            //누적된 X 가 최소값을 넘지 않았는지
            //Y 가 최대값을 넘지 않았는지
            //Y 가 최소값을 넘지 않았는지
            //오프셋이 최대값을 넘지 않았는지

            if (accX < minX) {
                isOver = true;

                xOffset += x;
                accX = 0f;

            }

            if (accX > (maxX * 0.95f)) {
                isOver = true;

                maxY *= (accX / (maxX * 0.95f));
                minY *= (accX / (maxX * 0.95f));

                maxX *= (accX / (maxX * 0.95f));
            }

            if (xOffset > maxX) {
                isOver = true;

                maxY *= (xOffset / maxX);
                minY *= (xOffset / maxX);

                maxX = xOffset;
            }

            if (y > (maxY * 0.95f)) {
                isOver = true;

                maxX *= (y / (maxY * 0.95f));
                minY *= (y / (maxY * 0.95f));

                maxY *= (y / (maxY * 0.95f));
            }

            if (y < (minY * 0.95f)) {
                isOver = true;

                maxX *= Math.abs((y / (minY * 0.95f)));
                maxY *= Math.abs((y / (minY * 0.95f)));

                minY *= Math.abs((y / (minY * 0.95f)));
            }


            if (!isOver) {

                //path.lineTo(xToPosition(width, maxX, minX, accX) + left, yToPosition(height, maxY, minY, y) + top);

            } else {

                /*
                accX = 0f;
                accY = 0;

                path.reset();
                path.moveTo(xToPosition(width, maxX, minX, accX) + left, yToPosition(height, maxY, minY, accY) + top);


                for (int j = 0; j <= i; j++) {

                    Coordinate coo = values.get(j);
                    float x2 = (float)coo.getVolume();
                    float y2 = (float)coo.getLps();

                    if (y2 >= 0f) accX += x2;
                    else accX -= x2;

                    path.lineTo(xToPosition(width, maxX, minX, accX + xOffset) + left, yToPosition(height, maxY, minY, y2) + top);

                }

                 */


            }





        }

        accX = 0f;
        accY = 0f;
        xGap = measureGap(0.25f, 9, maxX, minX);
        yGap = measureGap(0.25f, 10, maxY, minY);

        float xGapSize = gapToSizeX(width, maxX, minX, xGap);
        float yGapSize = gapToSizeY(height, maxY, minY, yGap);
        float yCenter = height * (maxY / (maxY - minY));

        float length = 5f;

        for (int i = 0; i <= (int)(maxX / xGap); i++) {

            setSubLinePaint(paint);
            canvas.drawLine(left + (xGapSize * (float)i), top, left + (xGapSize * (float)i), bottom + length, paint);
            setLabelPaint(paint, 5f);
            canvas.drawText(context.getString(R.string.float_two, (xGap * (float)i)), left + (xGapSize * (float)i), bottom + 10f, paint);

        }

        for (int i = 0; i <= (int)(maxY / yGap); i++) {

            setSubLinePaint(paint);
            canvas.drawLine(left - length ,top + yToPosition(height, maxY, minY, (yGap * i)), right, top + yToPosition(height, maxY, minY, (yGap * i)), paint);

            setLabelPaint(paint, 5f);
            canvas.drawText(context.getString(R.string.float_two, (yGap * (float)i)), left - 10f, top + yToPosition(height, maxY, minY, (yGap * i)), paint);
        }


        for (int i = 1; i <= (int)(Math.abs((minY / yGap))); i++) {

            setSubLinePaint(paint);
            canvas.drawLine(left - length, top + yToPosition(height, maxY, minY, -(yGap * i)), right, top + yToPosition(height, maxY, minY, -(yGap * i)), paint);

            setLabelPaint(paint, 5f);
            canvas.drawText(context.getString(R.string.float_two, -(yGap * i)), left - 10f, top + yToPosition(height, maxY, minY, -(yGap * i)) ,paint);
        }

        setPathPaint(paint);

        path.reset();
        path.moveTo(xToPosition(width, maxX, minX, accX + xOffset) + left, yToPosition(height, maxY, minY, accY) + top);


        for (int i = 0; i < values.size(); i++) {

            Coordinate coo = values.get(i);
            float x2 = (float)coo.getVolume();
            float y2 = (float)coo.getLps();

            if (y2 >= 0f) accX += x2;
            else accX -= x2;

            path.lineTo(xToPosition(width, maxX, minX, accX + xOffset) + left, yToPosition(height, maxY, minY, y2) + top);

        }

        canvas.drawPath(path, paint);
        Log.e(getClass().getSimpleName(), maxX + ", " + minX + ", " + maxY + ", " + minY + ", " + xOffset);

    }

    private int dateDiff(long from, long to) {

        Calendar fromCalendar = Calendar.getInstance();
        fromCalendar.setTimeInMillis(from);

        Calendar toCalendar = Calendar.getInstance();
        toCalendar.setTimeInMillis(to);

        int yearDiff = toCalendar.get(Calendar.YEAR) - fromCalendar.get(Calendar.YEAR);
        int monthDiff = toCalendar.get(Calendar.MONTH) - fromCalendar.get(Calendar.MONTH);

        return (yearDiff * 12) + monthDiff;

    }


}
