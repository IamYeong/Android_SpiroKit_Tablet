package kr.co.theresearcher.spirokitfortab.graph;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

import kr.co.theresearcher.spirokitfortab.R;

public class GradationView extends View {

    private Paint paint;
    private int portion = 0;
    private float width;

    private RectF rect;

    public GradationView(Context context) {
        super(context);
        rect = new RectF();
        paint = new Paint();
        setPaint();
    }

    public void setPortion(float width, float height, int number) {
        this.width = width;
        this.portion = number;

    }

    @Override
    protected void onDraw(Canvas canvas) {
        //super.onDraw(canvas);

        float widthPadding = width / (float) portion;

        for (int i = 1; i < portion; i++) {
            rect.set((widthPadding * (float) i), 0f, (widthPadding * (float) i) + 3f, 20f);
            canvas.drawRect(rect, paint);
        }

    }

    private void setPaint() {

        paint.setColor(getContext().getColor(R.color.secondary_color));
        paint.setAntiAlias(true);

    }



}
