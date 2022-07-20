package kr.co.theresearcher.spirokitfortab.graph;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import kr.co.theresearcher.spirokitfortab.R;

public class SlowVolumeTimeGraphView extends View {

    private float canvasWidth, canvasHeight;

    private float maxX, minX;
    private float maxY, minY;
    //private float xOffset;

    private float verticalLabelMargin;
    private float horizontalLabelMargin;
    private float outLineLength;

    private float x = 0f;
    private float y = 0f;
    private List<Coordinate> values;

    private Path path;
    private Paint labelPaint, linePaint, pathPaint, detailLinePaint;

    private int leftMargin = 0;
    private int topMargin = 0;
    private int rightMargin = 0;
    private int bottomMargin = 0;

    private float xGap = 0f;
    private float yGap = 0f;

    public SlowVolumeTimeGraphView(Context context) {
        super(context);

        values = new ArrayList<>();
        labelPaint = new Paint();
        linePaint = new Paint();
        pathPaint = new Paint();
        detailLinePaint = new Paint();
        path = new Path();

        outLineLength = 20f;
        horizontalLabelMargin = 50f;
        verticalLabelMargin = 80f;

        setLabelPaint();
        setLinePaint();
        setPathPaint();
        setDetailLinePaint();

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //제목 라벨
        //canvas.drawText("Volume|Flow Graph", canvasWidth / 2f, horizontalLabelMargin, labelPaint);

        //축 라벨
        canvas.drawText("Volume(L)", (leftMargin + verticalLabelMargin + outLineLength), 30f, labelPaint);
        canvas.drawText("Time(s)", canvasWidth - 100f, canvasHeight - 30f, labelPaint);

        //Y축 선
        canvas.drawLine((leftMargin + outLineLength + verticalLabelMargin), topMargin, (leftMargin + outLineLength + verticalLabelMargin), (canvasHeight - bottomMargin - outLineLength - horizontalLabelMargin), linePaint);
        //X축 선
        canvas.drawLine((leftMargin + outLineLength + verticalLabelMargin), (canvasHeight - bottomMargin - outLineLength - horizontalLabelMargin), (canvasWidth - rightMargin), (canvasHeight - bottomMargin - outLineLength - horizontalLabelMargin), linePaint);


        float temp = 0f;
        float accGap = 0f;
        float gapSize = gapToSizeX(xGap);

        for (accGap = 0f; accGap <= maxX;) {

            //canvas.drawLine((float)xPadding * (float)i, canvasHeight, ((float)xPadding * (float)i), 0f, detailLinePaint);


            canvas.drawLine(
                    (leftMargin + outLineLength + verticalLabelMargin) + temp,
                    (canvasHeight - bottomMargin - outLineLength - horizontalLabelMargin),
                    (leftMargin + outLineLength + verticalLabelMargin) + temp,
                    (canvasHeight - bottomMargin - horizontalLabelMargin),
                    detailLinePaint);

            canvas.drawLine(
                    (leftMargin + outLineLength + verticalLabelMargin) + temp,
                    (canvasHeight - bottomMargin - outLineLength - horizontalLabelMargin),
                    (leftMargin + outLineLength + verticalLabelMargin) + temp,
                    (topMargin),
                    detailLinePaint);

            //canvas.drawText(Float.toString(Fluid.autoRound(1, ((float)xInterval * (float) i))), ((float)xPadding * (float)i) - outLineLength, (canvasHeight * 0.5f) - 25f, labelPaint);
            String label = getContext().getString(R.string.with_sec, accGap);
            canvas.drawText(label, (leftMargin + outLineLength + horizontalLabelMargin) + temp - 10f, (canvasHeight - bottomMargin - outLineLength - 5f), labelPaint);

            accGap += xGap;
            temp += gapSize;
        }

        accGap = 0f;
        gapSize = gapToSizeY(yGap);
        temp = gapSize;

        float center = (((canvasHeight - topMargin - bottomMargin - outLineLength - horizontalLabelMargin) * (maxY / (maxY - minY))) + topMargin);

        canvas.drawLine((leftMargin + verticalLabelMargin), center, (canvasWidth - rightMargin), center, linePaint);
        String centerLabel = getContext().getString(R.string.with_L, 0f);
        canvas.drawText(centerLabel, (leftMargin), center + 10f, labelPaint);

        for (accGap += yGap; accGap <= maxY; accGap += yGap) {

            //canvas.drawLine(0f, yPadding * (float)i, canvasWidth, yPadding * (float)i, detailLinePaint);

            //Log.e(getClass().getSimpleName(), "ACC GAP Y : " + accGap + ", MAX Y : " + maxY);

            canvas.drawLine(
                    (leftMargin + outLineLength + verticalLabelMargin),
                    center - temp,
                    leftMargin + verticalLabelMargin,
                    center - temp,
                    detailLinePaint);

            canvas.drawLine(
                    (leftMargin + outLineLength + verticalLabelMargin),
                    center - temp,
                    canvasWidth - rightMargin,
                    center - temp,
                    detailLinePaint);

            String label = getContext().getString(R.string.with_L, accGap);
            canvas.drawText(label, leftMargin, center - temp + 10f, labelPaint);

            temp += gapSize;
        }

        accGap = 0f;
        gapSize = gapToSizeY(yGap);
        temp = gapSize;

        for (accGap -= yGap; accGap >= minY;) {

            //Log.e(getClass().getSimpleName(), "ACC GAP Y : " + accGap + ", MIN Y : " + minY);

            canvas.drawLine(
                    (leftMargin + outLineLength + verticalLabelMargin),
                    center + temp,
                    leftMargin + verticalLabelMargin,
                    center + temp,
                    detailLinePaint);

            canvas.drawLine(
                    (leftMargin + outLineLength + verticalLabelMargin),
                    center + temp,
                    canvasWidth - rightMargin,
                    center + temp,
                    detailLinePaint);

            String label = getContext().getString(R.string.with_L, accGap);
            canvas.drawText(label, leftMargin, center + temp + 10f, labelPaint);

            accGap -= yGap;
            temp += gapSize;

        }

        //경로 그리기
        canvas.drawPath(path, pathPaint);

    }

    //초기 설정이 끝나거나 setValue 후 값 조정이 끝났을 때 사용
    public void commit() {

        if ((maxX / xGap) > 12) {
            xGap = (maxX * 0.1f);
        }

        if (((maxY / yGap) > 9)) {
            yGap = maxY * 0.2f;
        }

        path.reset();
        path.moveTo(xToPosition(this.x), yToPosition(0d));


    }

    public void clear() {
        values.clear();
        this.x = 0f;
        this.y = 0f;
        //xOffset = 0f;
        commit();


    }

    public void setX(float max, float min) {

        maxX = max;
        minX = min;

    }

    public void setY(float max, float min) {

        maxY = max;
        minY = min;

    }

    public void setValue(float x, float y) {

        boolean isOver = false;
        values.add(new Coordinate(x, y));

        if (maxY < this.y + y) {
            isOver = true;

            minY *= ((y + maxY) / maxY);
            maxY *= ((y + maxY) / maxY);

        }

        if (minY > this.y + y) {
            isOver = true;

            maxY *= Math.abs(y + minY) / Math.abs(minY);
            minY *= Math.abs(y + minY) / Math.abs(minY);

        }


        if (isOver) {

            this.x = 0f;
            this.y = 0f;

            commit();

            for (int i = 0; i < values.size(); i++) {

                this.x += values.get(i).getX();
                this.y += values.get(i).getY();

                path.lineTo(xToPosition(this.x), yToPosition(this.y));

            }


        } else {

            this.x += x;
            this.y += y;

            path.lineTo(xToPosition(this.x), yToPosition(this.y));

        }



    }

    public void setCanvasSize(float width, float height) {

        this.canvasWidth = width;
        this.canvasHeight = height;

    }

    public void setMargin(int left, int top, int right, int bottom) {
        leftMargin = left;
        topMargin = top;
        rightMargin = right;
        bottomMargin = bottom;

    }

    private double findOffset() {

        double offset = 0d;
        double acc = 0d;

        for (int i = 0; i < values.size(); i++) {

            if (values.get(i).getY() >= 0d) {

                acc -= values.get(i).getX();

            } else {
                acc += values.get(i).getX();
            }


            if (acc > offset) offset = acc;

        }

        return offset;


    }

    private float gapToSizeX(float gap) {

        return (canvasWidth - leftMargin - rightMargin - outLineLength - verticalLabelMargin) * (gap / maxX);

    }

    private float gapToSizeY(float gap) {

        return (canvasHeight - (topMargin + bottomMargin + outLineLength + horizontalLabelMargin)) * (gap / (maxY - minY));

    }


    private float xToPosition(double value) {
        return (((float)(((value) / maxX))) * (canvasWidth - leftMargin - rightMargin - outLineLength - verticalLabelMargin)) + (leftMargin + outLineLength + verticalLabelMargin);

    }

    private float yToPosition(double value) {
        return (((float)((maxY - value) / (maxY - minY))) * (canvasHeight - topMargin - bottomMargin - outLineLength - horizontalLabelMargin)) + (topMargin);

    }

    private void setLabelPaint() {

        labelPaint.setColor(getContext().getColor(R.color.secondary_color));
        labelPaint.setTextSize(20f);
       // labelPaint.setTextAlign(Paint.Align.RIGHT);

    }

    private void setLinePaint() {

        linePaint.setColor(getContext().getColor(R.color.secondary_color));
        linePaint.setStrokeWidth(2f);

    }

    private void setPathPaint() {

        pathPaint.setAntiAlias(true);
        pathPaint.setStrokeWidth(3f);
        pathPaint.setStyle(Paint.Style.STROKE);
        pathPaint.setColor(getContext().getColor(R.color.primary_color));

    }

    private void setDetailLinePaint() {

        detailLinePaint.setColor(getContext().getColor(R.color.gray));
        detailLinePaint.setStrokeWidth(1f);

    }
}
