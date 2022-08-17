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

        int data = 1542;
        System.out.println(dataLengthConvert9(data));

    }

    private String dataLengthConvert9(int data) {

        String value = "";
        int length = 0;

        while (true) {

            System.out.println(length);
            int ref = (int)Math.pow(10, length++);
            if (data < ref) break;

        }

        for (int i = 0; i <= 9 - length; i++) {
            value += "0";
        }

        value += Integer.toString(data);

        return value;
    }

}