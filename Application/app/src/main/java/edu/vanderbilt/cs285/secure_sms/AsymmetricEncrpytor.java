package edu.vanderbilt.cs285.secure_sms;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.Cipher;

/**
 * Created by Jay on 11/30/2014.
 */
public class AsymmetricEncrpytor {

    private final static String TAG = AsymmetricEncrpytor.class.getName();
    //shared prefs for key storage
    public static final String PREFS_MY_KEYS = "MyKeys";

    private static final String PREF_PUBLIC_MOD = "PublicModulus";
    private static final String PREF_PUBLIC_EXP = "PublicExponent";
    private static final String PREF_PRIVATE_MOD = "PrivateModulus";
    private static final String PREF_PRIVATE_EXP = "PrivateExponent";

    private static final String DEFAULT_PREF = "";

    //generate RSA keypair information
    public static void init(Context context) throws Exception {
        KeyPairGenerator kpg= KeyPairGenerator.getInstance("RSA");
        SecureRandom random = new SecureRandom();
        kpg.initialize(1024, random);
        KeyPair kp = kpg.generateKeyPair();
        PublicKey publicKey = kp.getPublic();
        PrivateKey privateKey = kp.getPrivate();
        KeyFactory fact = KeyFactory.getInstance("RSA");
        RSAPublicKeySpec pub = fact.getKeySpec(publicKey,
                RSAPublicKeySpec.class);
        RSAPrivateKeySpec priv = fact.getKeySpec(privateKey,
                RSAPrivateKeySpec.class);

        savePublicKey(pub, context);

        savePrivateKey(priv, context);

    }

    //RSA encryption
    public static byte[] encryptBytes(byte[] data, RSAPublicKeySpec recipientsPubKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        KeyFactory keyFactory=KeyFactory.getInstance("RSA");
        PublicKey publicKey=keyFactory.generatePublic(recipientsPubKey);
        cipher.init(Cipher.ENCRYPT_MODE,publicKey );
        return cipher.doFinal(data);
    }


    public static byte[] decryptBytes(byte[] data, RSAPrivateKeySpec recipientsPrivKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        KeyFactory keyFactory=KeyFactory.getInstance("RSA");
        PublicKey publicKey=keyFactory.generatePublic(recipientsPrivKey);
        cipher.init(Cipher.ENCRYPT_MODE,publicKey );
        return cipher.doFinal(data);
    }


    /*
     * get the modulus from sharedpreferences
     */
    public static String getPubMod(String contactID, Context context) {
        SharedPreferences prefs = context.getSharedPreferences(contactID,
                Context.MODE_PRIVATE);

        return prefs.getString(PREF_PUBLIC_MOD, DEFAULT_PREF);


    }

    /*
     * get the modulus from sharedpreferences
     */
    public static String getPubExp(String contactID, Context context) {
        SharedPreferences prefs = context.getSharedPreferences(contactID,
                Context.MODE_PRIVATE);

        return prefs.getString(PREF_PUBLIC_EXP, DEFAULT_PREF);

    }

    protected static RSAPublicKeySpec getPublicKeySpec(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_MY_KEYS,
                Context.MODE_PRIVATE);

        String pubMod = prefs.getString(PREF_PUBLIC_MOD, DEFAULT_PREF);
        String pubExp = prefs.getString(PREF_PUBLIC_EXP, DEFAULT_PREF);
        // String recipient = prefs.getString(PREF_RECIPIENT_NUM, DEFAULT_PREF);
        if (!pubMod.isEmpty() && !pubExp.isEmpty()) {
            byte[] pubModBA = Base64.decode(pubMod, Base64.DEFAULT);
            byte[] pubExpBA = Base64.decode(pubExp, Base64.DEFAULT);
            BigInteger pubModBI = new BigInteger(pubModBA);
            BigInteger pubExpBI = new BigInteger(pubExpBA);

           return new RSAPublicKeySpec(pubModBI,
                    pubExpBI);
        }
        Log.w(TAG, "public key not generated");
        return null;
    }

    public static RSAPublicKeySpec getRecipientsPublicKey(String contactNum,
                                                          Context context) {
        Log.w(TAG, "retrieving public key for contact " + contactNum);
        SharedPreferences prefs = context.getSharedPreferences(contactNum,
                Context.MODE_PRIVATE);

        String pubMod = prefs.getString(PREF_PUBLIC_MOD, DEFAULT_PREF);
        String pubExp = prefs.getString(PREF_PUBLIC_EXP, DEFAULT_PREF);
        Log.w(TAG, "the public modulus is " + pubMod + " and exponent is "
                + pubExp + " for " + contactNum);
        // String recipient = prefs.getString(PREF_RECIPIENT_NUM, DEFAULT_PREF);
        if (!pubMod.isEmpty() && !pubExp.isEmpty()) {
            byte[] pubModBA = Base64.decode(pubMod, Base64.NO_WRAP);
            byte[] pubExpBA = Base64.decode(pubExp, Base64.NO_WRAP);
            BigInteger pubModBI = new BigInteger(1, pubModBA); // 1 is added as

            BigInteger pubExpBI = new BigInteger(1, pubExpBA);

            return new RSAPublicKeySpec(pubModBI,
                    pubExpBI);

        }
        return null;
    }


    //check if keys found in app preferences
    public static boolean getKeys(int keySize, Context context) throws Exception{
        SharedPreferences prefs = context.getSharedPreferences(PREFS_MY_KEYS,
                Context.MODE_PRIVATE);
        String pubMod = prefs.getString(PREF_PUBLIC_MOD, DEFAULT_PREF);
        String pubExp = prefs.getString(PREF_PUBLIC_EXP, DEFAULT_PREF);
        String privateMod = prefs.getString(PREF_PRIVATE_MOD, DEFAULT_PREF);
        String privateExp = prefs.getString(PREF_PRIVATE_EXP, DEFAULT_PREF);

        return (!pubMod.isEmpty() && !pubExp.isEmpty() && !privateMod.isEmpty()
                && !privateExp.isEmpty()) ;
    }


    private static void savePublicKey(String mod, String exp, Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_MY_KEYS,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = prefs.edit();

        prefsEditor.putString(PREF_PUBLIC_MOD, mod);
        prefsEditor.putString(PREF_PUBLIC_EXP, exp);
        prefsEditor.apply();
    }

    private static void savePublicKey(RSAPublicKeySpec pubKey, Context context) {
        BigInteger pubModBI = pubKey.getModulus();
        BigInteger pubExpBI = pubKey.getPublicExponent();

        byte[] pubModBA = pubModBI.toByteArray();// Base64.encodeInteger(pubModBI);
        byte[] pubExpBA = pubExpBI.toByteArray();// Base64.encodeInteger(pubExpBI);

        try {
            String pubModBase64Str = Base64.encodeToString(pubModBA,
                    Base64.NO_WRAP);
            String pubExpBase64Str = Base64.encodeToString(pubExpBA,
                    Base64.NO_WRAP);

            savePublicKey(pubModBase64Str, pubExpBase64Str, context);

        } catch (NoSuchMethodError e) {
            Log.e(TAG, "Base64.encode() method not available", e);
        }
    }

    private static void savePrivateKey(RSAPrivateKeySpec privateKey,
                                       Context context) {
        BigInteger privateModBI = privateKey.getModulus();
        BigInteger privateExpBI = privateKey.getPrivateExponent();

        byte[] privateModBA = privateModBI.toByteArray();
        byte[] privateExpBA = privateExpBI.toByteArray();

        try {
            String privateModBase64Str = Base64.encodeToString(privateModBA,
                    Base64.NO_WRAP);
            String privateExpBase64Str = Base64.encodeToString(privateExpBA,
                    Base64.NO_WRAP);
			/*Log.i(TAG, "the modulus of the current user's private key is "
					+ privateModBI + " and the exponent is " + privateExpBI
					+ " | encoded module is " + privateModBase64Str
					+ " | encoded exponent is " + privateExpBase64Str);*/

            savePrivateKey(privateModBase64Str, privateExpBase64Str, context);

        } catch (NoSuchMethodError e) {
            Log.e(TAG, "Base64.encode() method not available", e);
        }
    }

    private static void savePrivateKey(String mod, String exp, Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_MY_KEYS,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = prefs.edit();

        prefsEditor.putString(PREF_PRIVATE_MOD, mod);
        prefsEditor.putString(PREF_PRIVATE_EXP, exp);
        prefsEditor.apply();
    }
}
