package edu.vanderbilt.cs285.secure_sms;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.Base64;

import java.io.IOException;

import static android.util.Base64.decode;
import static android.util.Base64.encodeToString;

/**
 * Created by Jay on 11/30/2014.
 */
public class EncryptionHelper {
    private Encryptor encryptor;

    EncryptionHelper(Encryptor encryptor)
    {
        this.encryptor = encryptor;
    }

    public String encryptBody(String body) throws Exception  {
        return encryptAndEncodeBytes(body.getBytes());
    }

    public String decryptBody(String body) throws Exception {
        return new String(decodeAndDecryptBytes(body));
    }


    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private byte[] decodeAndDecryptBytes(String body) throws Exception{
        try{
            byte[] decodedBody = decode(body, Base64.DEFAULT);
            return encryptor.decryptBytes(decodedBody);
        } catch(IOException e) {
            throw new IOException("Bad Base64 Encoding...", e);
        }
    }

    private String encryptAndEncodeBytes( byte[] bytes) throws Exception {
        byte[] encrypted = encryptor.encryptBytes(bytes);
        return encodeToString(encrypted, Base64.DEFAULT);
    }
}
