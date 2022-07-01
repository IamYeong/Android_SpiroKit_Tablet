package kr.co.theresearcher.spirokitfortab.graph;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import kr.co.theresearcher.spirokitfortab.R;
import kr.co.theresearcher.spirokitfortab.Fluid;

public class VolumeTimeResultView extends View {

    private float canvasWidth, canvasHeight;

    private float maxX, minX;
    private float maxY, minY;
    private float xStartPosition;
    //private float yStartPosition;
    private float x = 0f;
    private float y = 0f;
    private List<Coordinate> values;

    private Path path;
    private Paint labelPaint, linePaint, pathPaint;

    private float xInterval, yInterval;
    private float xPadding, yPadding;
    private int xMarking, yMarking;
    private float xValueMargin, yValueMargin;

    public VolumeTimeResultView(Context context) {
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
        //canvas.drawText("Volume|Time Curve", canvasWidth / 2f, 30f, labelPaint);

        //X 축 하단선
        //canvas.drawLine(0f, canvasHeight, canvasWidth, canvasHeight, linePaint);
        //Y 축 측면선
        //canvas.drawLine(0f, canvasHeight, 0f, 0f, linePaint);

        //경로 그리기
        //마지막 평평한 그래프경로 추가
        path.lineTo(canvasWidth - (canvasWidth * 0.05f), yToPosition(y));
        canvas.drawPath(path, pathPaint);


        for (int i = 1; i < xMarking; i++) {
            canvas.drawLine((float)xPadding * (float)i, canvasHeight, ((float)xPadding * (float)i), (canvasHeight) - 20f, linePaint); // 중앙에 x축 눈금 그리기
            canvas.drawText(Float.toString(Fluid.autoRound(1, ((float)xInterval * (float) i) - xValueMargin)), ((float)xPadding * (float)i) - 5f, (canvasHeight) - 25f, labelPaint);
            //canvas.drawText(Float.toString(Fluid.autoRound(1, maxX - (xInterval * (double) i))), (xPadding * (float)i) - 20f, (canvasHeight) - 25f, labelPaint);
        }

        for (int i = 1; i < yMarking; i++) {

            canvas.drawLine(0f, yPadding * (float)i, 20f, yPadding * (float)i,linePaint);
            canvas.drawText(Float.toString(Fluid.autoRound(1, (maxY - (yInterval * (double)i)))), 25f, (yPadding * (float)i) + 10f, labelPaint);


        }

    }

    //초기 설정이 끝나거나 setValue 후 값 조정이 끝났을 때 사용
    public void commit() {

        xInterval = (maxX - minX) / (float)(xMarking);
        yInterval = (maxY - minY) / (float)(yMarking);

        xPadding = canvasWidth / (float)xMarking;
        yPadding = canvasHeight / (float)yMarking;

        xValueMargin = maxX * 0.05f;
        yValueMargin = maxY * 0.05f;

        path.reset();
        path.moveTo(xToPosition(xValueMargin), yToPosition(0f));


    }

    public void setX(float max, float min) {

        maxX = max;
        minX = min;

    }

    public void setY(float max, float min) {

        maxY = max;
        minY = min;

    }

    public void setValue(float x, float y, float flow) {

        if (flow <= 0f) return;

        boolean isOver = false;
        values.add(new Coordinate(x, y));

        if ((this.x + x) > (maxX - xValueMargin)) {

            isOver = true;

            maxY *= (x + (maxX - xValueMargin)) / (maxX - xValueMargin);
            maxX *= (x + (maxX - xValueMargin)) / (maxX - xValueMargin);

        }

        if ((this.y + y) > (maxY - yValueMargin)) {

            isOver = true;

            maxX *= (y + (maxY - yValueMargin)) / (maxY - yValueMargin);
            maxY *= (y + (maxY - yValueMargin)) / (maxY - yValueMargin);

        }

        if (isOver) {

            this.x = xValueMargin;
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

    public void clear() {

        values.clear();
        this.x = xValueMargin;
        this.y = 0f;
        commit();

    }

    public void setCanvasSize(float width, float height) {

        this.canvasWidth = width;
        this.canvasHeight = height;

    }

    public void setxStartPosition(float xStartPosition) {
        this.xStartPosition = xStartPosition;
    }


    public void setMarkingCount(int horizontalNum, int verticalNum) {
        xMarking = horizontalNum;
        yMarking = verticalNum;
    }

    private float xToPosition(double value) {
        return ((float)(value / (maxX - minX))) * canvasWidth;

    }

    private float yToPosition(double value) {
        return ((float)(1d - ((value) / (maxY - minY)))) * canvasHeight;

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
