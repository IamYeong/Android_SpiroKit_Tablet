package kr.co.theresearcher.spirokitfortab.graph;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import kr.co.theresearcher.spirokitfortab.Fluid;
import kr.co.theresearcher.spirokitfortab.R;

public class VolumeFlowGraphView extends View {


    private float canvasWidth, canvasHeight;

    private float maxX, minX;
    private float maxY, minY;

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

    public VolumeFlowGraphView(Context context) {
        super(context);

        values = new ArrayList<>();
        labelPaint = new Paint();
        linePaint = new Paint();
        pathPaint = new Paint();
        detailLinePaint = new Paint();
        path = new Path();

        //setLabelPaint();
        //setLinePaint();
        //setPathPaint();
        //setDetailLinePaint();
    }

    /*
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //제목 라벨
        //canvas.drawText("Volume|Flow Graph", canvasWidth / 2f, 30f, labelPaint);

        //축 라벨
        canvas.drawText("Flow(l/s)", 30f, 30f, labelPaint);
        canvas.drawText("Volume(L)", canvasWidth - 100f, canvasHeight - 30f, labelPaint);

        //Y축 선
        canvas.drawLine((leftMargin + 20f + 30f), topMargin, (leftMargin + 20f + 30f), (canvasHeight - bottomMargin - 20f - 30f), linePaint);
        //X축 선
        canvas.drawLine((leftMargin + 20f + 30f), (canvasHeight - bottomMargin - 20f - 30f), (canvasWidth - rightMargin), (canvasHeight - bottomMargin - 20f - 30f), linePaint);


        float temp = 0f;
        float accGap = 0f;
        float gapSize = gapToSizeX(xGap);

        for (accGap = 0f; accGap <= maxX;) {

            //canvas.drawLine((float)xPadding * (float)i, canvasHeight, ((float)xPadding * (float)i), 0f, detailLinePaint);

            canvas.drawLine((leftMargin + 20f + 30f) + temp, (canvasHeight - bottomMargin - 20f - 30f), (leftMargin + 20f + 30f) + temp, (canvasHeight - bottomMargin - 30f), linePaint); // 중앙에 x축 눈금 그리기
            //canvas.drawText(Float.toString(Fluid.autoRound(1, ((float)xInterval * (float) i))), ((float)xPadding * (float)i) - 20f, (canvasHeight * 0.5f) - 25f, labelPaint);
            String label = getContext().getString(R.string.with_L, accGap);
            canvas.drawText(label, (leftMargin + 20f + 30f) + temp - 10f, (canvasHeight - bottomMargin - 20f - 5f), labelPaint);

            accGap += xGap;
            temp += gapSize;
        }

        //y축 중앙선
        float center = (((canvasHeight - topMargin - bottomMargin - 20f - 30f) / 2f) + topMargin);

        canvas.drawLine((leftMargin + 30f), center, (canvasWidth - rightMargin), center, linePaint);
        canvas.drawText("0", (leftMargin), center + 10f, labelPaint);

        accGap = 0f;
        gapSize = gapToSizeY(yGap);
        temp = gapSize;

        for (accGap += yGap; accGap <= maxY;) {

            //canvas.drawLine(0f, yPadding * (float)i, canvasWidth, yPadding * (float)i, detailLinePaint);

            canvas.drawLine((leftMargin + 20f + 30f), center - temp, leftMargin + 30f, center - temp,linePaint);

            String label = getContext().getString(R.string.with_lps, accGap);
            canvas.drawText(label, leftMargin, center - temp + 10f, labelPaint);

            accGap += yGap;
            temp += gapSize;
        }

        accGap = 0f;
        gapSize = gapToSizeY(yGap);
        temp = gapSize;

        for (accGap -= yGap; accGap >= minY;) {

            canvas.drawLine((leftMargin + 20f + 30f), center + temp, leftMargin + 30f, center + temp,linePaint);

            String label = getContext().getString(R.string.with_lps, accGap);
            canvas.drawText(label, leftMargin, center + temp + 10f, labelPaint);

            accGap -= yGap;
            temp += gapSize;

        }

        //경로 그리기
        canvas.drawPath(path, pathPaint);

    }

    //초기 설정이 끝나거나 setValue 후 값 조정이 끝났을 때 사용
    public void commit() {


        this.x = (maxX - (float)findOffset()) - xValueMargin;
        path.reset();
        path.moveTo(xToPosition(this.x), yToPosition(0d));


    }

    public void clear() {
        values.clear();
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
        if (y >= 0d) this.x -= x;
        else this.x += x;
        values.add(new Coordinate(x, y));

        if ((this.x > (maxX - xValueMargin))) {

            isOver = true;

            maxY *= (x + (maxX - minX)) / (maxX - minX);
            minY *= (x + (maxX - minX)) / (maxX - minX);
            maxX += (x * 2f);

        }

        if ((this.x < minX )) {

            isOver = true;


            minX = this.x;

        }

        if (y > (maxY - yValueMargin)) {

            isOver = true;

            maxX *= y / (maxY - yValueMargin);
            minY *= y / (maxY - yValueMargin);
            maxY *= y / (maxY - yValueMargin);

        }

        if (y < (minY + yValueMargin)) {

            isOver = true;


            maxY *= Math.abs(y / (minY + yValueMargin));
            maxX *= Math.abs(y / (minY + yValueMargin));
            minY *= Math.abs(y / (minY + yValueMargin));

        }

        //Log.d(getClass().getSimpleName(), this.x + ", " + y + "___" + xValueMargin + ", " + (yValueMargin));

        if (isOver) {

            //this.x = (maxX - minX) * xStartPosition;
            commit();

            for (int i = 0; i < values.size(); i++) {

                this.y = values.get(i).getY();

                if (this.y >= 0d) {
                    this.x -= values.get(i).getX();

                } else {
                    this.x += values.get(i).getX();

                }
                path.lineTo(xToPosition(this.x), yToPosition(this.y));

            }

        } else {
            path.lineTo(xToPosition(this.x), yToPosition(y));
        }


    }

    public void setCanvasSize(float width, float height) {

        this.canvasWidth = width;
        this.canvasHeight = height;

    }

    public void setMargin(int left, int top, int right, int bottom) {

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

        return (canvasWidth - (leftMargin + rightMargin + 20f + 30f)) * (gap / maxX);

    }

    private float gapToSizeY(float gap) {

        return (canvasHeight - (topMargin + bottomMargin + 20f + 30f)) * (gap / maxY);

    }

    private float xToPosition(double value) {
        return ((float)(1d - (value / (maxX - minX)))) * canvasWidth;

    }

    private float yToPosition(double value) {
        return ((float)((maxY - value) / (maxY - minY))) * canvasHeight;

    }

    private void setLabelPaint() {

        labelPaint.setColor(getContext().getColor(R.color.secondary_color));
        labelPaint.setTextSize(20f);

    }

    private void setLinePaint() {

        linePaint.setColor(getContext().getColor(R.color.gray));
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

     */

}
