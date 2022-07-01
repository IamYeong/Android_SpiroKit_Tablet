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

public class SlowVolumeTimeRunView extends View {

    private List<Coordinate> values;

    private float canvasWidth, canvasHeight;
    private float x = 0f;
    private float y = 0f;

    private float maxX, minX;
    private float maxY, minY;

    private float xPadding, yPadding;
    private float xInterval, yInterval;
    private int xMarking, yMarking;

    private float xValueMargin, yValueMargin;

    private Paint pathPaint, labelPaint, linePaint;
    private Path path;


    public SlowVolumeTimeRunView(Context context) {
        super(context);

        values = new ArrayList<>();
        labelPaint = new Paint();
        linePaint = new Paint();
        pathPaint = new Paint();
        path = new Path();

        setLabelPaint();
        setLinePaint();
        setPathPaint();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //축 라벨
        canvas.drawText("Volume(L)", 30f, 30f, labelPaint);
        canvas.drawText("Time(s)", canvasWidth - 100f, canvasHeight - 30f, labelPaint);

        //제목 라벨
        canvas.drawText("Volume|Time Graph", canvasWidth / 2f, 30f, labelPaint);

        //X축 중간선
        canvas.drawLine(0f, canvasHeight / 2f, canvasWidth, canvasHeight / 2f, linePaint);

        //X 축 하단선
        //canvas.drawLine(widthMargin, canvasHeight - heightMargin, canvasWidth - widthMargin, canvasHeight - heightMargin, linePaint);
        //Y 축 측면선
        //canvas.drawLine(widthMargin, heightMargin, widthMargin, canvasHeight - heightMargin, linePaint);

        //경로 그리기
        canvas.drawPath(path, pathPaint);


        for (int i = 1; i < xMarking; i++) {
            canvas.drawLine(((float)xPadding * (float)i), canvasHeight, ((float)xPadding * (float)i), (canvasHeight) - 20f, linePaint); // 중앙에 x축 눈금 그리기
            canvas.drawText(Float.toString(Fluid.autoRound(1, ((float)xInterval * (float) i))), ((float)xPadding * (float)i) - 20f, (canvasHeight) - 25f, labelPaint);
            //canvas.drawText(Float.toString(Fluid.autoRound(1, maxX - (xInterval * (double) i))), (xPadding * (float)i) - 20f, (canvasHeight) - 25f, labelPaint);
        }

        for (int i = 1; i < yMarking; i++) {

            canvas.drawLine(0f, (yPadding * (float)i), 20f, (yPadding * (float)i), linePaint);
            canvas.drawText(Float.toString(Fluid.autoRound(1, (maxY - (yInterval * (double)i)))), 25f, ((yPadding * (float)i)) + 10f, labelPaint);

        }


    }

    public void setValue(float x, float y) {

        // Y축만 더 크면 늘리고 x축은 그대로.

        boolean isOver = false;
        values.add(new Coordinate(x, y));

        if ((maxY - yValueMargin) < this.y + y) {
            isOver = true;

            minY *= ((y + (maxY - yValueMargin)) /(maxY - yValueMargin));
            maxY *= ((y + (maxY - yValueMargin)) /(maxY - yValueMargin));

        }

        if ((minY + yValueMargin) > this.y + y) {
            isOver = true;

            maxY *= Math.abs(y + (minY + yValueMargin)) / Math.abs(minY + yValueMargin);
            minY *= Math.abs(y + (minY + yValueMargin)) / Math.abs(minY + yValueMargin);

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

    public void commit() {

        xInterval = (maxX - minX) / (float)(xMarking);
        yInterval = (maxY - minY) / (float)(yMarking);

        xPadding = (canvasWidth) / (float)xMarking;
        yPadding = (canvasHeight) / (float)yMarking;

        xValueMargin = maxX * 0.05f;
        yValueMargin = (maxY - minY) * 0.05f;

        path.reset();
        path.moveTo(0f, canvasHeight / 2f);
    }

    public void setCanvasSize(float width, float height) {

        this.canvasWidth = width;
        this.canvasHeight = height;

        //5% 를 상하좌우 마진영역으로 잡음.

    }


    public void setX(float max, float min) {

        maxX = max;
        minX = min;

    }

    public void setY(float max, float min) {

        maxY = max;
        minY = min;

    }

    public void setMarkingCount(int horizontalNum, int verticalNum) {
        xMarking = horizontalNum;
        yMarking = verticalNum;
    }

    public void clear() {
        x = 0f;
        y = 0f;
        values.clear();
        commit();
    }

    private float xToPosition(float value) {
        return ( (value / (maxX - minX)) * (canvasWidth) );

    }

    private float yToPosition(float value) {
        return  ( ((maxY - value) / (maxY - minY)) * (canvasHeight) );

    }

    private void setLabelPaint() {

        labelPaint.setColor(getContext().getColor(R.color.secondary_color));
        labelPaint.setTextSize(20f);

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

}
