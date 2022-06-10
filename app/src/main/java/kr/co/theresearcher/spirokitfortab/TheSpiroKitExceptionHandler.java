package kr.co.theresearcher.spirokitfortab;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Process;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.PrintWriter;
import java.io.StringWriter;

import kr.co.theresearcher.spirokitfortab.IntentAttributes;

public class TheSpiroKitExceptionHandler implements Thread.UncaughtExceptionHandler {

    private Context applicationContext;
    private Activity lastActivity = null;
    private int activityCount = 0;

    public TheSpiroKitExceptionHandler(Application application, Thread.UncaughtExceptionHandler defaultHandler) {

        //degaultHandler
        this.applicationContext = application.getApplicationContext();

        application.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {

            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {

                lastActivity = activity;
                activityCount++;
            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {

            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {

                activityCount--;

            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {

            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {

            }
        });


    }



    @Override
    public void uncaughtException(@NonNull Thread thread, @NonNull Throwable throwable) {

        StringWriter stringWriter = new StringWriter();
        throwable.printStackTrace(new PrintWriter(stringWriter));

        Intent intent = new Intent(applicationContext, ExceptionActivity.class);
        Intent lastIntent = new Intent(applicationContext, lastActivity.getClass());
        intent.putExtra(IntentAttributes.INTENT_NAME_LAST_ACTIVITY, lastIntent);
        intent.putExtra(IntentAttributes.INTENT_NAME_EXCEPTION, stringWriter.toString());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        applicationContext.startActivity(intent);

        Process.killProcess(Process.myPid());
        System.exit(-1);


    }
}
