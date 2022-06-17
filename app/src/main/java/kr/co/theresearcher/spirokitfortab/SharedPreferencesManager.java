package kr.co.theresearcher.spirokitfortab;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Calendar;

public class SharedPreferencesManager {

    private static final String SHARED_PREFERENCES_NAME =
            "kr.co.theresearcher.thespirokit.sharedpreferences";
    public static final String LOGGED_IN_USER =
            "kr.co.theresearcher.logged.in.user";
    public static final String INPUT_EMAIL_ADDRESS =
            "kr.co.theresearcher.input.eamil";
    public static final String INPUT_USER_PASSWORD =
            "kr.co.theresearcher.user.input.password";
    public static final String USER_NICKNAME =
            "kr.co.theresearcher.user.nickname";

    public static final String INPUT_PATIENT_ =
            "kr.co.theresearcher.preferences.patient.";

    public static final String CONNECTED_MAC_ADDRESS =
            "kr.co.theresearcher.preferences.mac.address";

    public static final String IS_CONNECT =
            "kr.co.theresearcher.is.connect";

    public static final String OFFICE_ID =
            "kr.co.theresearcher.office.id";


    public static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public static boolean setLoggedInUserId(Context context, int id) {
        SharedPreferences sharedPreferences = getPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(LOGGED_IN_USER, id);
        return editor.commit();
    }

    public static int getLoggedInUserId(Context context) {
        return getPreferences(context).getInt(LOGGED_IN_USER, 0);
    }

    public static boolean setInputEmail(Context context, String email) {
        SharedPreferences sharedPreferences = getPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(INPUT_EMAIL_ADDRESS, email);
        return editor.commit();
    }

    public static String getInputEmail(Context context) {
        return getPreferences(context).getString(INPUT_EMAIL_ADDRESS, "");
    }

    public static boolean setInputNickname(Context context, String nickname) {
        SharedPreferences sharedPreferences = getPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USER_NICKNAME, nickname);
        return editor.commit();
    }

    public static String getInputNickname(Context context) {
        return getPreferences(context).getString(USER_NICKNAME, "");
    }

    public static boolean setInputPassword(Context context, String password) {
        SharedPreferences sharedPreferences = getPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(INPUT_USER_PASSWORD, password);
        return editor.commit();
    }

    public static String getInputUserPassword(Context context) {
        return getPreferences(context).getString(INPUT_USER_PASSWORD, "");
    }

    public static boolean setPatientId(Context context, int id) {

        SharedPreferences sharedPreferences = getPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(INPUT_PATIENT_ + "id", id);
        return editor.commit();
    }

    public static int getPatientId(Context context) {
        return getPreferences(context).getInt(INPUT_PATIENT_ + "id", 0);
    }

    public static boolean setPatientName(Context context, String name) {

        SharedPreferences sharedPreferences = getPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(INPUT_PATIENT_ + "name", name);
        return editor.commit();
    }

    public static String getPatientName(Context context) {
        return getPreferences(context).getString(INPUT_PATIENT_ + "name", "");
    }

    public static boolean setPatientChartNumber(Context context, String chartNumber) {

        SharedPreferences sharedPreferences = getPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(INPUT_PATIENT_ + "chart.number", chartNumber);
        return editor.commit();
    }

    public static String getPatientChartNumber(Context context) {
        return getPreferences(context).getString(INPUT_PATIENT_ + "chart.number", "");
    }

    public static boolean setPatientGender(Context context, boolean gender) {

        SharedPreferences sharedPreferences = getPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(INPUT_PATIENT_ + "gender", gender);
        return editor.commit();
    }

    public static boolean getPatientGender(Context context) {
        return getPreferences(context).getBoolean(INPUT_PATIENT_ + "gender", true);
    }

    public static boolean setPatientIsSmoking(Context context, boolean isSmoking) {

        SharedPreferences sharedPreferences = getPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(INPUT_PATIENT_ + "is.smoking", isSmoking);
        return editor.commit();
    }

    public static boolean getPatientIsSmoking(Context context) {
        return getPreferences(context).getBoolean(INPUT_PATIENT_ + "is.smoking", true);
    }

    public static boolean setPatientHumanRaceId(Context context, int humanRaceId) {

        SharedPreferences sharedPreferences = getPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(INPUT_PATIENT_ + "human.race.id", humanRaceId);
        return editor.commit();
    }

    public static int getPatientHumanRaceId(Context context) {
        return getPreferences(context).getInt(INPUT_PATIENT_ + "human.race.id", 0);
    }

    public static boolean setPatientHeight(Context context, int height) {

        SharedPreferences sharedPreferences = getPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(INPUT_PATIENT_ + "height", height);
        return editor.commit();
    }

    public static int getPatientHeight(Context context) {
        return getPreferences(context).getInt(INPUT_PATIENT_ + "height", 0);
    }

    public static boolean setPatientWeight(Context context, int weight) {

        SharedPreferences sharedPreferences = getPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(INPUT_PATIENT_ + "weight", weight);
        return editor.commit();
    }

    public static int getPatientWeight(Context context) {
        return getPreferences(context).getInt(INPUT_PATIENT_ + "weight", 0);
    }

    public static boolean setPatientBirth(Context context, long birth) {
        SharedPreferences sharedPreferences = getPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(INPUT_PATIENT_ + "birth", birth);
        return editor.commit();
    }

    public static long getPatientBirth(Context context) {
        return getPreferences(context).getLong(INPUT_PATIENT_ + "birth", Calendar.getInstance().getTime().getTime());
    }

    public static boolean setPatientStartSmokingDate(Context context, long startSmoking) {
        SharedPreferences sharedPreferences = getPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(INPUT_PATIENT_ + "start.smoking", startSmoking);
        return editor.commit();
    }

    public static long getPatientStartSmokingDate(Context context) {
        return getPreferences(context).getLong(INPUT_PATIENT_ + "start.smoking", Calendar.getInstance().getTime().getTime());
    }

    public static boolean setPatientNoSmokingDate(Context context, long noSmoking) {
        SharedPreferences sharedPreferences = getPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(INPUT_PATIENT_ + "no.smoking", noSmoking);
        return editor.commit();
    }

    public static long getPatientNoSmokingDate(Context context) {
        return getPreferences(context).getLong(INPUT_PATIENT_ + "no.smoking", Calendar.getInstance().getTime().getTime());
    }

    public static boolean setPatientSmokingAmount(Context context, float pack) {
        SharedPreferences sharedPreferences = getPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(INPUT_PATIENT_ + "pack", pack);
        return editor.commit();
    }

    public static int getPatientSmokingAmount(Context context) {
        return getPreferences(context).getInt(INPUT_PATIENT_ + "pack", 0);
    }

    public static boolean setBluetoothDeviceMacAddress(Context context, String address) {
        SharedPreferences sharedPreferences = getPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();;
        editor.putString(CONNECTED_MAC_ADDRESS, address);
        return editor.commit();
    }

    public static String getBluetoothDeviceMacAddress(Context context) {
        return getPreferences(context).getString(CONNECTED_MAC_ADDRESS, "");
    }

    public static boolean setConnectState(Context context, boolean isConnect) {
        SharedPreferences sharedPreferences = getPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(IS_CONNECT, isConnect);
        return editor.commit();
    }

    public static boolean getConnectState(Context context) {
        return getPreferences(context).getBoolean(IS_CONNECT, false);
    }

    public static int getOfficeID(Context context) {
        return getPreferences(context).getInt(OFFICE_ID, 0);
    }

    public static boolean setOfficeID(Context context, int id) {
        SharedPreferences sharedPreferences = getPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(OFFICE_ID, id);
        return editor.commit();
    }

}
