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
    private float widthMargin, heightMargin;
    private float xInterval, yInterval;
    private int xMarking, yMarking;

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

        //X축 중간선
        canvas.drawLine(widthMargin, canvasHeight / 2f, canvasWidth - widthMargin, canvasHeight / 2f, linePaint);

        //X 축 하단선
        canvas.drawLine(widthMargin, canvasHeight - heightMargin, canvasWidth - widthMargin, canvasHeight - heightMargin, linePaint);
        //Y 축 측면선
        canvas.drawLine(widthMargin, heightMargin, widthMargin, canvasHeight - heightMargin, linePaint);

        //경로 그리기
        canvas.drawPath(path, pathPaint);


        for (int i = 1; i <= xMarking; i++) {
            canvas.drawLine(((float)xPadding * (float)i) + widthMargin, canvasHeight - heightMargin, ((float)xPadding * (float)i) + widthMargin, (canvasHeight - heightMargin) - 20f, linePaint); // 중앙에 x축 눈금 그리기
            canvas.drawText(Float.toString(Fluid.autoRound(1, ((float)xInterval * (float) i))), ((float)xPadding * (float)i) + widthMargin - 20f, (canvasHeight - heightMargin) - 25f, labelPaint);
            //canvas.drawText(Float.toString(Fluid.autoRound(1, maxX - (xInterval * (double) i))), (xPadding * (float)i) - 20f, (canvasHeight) - 25f, labelPaint);
        }

        for (int i = 1; i < yMarking; i++) {

            canvas.drawLine(widthMargin, (yPadding * (float)i) + heightMargin, widthMargin + 20f, (yPadding * (float)i) + heightMargin, linePaint);
            canvas.drawText(Float.toString(Fluid.autoRound(1, (maxY - (yInterval * (double)i)))), widthMargin + 25f, ((yPadding * (float)i) + heightMargin) + 10f, labelPaint);

        }


    }

    public void setValue(float x, float y) {

        // Y축만 더 크면 늘리고 x축은 그대로.

        boolean isOver = false;
        values.add(new Coordinate(x, y));

        if (maxY < this.y + y) {
            isOver = true;

            minY *= ((y + this.y) / maxY);
            maxY += y;

        }

        if (minY > this.y + y) {
            isOver = true;

            maxY *= Math.abs(y + this.y) / Math.abs(minY);
            minY += y;

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

        xPadding = (canvasWidth - (widthMargin * 2f)) / (float)xMarking;
        yPadding = (canvasHeight - (heightMargin * 2f)) / (float)yMarking;

        path.reset();
        path.moveTo(widthMargin, canvasHeight / 2f);
    }

    public void setCanvasSize(float width, float height) {

        this.canvasWidth = width;
        this.canvasHeight = height;

        //5% 를 상하좌우 마진영역으로 잡음.
        setMargin();

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

    private void setMargin() {

        this.widthMargin = canvasWidth * 0.05f;
        this.heightMargin = canvasHeight * 0.05f;

    }

    private float xToPosition(float value) {
        return widthMargin + ( (value / (maxX - minX)) * (canvasWidth - (widthMargin * 2f)) );

    }

    private float yToPosition(float value) {
        return heightMargin + ( ((maxY - value) / (maxY - minY)) * (canvasHeight - (heightMargin * 2f)) );

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
