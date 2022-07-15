package kr.co.theresearcher.spirokitfortab;

import android.app.Application;

import kr.co.theresearcher.spirokitfortab.volley.SpiroKitVolley;

public class SpiroKitTabApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Thread.UncaughtExceptionHandler defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(new TheSpiroKitExceptionHandler(this, defaultHandler));
        TheSpiroKitErrorHandler errorHandler = new TheSpiroKitErrorHandler(getApplicationContext());
        SpiroKitVolley.createRequestQueue(getApplicationContext());


    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
