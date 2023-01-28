package utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public abstract class ToolUtils {
    private static String byte2Hex(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder();
        String temp;
        for (byte aByte : bytes) {
            temp = Integer.toHexString(aByte & 0xFF);
            if (temp.length() == 1) {
                //1得到一位的进行补0操作
                stringBuilder.append("0");
            }
            stringBuilder.append(temp);
        }
        return stringBuilder.toString();
    }

    public static String getSha256Str(String str) throws NoSuchAlgorithmException {
        MessageDigest messageDigest;
        String encodeStr;
        messageDigest = MessageDigest.getInstance("SHA-256");
        final String salt = "online video website";
        str += salt;
        messageDigest.update(str.getBytes(StandardCharsets.UTF_8));
        encodeStr = byte2Hex(messageDigest.digest());
        return encodeStr;
    }
}
