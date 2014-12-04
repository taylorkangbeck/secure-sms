package edu.vanderbilt.cs285.secure_sms;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * Created by Jay on 11/30/2014.
 */
public class SymmetricEncryptor implements Encryptor {
    private static final String TAG = SymmetricEncryptor.class.getName();

    private static boolean initialized = false;

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    private static byte[] iv;
    private static byte[] symmetricKey;
    private static SecretKeySpec aesKeySpec;
    private final static String SYMMETRIC_KEY = "SymmetricKey";
    private final static String DEFAULT_PREF = "";
    private static final String PREFS_MY_KEYS = "MyKeys";

    private SymmetricEncryptor() throws Exception {
        symmetricKey = generateRandomKey().getEncoded();
        initialized = true;
    }

    // generate a random secret key
    private static SecretKey generateRandomKey() throws NoSuchAlgorithmException {
        KeyGenerator generator = KeyGenerator.getInstance("AES");
        generator.init(128);
        return generator.generateKey();
    }

    public byte[] encryptBytes(byte[] plainText) throws Exception {

        // create a digest for key
        MessageDigest sha = MessageDigest.getInstance("SHA-256");

        byte[] hashedKey = sha.digest(symmetricKey);

        // create a hashed secret key
        SecretKeySpec sk = new SecretKeySpec(hashedKey, "AES");
        // Create the cipher
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding", "BC");
        // Initialize the cipher for encryption
        cipher.init(Cipher.ENCRYPT_MODE, sk);
        iv = cipher.getIV();
        aesKeySpec = sk;
        // Encrypt the plaintext
        return cipher.doFinal(plainText);
    }

    public static byte[] encryptBytes(byte[] key, byte[] data) throws Exception {
        symmetricKey = key;
        MessageDigest sha = MessageDigest.getInstance("SHA-256");

        byte[] hashedKey = sha.digest(symmetricKey);

        // create a hashed secret key
        SecretKeySpec sk = new SecretKeySpec(hashedKey, "AES");
        // Create the cipher
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding", "BC");
        // Initialize the cipher for encryption
        cipher.init(Cipher.ENCRYPT_MODE, sk);
        iv = cipher.getIV();
        aesKeySpec = sk;
        // Encrypt the plaintext
        return cipher.doFinal(data);
    }

    public byte[] decryptBytes(byte[] cipherText) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding", "BC");
        // Initialize the same cipher for decryption
        cipher.init(Cipher.DECRYPT_MODE, aesKeySpec, new IvParameterSpec(iv));
        // Decrypt the ciphertext
        return cipher.doFinal(cipherText);
    }

    public static  byte[] decryptBytes(byte[] key, byte[] cipherText) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding", "BC");
        // Initialize the same cipher for decryption
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] hashedKey = sha.digest(key);
        SecretKeySpec sk = new SecretKeySpec(hashedKey, "AES");
        cipher.init(Cipher.DECRYPT_MODE, sk, new IvParameterSpec(iv));
        // Decrypt the ciphertext
        return cipher.doFinal(cipherText);
    }


    public static boolean isInitialized() {
        return initialized;
    }

    public byte[] getKey() {
        return symmetricKey;
    }

    public static String getRecipientsSymmetricKey(String contactNum,
                                                   Context context) {
        Log.w(TAG, "retrieving symmetric key for contact " + contactNum);
        SharedPreferences prefs = context.getSharedPreferences(contactNum,
                Context.MODE_PRIVATE);

        String symmetricKey = prefs.getString(SYMMETRIC_KEY, DEFAULT_PREF);
        return symmetricKey;
    }

    public static void saveSymmetricKey(String symmetricKey, Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_MY_KEYS,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = prefs.edit();

        prefsEditor.putString(SYMMETRIC_KEY, symmetricKey);
        prefsEditor.apply();
    }


    public static byte[] generateKey() throws Exception {
       return generateRandomKey().getEncoded();
    }
}



