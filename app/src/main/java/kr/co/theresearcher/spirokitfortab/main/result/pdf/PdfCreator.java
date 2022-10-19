package kr.co.theresearcher.spirokitfortab.main.result.pdf;

import android.content.Context;
import android.graphics.Canvas;

/**
 * 222.10.18 검사 종류에 따른 그래프를 A4사이즈 PDF canvas 에 그리는 인터페이스
 */
public interface PdfCreator {

    Canvas drawPDF(Context context, Canvas canvas);

    float measureGap(float unit, int limit, float max, float min);

    float gapToSizeX(float width, float max, float min, float gap) ;

    float gapToSizeY(float height, float max, float min, float gap);

    float xToPosition(float width, float max, float min, float x);

    float yToPosition(float height, float max, float min, float y);
}
