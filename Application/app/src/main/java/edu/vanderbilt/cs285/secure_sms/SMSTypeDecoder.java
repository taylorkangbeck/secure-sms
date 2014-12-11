package edu.vanderbilt.cs285.secure_sms;

import android.util.Base64;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Jay on 12/3/2014.
 */
class SMSTypeDecoder {

    private static final int HASH_ITERATIONS = 5;
    private static final int PREFIX_BYTES    = 3;
    public  static final int PREFIX_SIZE     = 4;

    public static boolean isInitKeyExchange(String message) {
        return verifyPrefix("?TSK", message);
    }

    public static boolean isReplyKeyExchange(String message) {
        return verifyPrefix("?TRK", message);
    }

    public static boolean isEncryptedMessage(String message) {
        return verifyPrefix("?TSM", message);
    }

    public static boolean isSymmetricKey(String message) {
        return verifyPrefix("?TSP", message);
    }

    public static boolean isEndSession(String message) {
        return verifyPrefix("?TSE", message);
    }

    public static String calculateReplyKeyExchangePrefix(String message){
        return calculatePrefix(("?TRK" + message).getBytes(), PREFIX_BYTES);
    }
    public static String calculateKeyExchangePrefix(String message) {
        return calculatePrefix(("?TSK" + message).getBytes(), PREFIX_BYTES);
    }

    public static String calculateEncryptedMesagePrefix(String message) {
        return calculatePrefix(("?TSM" + message).getBytes(), PREFIX_BYTES);
    }

    public static String calculateEncryptedKeyPrefix(String message) {
        return calculatePrefix(("?TSP" + message).getBytes(), PREFIX_BYTES);
    }

    public static String calculateEndSessionPrefix(String message) {
        return calculatePrefix(("?TSE" + message).getBytes(), PREFIX_BYTES);
    }

    private static boolean verifyPrefix(String prefixType, String message) {
        if (message.length() <= PREFIX_SIZE)
            return false;

        String prefix           = message.substring(0, PREFIX_SIZE);
        message                 = message.substring(PREFIX_SIZE);

        String calculatedPrefix = calculatePrefix((prefixType + message).getBytes(), PREFIX_BYTES);

        assert(calculatedPrefix.length() == PREFIX_SIZE);

        return prefix.equals(calculatedPrefix);
    }

    private static String calculatePrefix(byte[] message, int byteCount) {
        try {
            MessageDigest md     = MessageDigest.getInstance("SHA1");
            byte[] runningDigest = message;

            for (int i=0;i<HASH_ITERATIONS;i++) {
                runningDigest = md.digest(runningDigest);
            }
            return Base64.encodeToString(runningDigest, 0, byteCount, Base64.NO_WRAP);
        } catch (NoSuchAlgorithmException e) {
            throw new AssertionError(e);
        }
    }
}
