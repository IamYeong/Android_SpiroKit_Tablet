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

public class VolumeFlowResultView extends View {

    private float canvasWidth, canvasHeight;

    private double maxX, minX;
    private double maxY, minY;
    private float xStartPosition;
    //private float yStartPosition;
    private double x = 0d;
    private double y = 0d;
    private List<ResultCoordinate> values;

    private Path path;
    private Paint labelPaint, linePaint, pathPaint;

    private double xInterval, yInterval;
    private float xPadding, yPadding;
    private int xMarking, yMarking;

    public VolumeFlowResultView(Context context) {
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



        //X 축 중앙선
        canvas.drawLine(0f, canvasHeight * 0.5f, canvasWidth, canvasHeight * 0.5f, linePaint);
        //Y 축 측면선
        canvas.drawLine(0f, canvasHeight, 0f, 0f, linePaint);

        //경로 그리기
        canvas.drawPath(path, pathPaint);


        for (int i = 1; i < xMarking; i++) {
            canvas.drawLine((float)xPadding * (float)i, canvasHeight * 0.5f, ((float)xPadding * (float)i), (canvasHeight * 0.5f) - 20f, linePaint); // 중앙에 x축 눈금 그리기
            canvas.drawText(Float.toString(Fluid.autoRound(1, (xInterval * (double) i))), ((float)xPadding * (float)i) - 20f, (canvasHeight * 0.5f) - 25f, labelPaint);
            //canvas.drawText(Float.toString(Fluid.autoRound(1, maxX - (xInterval * (double) i))), (xPadding * (float)i) - 20f, (canvasHeight * 0.5f) - 25f, labelPaint);
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

        this.x = maxX - findOffset();
        path.reset();
        path.moveTo(xToPosition(this.x), yToPosition(0d));


    }

    public void setX(double max, double min) {

        maxX = max;
        minX = min;

    }

    public void setY(double max, double min) {

        maxY = max;
        minY = min;

    }

    public void setValue(double x, double y) {

        boolean isOver = false;
        if (y >= 0d) this.x -= x;
        else this.x += x;
        values.add(new ResultCoordinate(x, y));

        if ((this.x > maxX)) {

            isOver = true;
            //System.out.println("X OVER");

            maxY *= this.x / (maxX - minX);
            minY *= this.x / (maxX - minX);
            maxX += x;

        }

        if (this.x < minX) {

            isOver = true;

            maxY *= (x + (maxX - minX)) / (maxX - minX);
            minY *= (x + (maxX - minX)) / (maxX - minX);
            maxX += x;
            this.x = 0d;
        }

        if (y > maxY) {

            isOver = true;
            //System.out.println("Y max over");

            maxX *= y / maxY;
            minY *= y / maxY;
            maxY = y;

        }

        if (y < minY) {

            isOver = true;
            //System.out.println("Y min over");

            maxX *= Math.abs(y / minY);
            maxY *= Math.abs(y / minY);
            minY = y;

        }

    }

    public void apply() {

        //초깃값을 어떻게 찾아낼 것인가

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

    }

    //원래 폐에 얼만큼의 공기를 가지고 있었는지 유추하는 함수
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
        return ((float)(1d - (value / (maxX - minX)))) * canvasWidth;

    }

    private float yToPosition(double value) {
        return ((float)((maxY - value) / (maxY - minY))) * canvasHeight;

    }

    private void setLabelPaint() {

        labelPaint.setColor(getContext().getColor(R.color.secondary_color));
        labelPaint.setTextSize(30f);

    }

    private void setLinePaint() {

        linePaint.setColor(getContext().getColor(R.color.secondary_color));
        linePaint.setStrokeWidth(3f);

    }

    private void setPathPaint() {

        pathPaint.setAntiAlias(true);
        pathPaint.setStrokeWidth(6f);
        pathPaint.setStyle(Paint.Style.STROKE);
        pathPaint.setColor(getContext().getColor(R.color.primary_color));

    }

}
