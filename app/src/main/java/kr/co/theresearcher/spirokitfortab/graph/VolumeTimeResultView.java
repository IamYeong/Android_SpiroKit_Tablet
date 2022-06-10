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

        canvas.drawLine(0f, canvasHeight, canvasWidth, canvasHeight, linePaint);
        //Y 축 측면선
        canvas.drawLine(0f, canvasHeight, 0f, 0f, linePaint);

        //경로 그리기
        canvas.drawPath(path, pathPaint);


        for (int i = 1; i < xMarking; i++) {
            canvas.drawLine((float)xPadding * (float)i, canvasHeight, ((float)xPadding * (float)i), (canvasHeight) - 20f, linePaint); // 중앙에 x축 눈금 그리기
            canvas.drawText(Float.toString(Fluid.autoRound(1, ((float)xInterval * (float) i))), ((float)xPadding * (float)i) - 20f, (canvasHeight) - 25f, labelPaint);
            //canvas.drawText(Float.toString(Fluid.autoRound(1, maxX - (xInterval * (double) i))), (xPadding * (float)i) - 20f, (canvasHeight) - 25f, labelPaint);
        }

        for (int i = 1; i < yMarking; i++) {

            canvas.drawLine(0f, yPadding * (float)i, 20f, yPadding * (float)i,linePaint);
            canvas.drawText(Float.toString(Fluid.autoRound(1, (maxY - (yInterval * (double)i)))), 25f, (yPadding * (float)i) + 10f, labelPaint);


        }

    }

    //초기 설정이 끝나거나 setValue 후 값 조정이 끝났을 때 사용
    public void commit() {

        //System.out.println("COMMIT");

        xInterval = (maxX - minX) / (float)(xMarking);
        yInterval = (maxY - minY) / (float)(yMarking);

        xPadding = canvasWidth / (float)xMarking;
        yPadding = canvasHeight / (float)yMarking;

        this.x = (maxX - minX) * xStartPosition;
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
        values.add(new ResultCoordinate(x, y));

        if ((this.x + x) > maxX) {

            isOver = true;

            maxY *= (x + (maxX - minX)) / (maxX - minX);
            maxX += x;

        }

        if ((this.y + y) > maxY) {

            isOver = true;

            maxX *= (maxY + y) / maxY;
            maxY += y;

        }

        this.x += x;
        this.y += y;


    }

    public void apply() {

        this.x = 0f;
        this.y = 0f;
        commit();

        for (int i = 0; i < values.size(); i++) {

            this.x += values.get(i).getX();
            this.y += values.get(i).getY();

            path.lineTo(xToPosition(this.x), yToPosition(this.y));

        }

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
