package edu.vanderbilt.cs285.secure_sms;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.RSAPublicKeySpec;


public class ViewMyFingerprintActivity extends Activity {
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
        setContentView(R.layout.activity_view_my_fingerprint);

        key = (TextView) findViewById(R.id.editText);

        // get my public key information
        SharedPreferences prefs = getApplicationContext().getSharedPreferences(PREFS_MY_KEYS,
                Context.MODE_PRIVATE);
        String pubMod = prefs.getString(PREF_PUBLIC_MOD, DEFAULT_PREF);
        String pubExp = prefs.getString(PREF_PUBLIC_MOD, DEFAULT_PREF);

        // if I haven't a public key yet, generate one
        if(pubMod.isEmpty()) {
            try {
                AsymmetricEncrpytor.init(getApplicationContext());
            } catch (Exception e) {
                key.setText(e.toString());
            }
            // call it again, after initializing
            pubMod = prefs.getString(PREF_PUBLIC_MOD, DEFAULT_PREF);
            pubExp = prefs.getString(PREF_PUBLIC_MOD, DEFAULT_PREF);
        }
        // myPubkey will be the modulus with the exponent appended onto it
        String myPubKey = pubMod + pubExp;

        // create a hash of myPubKey, to make it easier an easier format
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        messageDigest.update(myPubKey.getBytes());
        // set myPubKey to the hash of itself
        myPubKey = toHexFormatted(messageDigest.digest());

        key.setText(myPubKey);
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.view_my_fingerprint, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
