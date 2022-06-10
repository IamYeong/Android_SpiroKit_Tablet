package kr.co.theresearcher.spirokitfortab.graph;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

import kr.co.theresearcher.spirokitfortab.R;

public class ProgressTextView extends View {

    private Paint paint;
    private float width, height;
    private int portion;
    private int cursor = 0;
    private int[] visibleNumbers;

    public ProgressTextView(Context context) {
        super(context);

        paint = new Paint();
        setPaint();


    }

    @Override
    protected void onDraw(Canvas canvas) {
        //super.onDraw(canvas);

        float widthPadding = width / (float)portion;

        for (int i = 1; i <= portion; i++) {

            boolean isVisible = false;

            for (int n : visibleNumbers) {

                if (n == i) {
                    isVisible = true;
                    break;
                }

            }

            if (isVisible) {


                String text = i + "초";
                canvas.drawText(text, (widthPadding * i) - 15f, 30f, paint);



            }


        }


    }

    public void setPortion(float width, float height, int number) {
        this.width = width;
        this.height = height;
        this.portion = number;
        visibleNumbers = new int[number];
    }

    public void setVisibleNumber(int num) {
        visibleNumbers[cursor++] = num;
    }

    public void setTextColor(int color) {
        paint.setColor(color);
    }

    private void setPaint() {

        paint.setTextSize(35f);
        paint.setColor(getContext().getColor(R.color.white));
        paint.setAntiAlias(true);
    }


}
