package edu.vanderbilt.cs285.secure_sms;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.spec.RSAPublicKeySpec;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * Created by Jay on 11/30/2014.
 */
public class SymmetricEncryptor{
    private static final String TAG = SymmetricEncryptor.class.getName();

    private static boolean initialized = false;

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    private final static String SYMMETRIC_KEY = "SymmetricKey";
    private final static String DEFAULT_PREF = "";
    private static final String PREFS_MY_KEYS = "MyKeys";
    private static final int ivLength = 16;

    // generate a random secret key
    private static SecretKey generateRandomKey() throws NoSuchAlgorithmException {
        KeyGenerator generator = KeyGenerator.getInstance("AES");
        generator.init(128);
        return generator.generateKey();
    }



    public static byte[] encryptBytes(byte[] key, byte[] data) throws Exception {
        MessageDigest sha = MessageDigest.getInstance("SHA-256");

        byte[] hashedKey = sha.digest(key);

        // create a hashed secret key
        SecretKeySpec sk = new SecretKeySpec(hashedKey, "AES");
        // Create the cipher
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding", "BC");
        // Initialize the cipher for encryption
        cipher.init(Cipher.ENCRYPT_MODE, sk);
        // Encrypt the plaintext
        byte[] cipherText = cipher.doFinal(data);
        //append IV to the start of the message
        byte [] cipherTextwithIV = Arrays.copyOf(cipher.getIV(), ivLength + cipherText.length);
        System.arraycopy(cipherText, 0, cipherTextwithIV, ivLength, cipherText.length);
        return cipherTextwithIV;
    }


    public static  byte[] decryptBytes(byte[] key, byte[] cipherTextwithIV) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding", "BC");
        // Initialize the same cipher for decryption
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] hashedKey = sha.digest(key);
        SecretKeySpec sk = new SecretKeySpec(hashedKey, "AES");
        byte[] iv = Arrays.copyOfRange(cipherTextwithIV, 0, ivLength);
        byte[] cipherText = Arrays.copyOfRange(cipherTextwithIV, ivLength, cipherTextwithIV.length);
        cipher.init(Cipher.DECRYPT_MODE, sk, new IvParameterSpec(iv));
        // Decrypt the ciphertext
        return cipher.doFinal(cipherText);
    }


    public static boolean isInitialized() {
        return initialized;
    }


    public static String getRecipientsSymmetricKey(String contactNum,
                                                   Context context) {
        Log.w(TAG, "retrieving symmetric key for contact " + contactNum);
        SharedPreferences prefs = context.getSharedPreferences(contactNum,
                Context.MODE_PRIVATE);

        String symmetricKey = prefs.getString(SYMMETRIC_KEY, DEFAULT_PREF);
        byte[] symmetricKeyBytes = Base64.decode(symmetricKey, Base64.NO_WRAP);
        try {
            return new String(symmetricKeyBytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void saveSymmetricKey(String symmetricKey, String contactNum,
                                        Context context) {
        SharedPreferences prefs = context.getSharedPreferences(contactNum,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        String keyBase64Str = null;
        try {
            keyBase64Str = Base64.encodeToString(symmetricKey.getBytes("UTF-8"),
                    Base64.NO_WRAP);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        prefsEditor.putString(SYMMETRIC_KEY, keyBase64Str);
        prefsEditor.apply();
    }


    public static byte[] generateKey() throws Exception {
       return generateRandomKey().getEncoded();
    }
}



