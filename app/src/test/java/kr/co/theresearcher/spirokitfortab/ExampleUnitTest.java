package kr.co.theresearcher.spirokitfortab;

import org.junit.Test;

import static org.junit.Assert.*;

import android.os.SystemClock;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Array;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalField;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import kr.co.theresearcher.spirokitfortab.db.human_race.HumanRace;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {

        List<Integer> data = new ArrayList<>();
        String stringData = "";

        List<Integer> tempData = new ArrayList<>();
        String tempString = "";

        for (int i = 0; i < 100000; i++) {
            data.add(123456789);
            stringData += 123456789 + " ";
        }


        long before = System.currentTimeMillis();
        System.out.println("BEFORE INPUT STRING : " + before);

        for (int i = 0; i < data.size(); i++) {
            tempString += data.get(i) + " ";
        }

        long after = System.currentTimeMillis();
        System.out.println("AFTER INPUT STRING : " + after);
        System.out.println("DIFF : " + (((after - before))) + " 밀리초");


        before = System.currentTimeMillis();
        System.out.println("BEFORE PARSE STRING : " + before);

        String[] strings = stringData.split(" ");

        tempData.clear();
        for (int i = 0; i < strings.length; i++) {

            tempData.add(Integer.parseInt(strings[i]));

        }

        after = System.currentTimeMillis();
        System.out.println("AFTER PARSE STRING : " + after);
        System.out.println("DIFF : " + (((after - before))) + " 밀리초");

        before = System.currentTimeMillis();
        System.out.println("BEFORE INPUT CHARACTERS : " + before);

        for (int i = 0; i < data.size(); i++) {

            tempString += data.get(i) / Character.MAX_VALUE;
            tempString += data.get(i) % Character.MAX_VALUE;

        }

        after = System.currentTimeMillis();
        System.out.println("AFTER INPUT CHARACTERS : " + after);
        System.out.println("DIFF : " + (((after - before))) + " 밀리초");


        tempData.clear();

        before = System.currentTimeMillis();
        System.out.println("BEFORE PARSE CHARACTERS : " + before);

        for (int i = 0; i < tempString.length(); i += 2) {

            tempData.add((tempString.charAt(i) * Character.MAX_VALUE) + (tempString.charAt(i + 1)));

        }

        after = System.currentTimeMillis();
        System.out.println("AFTER PARSE CHARACTERS : " + after);
        System.out.println("DIFF : " + (((after - before))) + " 밀리초");


        System.out.println("===========RESULT");
        for (int i = 0; i < data.size(); i++) {
            System.out.print(data.get(i) + ", ");
        }

        System.out.println();

        for (int i = 0; i < tempData.size(); i++) {
            System.out.print(tempData.get(i) + ", ");
        }

        /*
        String dateString = "2022-07-12 09:58:26.453264";
        String subString = dateString.substring(0, dateString.length() - 7);
        System.out.println(subString);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        try {
            long time = simpleDateFormat.parse(subString).getTime();
            SimpleDateFormat resultFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            System.out.println(resultFormat.format(time));
        } catch (ParseException e) {

        }


         */

        /*
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");
        long date = dateTimeFormatter.parse(dateString)
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        System.out.println(simpleDateFormat.format(date));

         */

        //long date = -1000000000;
        //SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd", Locale.getDefault());
        //System.out.println(simpleDateFormat.format(date));

        /*
        Instant instant = Instant.now().truncatedTo(ChronoUnit.MICROS);xc
        System.out.println(instant.toString());
        long date = instant.toEpochMilli();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.getDefault());
        System.out.println(simpleDateFormat.format(date));


         */
        /*
        String string = "1909403 ";
        byte[] data = string.getBytes();

        int value = 0;

        for (byte b : data) {

            System.out.println(b);

            if ((b >= 0x30) && (b <= 0x39)) {
                value *= 10;
                value += b - 0x30;
            }

        }
        System.out.println(value);


         */
        /*

        String data = "dkfjdkj dkjfdkjfkd jfkdjkfdjkf fjdkfjkdfj ";
        String[] splitData = data.split(" ");

        for (String d : splitData) System.out.println(d);

         */

        /*
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd");

        try {

            long from = simpleDateFormat.parse("2020.01.07").getTime();
            long to = simpleDateFormat.parse("2022.07.10").getTime();
            Date fromDate = new Date(from);
            Date toDate = new Date(to);

            Calendar fromCal = Calendar.getInstance();
            fromCal.setTime(fromDate);

            Calendar toCal = Calendar.getInstance();
            toCal.setTime(toDate);

            int diffYear = toCal.get(Calendar.YEAR) - fromCal.get(Calendar.YEAR);
            int diffMonth = toCal.get(Calendar.MONTH) - fromCal.get(Calendar.MONTH);

            System.out.println("FROM : " + fromCal.get(Calendar.YEAR) + "." + fromCal.get(Calendar.MONTH));
            System.out.println("TO : " + toCal.get(Calendar.YEAR) + "." + toCal.get(Calendar.MONTH));

            int result = (diffYear * 12) + diffMonth;
            System.out.println("RESULT DIFF : " + result);

        } catch (ParseException e) {

            System.out.println(e.toString());
        }

         */


    }
}