package kr.co.theresearcher.spirokitfortab;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashConverter {

    public static String hashingFromString(String text) throws NoSuchAlgorithmException {

        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] hash = messageDigest.digest(text.getBytes(StandardCharsets.UTF_8));

            StringBuilder stringBuilder = new StringBuilder();

            for (int i = 0; i < hash.length; i++) {

                String hex = Integer.toHexString(0xFF & hash[i]);
                if (hex.length() == 1) stringBuilder.append("0");
                stringBuilder.append(hex);

            }

            return stringBuilder.toString();

        } catch (NoSuchAlgorithmException e) {

            throw new NoSuchAlgorithmException();

        }

    }

}
