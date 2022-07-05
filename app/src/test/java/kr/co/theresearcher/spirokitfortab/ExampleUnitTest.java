package kr.co.theresearcher.spirokitfortab;

import org.junit.Test;

import static org.junit.Assert.*;

import android.os.SystemClock;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import kr.co.theresearcher.spirokitfortab.db.human_race.HumanRace;
import kr.co.theresearcher.spirokitfortab.db.meas_group.MeasGroup;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {

        try {
            String hash = HashConverter.hashingFromString("홍길동2022-07-05 00:00:001f10668b5f1cb897d57faf08cfe58c668060f14ce32077c43011c862fea5f5c7");
            System.out.println(hash);
        } catch (NoSuchAlgorithmException e) {

        }

    }
}