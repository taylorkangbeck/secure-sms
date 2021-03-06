package edu.vanderbilt.cs285.secure_sms;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.Base64;

import java.io.IOException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

import static android.util.Base64.decode;
import static android.util.Base64.encodeToString;

/**
 * Created by Jay on 11/30/2014.
 */
class EncryptionHelper {

    // prints bytes seperated by spaces.
    public static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
            sb.append(" ");
        }
        return sb.toString();
    }

    public static String encryptBody(String message, RSAPublicKeySpec recipientsPubKey) throws Exception {
        return encryptAndEncodeBytes(message.getBytes(), recipientsPubKey);
    }

    public static String encryptBody(String message, String symmetricKey) throws Exception {

        byte[] encrypted = SymmetricEncryptor.encryptBytes(symmetricKey.getBytes(), message.getBytes());
        return encodeToString(encrypted, Base64.NO_WRAP);
    }

    public static String encryptAndEncodeBytes(byte[] data, RSAPublicKeySpec recipientsPubKey) throws Exception {
        byte[] encrypted = AsymmetricEncrpytor.encryptBytes(data, recipientsPubKey);
        return encodeToString(encrypted, Base64.NO_WRAP);
    }

    public static String decryptBody(String body, RSAPrivateKeySpec recipientPrivateKeySpec) throws Exception {
        try{
            byte[] decodedBody = decode(body, Base64.NO_WRAP);
            return new String(AsymmetricEncrpytor.decryptBytes(decodedBody, recipientPrivateKeySpec));
        } catch(IOException e) {
            throw new IOException("Bad Base64 Encoding...", e);
        }

    }

    public static String decryptBody(String message, String symmetricKey) throws Exception {
        byte[] decodedBody = decode(message, Base64.NO_WRAP);
        return new String(SymmetricEncryptor.decryptBytes( symmetricKey.getBytes(), decodedBody));
    }
}
