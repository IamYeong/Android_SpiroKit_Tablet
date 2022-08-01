package kr.co.theresearcher.spirokitfortab;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Calendar;

import kr.co.theresearcher.spirokitfortab.db.human_race.HumanRace;
import kr.co.theresearcher.spirokitfortab.db.work.Work;

public class SharedPreferencesManager {

    private static final String SHARED_PREFERENCES_NAME =
            "kr.co.theresearcher.thespirokit.sharedpreferences";



    private static final String CONNECTED_MAC_ADDRESS =
            "kr.co.theresearcher.preferences.mac.address";

    private static final String CONNECTED_DEVICE_NAME =
            "kr.co.theresearcher.preferences.device.name";

    private static final String OFFICE_HASH =
            "kr.co.theresearcher.office.hash";

    private static final String OFFICE_ID =
            "kr.co.theresearcher.office.id";

    private static final String OFFICE_PASS =
            "kr.co.theresearcher.office.pass";

    private static final String OFFICE_NAME =
            "kr.co.theresearcher.office.name";

    private static final String OPERATOR_HASH =
            "kr.co.theresearcher.operator.hash";

    private static final String OPERATOR_FAMILY_DOCTOR =
            "kr.co.theresearcher.operator.family.doctor";

    private static final String CAL_HISTORY_HASH =
            "kr.co.theresearcher.cal.history.hash";

    private static final String PATIENT_HASHED =
            "kr.co.theresearcher.patient.hashed";

    private static final String USE_SYNC =
            "kr.co.theresearcher.sync.use";

    public static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public static boolean setDeviceMacAddress(Context context, String value) {

        SharedPreferences sharedPreferences = getPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(CONNECTED_MAC_ADDRESS, value);
        return editor.commit();

    }

    public static boolean setDeviceName(Context context, String value) {
        SharedPreferences sharedPreferences = getPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(CONNECTED_DEVICE_NAME, value);
        return editor.commit();
    }

    public static boolean setOfficeID(Context context, String value) {
        SharedPreferences sharedPreferences = getPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(OFFICE_ID, value);
        return editor.commit();
    }

    public static boolean setOfficePassword(Context context, String value) {
        SharedPreferences sharedPreferences = getPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(OFFICE_PASS, value);
        return editor.commit();
    }

    public static boolean setOfficeHashed(Context context, String value) {
        SharedPreferences sharedPreferences = getPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(OFFICE_HASH, value);
        return editor.commit();
    }

    public static boolean setOfficeName(Context context, String value) {
        SharedPreferences sharedPreferences = getPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(OFFICE_NAME, value);
        return editor.commit();
    }

    public static boolean setOperatorHash(Context context, String value) {
        SharedPreferences sharedPreferences = getPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(OPERATOR_HASH, value);
        return editor.commit();
    }

    public static boolean setHistoryHash(Context context, String value) {
        SharedPreferences sharedPreferences = getPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(CAL_HISTORY_HASH, value);
        return editor.commit();
    }

    public static boolean setPatientHash(Context context, String value) {
        SharedPreferences sharedPreferences = getPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PATIENT_HASHED, value);
        return editor.commit();
    }

    public static boolean setFamilyDoctorHash(Context context, String value) {
        SharedPreferences sharedPreferences = getPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(OPERATOR_FAMILY_DOCTOR, value);
        return editor.commit();
    }

    public static boolean setUseSync(Context context, boolean value) {
        SharedPreferences sharedPreferences = getPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(USE_SYNC, value);
        return editor.commit();
    }

    public static boolean getUseSync(Context context) { return getPreferences(context).getBoolean(USE_SYNC, false); }
    public static String getDeviceMacAddress(Context context) { return getPreferences(context).getString(CONNECTED_MAC_ADDRESS, null); }
    public static String getDeviceName(Context context) { return getPreferences(context).getString(CONNECTED_DEVICE_NAME, null); }
    public static String getOfficeID(Context context) { return getPreferences(context).getString(OFFICE_ID, null); }
    public static String getOfficePass(Context context) { return getPreferences(context).getString(OFFICE_PASS, null); }
    public static String getOfficeName(Context context) { return getPreferences(context).getString(OFFICE_NAME, null); }
    public static String getOfficeHash(Context context) { return getPreferences(context).getString(OFFICE_HASH, null); }
    public static String getOperatorHash(Context context) { return getPreferences(context).getString(OPERATOR_HASH, null); }
    public static String getFamilyDoctorHash(Context context) { return getPreferences(context).getString(OPERATOR_FAMILY_DOCTOR, null); }
    public static String getCalHistoryHash(Context context) { return getPreferences(context).getString(CAL_HISTORY_HASH, null); }
    public static String getPatientHashed(Context context) { return getPreferences(context).getString(PATIENT_HASHED, null); }

}
