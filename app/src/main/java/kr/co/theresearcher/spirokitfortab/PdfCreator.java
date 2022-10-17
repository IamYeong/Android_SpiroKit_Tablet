package kr.co.theresearcher.spirokitfortab;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

//빈 도화지인 canvas 를 매개변수로 받아 A4 사이즈로 그려서 출력하는 클래스
public class PdfCreator {

    public static float A4_WIDTH = 545f; //210mm to pt
    public static float A4_HEIGHT = 841f; //297mm to pt

    public static float getA4Width() {
        return A4_WIDTH;
    }

    public static float getA4Height() {
        return A4_HEIGHT;
    }

    //2022.10.17 에 그리기 시작한 1차 PDF 문서(FVC)
    public static void drawFvcPdf(Context context, Canvas canvas) {

        Paint paint = new Paint();

        Bitmap logoImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.tr_logo); // 이미지 비트맵화
        Bitmap fitImage = Bitmap.createScaledBitmap(logoImage, 40, 30, false); // 사이즈 조정

        canvas.drawBitmap(fitImage, 20, 20, paint); // 이미지 그리기

        paint.setColor(Color.BLACK); // line color
        canvas.drawLine(20f,20f, 20f, A4_WIDTH - 20f, paint);
        canvas.drawLine(20f, 20f, A4_WIDTH - 20f, 20f, paint);
        canvas.drawLine(A4_WIDTH - 20f, 20f, A4_WIDTH - 20f, A4_HEIGHT - 20f, paint);
        canvas.drawLine(20f, A4_HEIGHT - 20f, A4_WIDTH - 20f, A4_HEIGHT - 20f, paint);




    }

    //2022.10.17 에 그리기 시작한 1차 PDF 문서(SVC)
    public static void drawSvcPdf(Canvas canvas) {

        Paint paint = new Paint();

    }


}
