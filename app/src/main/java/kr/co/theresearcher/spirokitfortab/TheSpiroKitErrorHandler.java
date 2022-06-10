package kr.co.theresearcher.spirokitfortab;

import android.content.Context;
import android.content.Intent;
import android.os.Process;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;

import kr.co.theresearcher.spirokitfortab.IntentAttributes;

public class TheSpiroKitErrorHandler implements ErrorListener {

    private Context applicationContext;

    public TheSpiroKitErrorHandler(Context context) {

        this.applicationContext = context;

    }

    @Override
    public void warning(TransformerException e) throws TransformerException {
        handleError(e);
    }

    @Override
    public void error(TransformerException e) throws TransformerException {
        handleError(e);
    }

    @Override
    public void fatalError(TransformerException e) throws TransformerException {
        handleError(e);
    }

    private void handleError(TransformerException e) {

        StringWriter stringWriter = new StringWriter();
        e.printStackTrace(new PrintWriter(stringWriter));

        Intent intent = new Intent(applicationContext, ExceptionActivity.class);
        //Intent lastIntent = new Intent(applicationContext, lastActivity.getClass());
        //intent.putExtra("EXCEPTION.INTENT", lastIntent);
        intent.putExtra(IntentAttributes.INTENT_NAME_EXCEPTION, stringWriter.toString());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        applicationContext.startActivity(intent);

        Process.killProcess(Process.myPid());
        System.exit(-1);

    }

}
