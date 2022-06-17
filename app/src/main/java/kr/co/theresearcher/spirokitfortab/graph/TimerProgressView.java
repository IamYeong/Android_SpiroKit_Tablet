package kr.co.theresearcher.spirokitfortab.graph;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

import kr.co.theresearcher.spirokitfortab.R;

public class TimerProgressView extends View {

    private boolean[] visibleLabels;
    private boolean[] visibleLines;
    private float max;
    private float interval;
    private float padding;
    private int number;
    private float canvasWidth, canvasHeight;

    private Paint labelPaint, linePaint;

    public TimerProgressView(Context context) {
        super(context);

        labelPaint = new Paint();
        linePaint = new Paint();

        setLabelPaint();
        setLinePaint();

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (int i = 1; i < number; i++) {

            if (visibleLines[i]) {
                canvas.drawLine((padding * i), 0f, (padding * i), 15f, linePaint);
            }

            if (visibleLabels[i]) {
                //체크 유니코드로 바꾼 뒤 다시 그리면 6초 표시 완료.
                canvas.drawText(Integer.toString((int)(interval * (float)i)) + "초", (padding * i) - 5f, canvasHeight - 3f, labelPaint);
            }

        }

    }

    public void setSize(int width, int height) {
        this.canvasWidth = width;
        this.canvasHeight = height;
    }

    public void setMax(float value) {
        this.max = value;
    }

    public void setNumber(int num) {
        this.number = num;
        visibleLabels = new boolean[num];
        visibleLines = new boolean[num];
    }

    public void commit() {

        padding = canvasWidth / number;
        interval = max / (float)number;

    }

    public void setInvisibleLines(int... index) {

        for(int i = 0; i < number; i++) {
            visibleLines[i] = true;
        }

        for (int i : index) {
            visibleLines[i] = false;
        }

    }

    public void setInvisibleLabels(int... index) {

        for(int i = 0; i < number; i++) {
            visibleLabels[i] = true;
        }

        for (int i : index) {
            visibleLabels[i] = false;
        }
    }

    private void setLabelPaint() {

        labelPaint.setColor(getContext().getColor(R.color.secondary_color));
        labelPaint.setTextSize(20f);

    }

    private void setLinePaint() {

        linePaint.setColor(getContext().getColor(R.color.secondary_color));
        linePaint.setStrokeWidth(2f);

    }

}
