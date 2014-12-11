package edu.vanderbilt.cs285.secure_sms;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class ViewTheirFingerprintActivity extends Activity {
    public static final String PREFS_MY_KEYS = "MyKeys";

    private static final String PREF_PUBLIC_MOD = "PublicModulus";
    private static final String PREF_PUBLIC_EXP = "PublicExponent";
    private static final String PREF_PRIVATE_MOD = "PrivateModulus";
    private static final String PREF_PRIVATE_EXP = "PrivateExponent";

    private static final String DEFAULT_PREF = "";

    TextView key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_view_their_fingerprint);
        key = (TextView) findViewById(R.id.editText);

        String recipientNumber =  getIntent().getStringExtra("theirNumber");

        // get their public key information
        SharedPreferences prefs = getApplicationContext().getSharedPreferences(recipientNumber,
                Context.MODE_PRIVATE);

        String pubMod = prefs.getString(PREF_PUBLIC_MOD, DEFAULT_PREF);
        String pubExp = prefs.getString(PREF_PUBLIC_MOD, DEFAULT_PREF);

        // if you have a public key for them
        if(!(pubMod.isEmpty())) {
            // theirPubKey will be the modulus with the exponent appended onto it
            String theirPubKey = pubMod + pubExp;

            // create a hash of myPubKey, to make it easier an easier format
            MessageDigest messageDigest = null;
            try {
                messageDigest = MessageDigest.getInstance("SHA-256");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            messageDigest.update(theirPubKey.getBytes());
            // set theirPubKey to the hash of itself
            theirPubKey = toHexFormatted(messageDigest.digest());
            key.setText(theirPubKey);
        }
    }

    // modified from http://stackoverflow.com/a/943963
    private static String toHexFormatted(byte[] bytes) {
        BigInteger bi = new BigInteger(1, bytes);
        String res = String.format("%0" + (bytes.length << 1) + "X", bi);
        // put spaces to separate every pair of characters
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < res.length(); ++i) {
            sb.append(res.charAt(i));
            if((i + 1) % 2 == 0)
                sb.append(" ");
            if((i + 1) % 16 == 0)
                sb.append("\n");
        }
        return sb.toString();
    }
}
