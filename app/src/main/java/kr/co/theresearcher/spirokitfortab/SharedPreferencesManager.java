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

    private static final String CAL_HISTORY_HASH =
            "kr.co.theresearcher.cal.history.hash";

    private static final String PATIENT_ID = "kr.co.theresearcher.patient.id";
    private static final String PATIENT_HASHED = "kr.co.theresearcher.patient.hashed";
    private static final String PATIENT_OFFICE_HASHED = "kr.co.theresearcher.patient.office.hashed";
    private static final String PATIENT_OPERATOR_WORK_HASHED = "kr.co.theresearcher.patient.operator.work.hashed";
    private static final String PATIENT_OPERATOR_DOCTOR_HASHED = "kr.co.theresearcher.patient.operator.doctor.hashed";
    private static final String PATIENT_CHART_NUMBER = "kr.co.theresearcher.patient.chart.number";
    private static final String PATIENT_NAME = "kr.co.theresearcher.patient.name";
    private static final String PATIENT_GENDER = "kr.co.theresearcher.patient.gender";
    private static final String PATIENT_HEIGHT = "kr.co.theresearcher.patient.height";
    private static final String PATIENT_WEIGHT = "kr.co.theresearcher.patient.weight";
    private static final String PATIENT_BIRTHDAY = "kr.co.theresearcher.patient.birthday";
    private static final String PATIENT_HUMAN_RACE = "kr.co.theresearcher.patient.human.race";
    private static final String PATIENT_SMOKING_AMOUNT_PER_DAY = "kr.co.theresearcher.patient.smoking.amount.per.day";
    private static final String PATIENT_SMOKING_PERIOD = "kr.co.theresearcher.patient.smoking.period";
    private static final String PATIENT_SMOKING_IS_NOW = "kr.co.theresearcher.patient.smoking.is.now";
    //private static final String PATIENT_SMOKING_NO_WHEN = "kr.co.theresearcher.patient.smoking.no.when";
    private static final String PATIENT_SMOKING_START_DATE = "kr.co.theresearcher.patient.smoking.start.date";
    private static final String PATIENT_SMOKING_STOP_DATE = "kr.co.theresearcher.patient.smoking.stop.date";
    private static final String PATIENT_LATEST_DAY = "kr.co.theresearcher.patient.latest.cal.date";
    //private static final String PATIENT_FROM_OS = "kr.co.theresearcher.patient.os";
    private static final String PATIENT_CREATE_DATE = "kr.co.theresearcher.patient.c.date";


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

    public static boolean setPatientID(Context context, int value) {
        SharedPreferences sharedPreferences = getPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(PATIENT_ID, value);
        return editor.commit();
    }

    public static boolean setPatientHash(Context context, String value) {
        SharedPreferences sharedPreferences = getPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PATIENT_HASHED, value);
        return editor.commit();
    }

    public static boolean setPatientOfficeHash(Context context, String value) {
        SharedPreferences sharedPreferences = getPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PATIENT_OFFICE_HASHED, value);
        return editor.commit();
    }

    public static boolean setPatientOperatorWorkHash(Context context, String value) {
        SharedPreferences sharedPreferences = getPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PATIENT_OPERATOR_WORK_HASHED, value);
        return editor.commit();
    }

    public static boolean setPatientOperatorDoctorHash(Context context, String value) {
        SharedPreferences sharedPreferences = getPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PATIENT_OPERATOR_DOCTOR_HASHED, value);
        return editor.commit();
    }

    public static boolean setPatientName(Context context, String value) {
        SharedPreferences sharedPreferences = getPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PATIENT_NAME, value);
        return editor.commit();
    }

    public static boolean setPatientChartNumber(Context context, String value) {
        SharedPreferences sharedPreferences = getPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PATIENT_CHART_NUMBER, value);
        return editor.commit();
    }

    public static boolean setPatientHeight(Context context, int value) {
        SharedPreferences sharedPreferences = getPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(PATIENT_HEIGHT, value);
        return editor.commit();
    }

    public static boolean setPatientWeight(Context context, int value) {
        SharedPreferences sharedPreferences = getPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(PATIENT_WEIGHT, value);
        return editor.commit();
    }

    public static boolean setPatientGender(Context context, String value) {
        SharedPreferences sharedPreferences = getPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PATIENT_GENDER, value);
        return editor.commit();
    }

    public static boolean setPatientHumanRace(Context context, String value) {
        SharedPreferences sharedPreferences = getPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PATIENT_HUMAN_RACE, value);
        return editor.commit();
    }

    public static boolean setPatientSmokingStartDate(Context context, String value) {
        SharedPreferences sharedPreferences = getPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PATIENT_SMOKING_START_DATE, value);
        return editor.commit();
    }

    public static boolean setPatientSmokingStopDate(Context context, String value) {
        SharedPreferences sharedPreferences = getPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PATIENT_SMOKING_STOP_DATE, value);
        return editor.commit();
    }

    public static boolean setPatientSmokingIsNow(Context context, int value) {
        SharedPreferences sharedPreferences = getPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(PATIENT_SMOKING_IS_NOW, value);
        return editor.commit();
    }

    public static boolean setPatientBirthday(Context context, String value) {
        SharedPreferences sharedPreferences = getPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PATIENT_BIRTHDAY, value);
        return editor.commit();
    }

    public static boolean setPatientLatestDay(Context context, String value) {
        SharedPreferences sharedPreferences = getPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PATIENT_LATEST_DAY, value);
        return editor.commit();
    }

    public static boolean setPatientSmokingAmountPerDay(Context context, String value) {
        SharedPreferences sharedPreferences = getPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PATIENT_SMOKING_AMOUNT_PER_DAY, value);
        return editor.commit();
    }

    public static boolean setPatientCreateDate(Context context, long value) {
        SharedPreferences sharedPreferences = getPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(PATIENT_CREATE_DATE, value);
        return editor.commit();
    }

    public static boolean setPatientSmokingPeriod(Context context, int value) {
        SharedPreferences sharedPreferences = getPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(PATIENT_SMOKING_PERIOD, value);
        return editor.commit();
    }


    public static String getDeviceMacAddress(Context context) { return getPreferences(context).getString(CONNECTED_MAC_ADDRESS, ""); }
    public static String getDeviceName(Context context) { return getPreferences(context).getString(CONNECTED_DEVICE_NAME, ""); }
    public static String getOfficeID(Context context) { return getPreferences(context).getString(OFFICE_ID, ""); }
    public static String getOfficePass(Context context) { return getPreferences(context).getString(OFFICE_PASS, ""); }
    public static String getOfficeName(Context context) { return getPreferences(context).getString(OFFICE_NAME, ""); }
    public static String getOfficeHash(Context context) { return getPreferences(context).getString(OFFICE_HASH, ""); }
    public static String getOperatorHash(Context context) { return getPreferences(context).getString(OPERATOR_HASH, ""); }
    public static String getCalHistoryHash(Context context) { return getPreferences(context).getString(CAL_HISTORY_HASH, ""); }

    public static int getPatientId(Context context) { return getPreferences(context).getInt(PATIENT_ID, -1); }
    public static String getPatientHashed(Context context) { return getPreferences(context).getString(PATIENT_HASHED, ""); }
    public static String getPatientOfficeHashed(Context context) { return getPreferences(context).getString(PATIENT_OFFICE_HASHED, ""); }
    public static String getPatientChartNumber(Context context) { return getPreferences(context).getString(PATIENT_CHART_NUMBER, ""); }
    public static String getPatientName(Context context) { return getPreferences(context).getString(PATIENT_NAME, ""); }
    public static String getPatientOperatorWorkHashed(Context context) { return getPreferences(context).getString(PATIENT_OPERATOR_WORK_HASHED, ""); }
    public static String getPatientOperatorDoctorHashed(Context context) { return getPreferences(context).getString(PATIENT_OPERATOR_DOCTOR_HASHED, ""); }
    public static String getPatientGender(Context context) { return getPreferences(context).getString(PATIENT_GENDER, "m"); }
    public static int getPatientHeight(Context context) { return getPreferences(context).getInt(PATIENT_HEIGHT, 0); }
    public static int getPatientWeight(Context context) { return getPreferences(context).getInt(PATIENT_WEIGHT, 0); }
    public static String getPatientSmokingStartDate(Context context) { return getPreferences(context).getString(PATIENT_SMOKING_START_DATE, null); }
    public static String getPatientSmokingStopDate(Context context) { return getPreferences(context).getString(PATIENT_SMOKING_STOP_DATE, null); }
    public static int getPatientSmokingIsNow(Context context) { return getPreferences(context).getInt(PATIENT_SMOKING_IS_NOW, 0); }
    public static String getPatientSmokingAmountPerDay(Context context) { return getPreferences(context).getString(PATIENT_SMOKING_AMOUNT_PER_DAY, null); }
    public static int getPatientSmokingPeriod(Context context) { return getPreferences(context).getInt(PATIENT_SMOKING_PERIOD, 0); }
    public static String getPatientBirthday(Context context) { return getPreferences(context).getString(PATIENT_BIRTHDAY, ""); }
    public static String getPatientHumanRace(Context context) { return getPreferences(context).getString(PATIENT_HUMAN_RACE, ""); }
    public static long getPatientCreateDate(Context context) { return getPreferences(context).getLong(PATIENT_CREATE_DATE, 0); }
    public static String getPatientLatestDay(Context context) { return getPreferences(context).getString(PATIENT_LATEST_DAY, ""); }



}
