package kr.co.theresearcher.spirokitfortab;

import org.junit.Test;

import static org.junit.Assert.*;

import android.os.SystemClock;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
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

        //이 방법 계산은 되고 포맷만 맞추면 됨
        Instant instant = Instant.now().truncatedTo(ChronoUnit.MICROS);
        System.out.println(instant.atZone(ZoneId.systemDefault()).toString());

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS")
                .withZone(ZoneId.systemDefault());

        String result = dateTimeFormatter.format(instant);

        System.out.println(result);

    }
}