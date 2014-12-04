package edu.vanderbilt.cs285.secure_sms;

import android.app.Activity;
import java.security.Key;
import java.util.ArrayList;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

//portions of this code have been adapted from  https://sites.google.com/site/mobilesecuritylabware/3-data-location-privacy/lab-activity/cryptography/cryptography-mobile-labs/encryption-decryption/2-lab-activity/lab-activity
public class SimpleSendSMSActivity extends Activity {

    EditText recip;
    EditText msgContent;
    Button send;
    Button cancel;
    Switch encSwitch;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_send_sms);

        recip = (EditText) findViewById(R.id.recipient);
        msgContent = (EditText) findViewById(R.id.msgContent);
        send = (Button) findViewById(R.id.Send);
        cancel = (Button) findViewById(R.id.cancel);
        encSwitch = (Switch) findViewById(R.id.encryptSwitch);

        encSwitch.setChecked(true); //default to encrypted



        // finish the activity when click Cancel button
        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        // encrypt the message and send when click Send button
        send.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String recNumString = recip.getText().toString();
                String secretKeyString = "10"; // TODO: fix this
                String msgContentString = msgContent.getText().toString();
                String msgString = "";

                // check for the validity of the user input
                // key length should be 16 characters as defined by AES-128-bit
                if (recNumString.length() > 0
                        && msgContentString.length() > 0) {
                    if (encSwitch.isChecked()) {
                        // encrypt the message

                        byte[] encryptedMsg = encryptSMS(secretKeyString,
                                msgContentString);

                        // convert the byte array to hex format for transmission
                        msgString = byte2hex(encryptedMsg);
                    }

                    // send the message through SMS
                    Log.i("SMS", "about to send");
                    sendSMS(recNumString, msgString);

                    //finish();

                } else
                    Toast.makeText(
                            getBaseContext(),
                            "Please enter phone number and a message.",
                            Toast.LENGTH_SHORT).show();
            }
        });

    }

    public static void sendSMS(String recNumString, String encryptedMsg) {
        try {

            // get a SmsManager
            SmsManager smsManager = SmsManager.getDefault();

            if (encryptedMsg.length() < 160) {
                smsManager.sendTextMessage(recNumString, null, encryptedMsg, null, null);
                Log.i("MSG", "got past send");
            }
            else {
                // Message may exceed 160 characters
                // need to divide the message into multiples
                ArrayList<String> parts = smsManager.divideMessage(encryptedMsg);
                smsManager.sendMultipartTextMessage(recNumString, null, parts,
                        null, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // utility function
    public static String byte2hex(byte[] b) {
        String hs = "";
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0xFF);
            if (stmp.length() == 1)
                hs += ("0" + stmp);
            else
                hs += stmp;
        }
        return hs.toUpperCase();
    }

    // encryption function
    public static byte[] encryptSMS(String secretKeyString,
                                    String msgContentString) {

        try {
            byte[] returnArray;

            // generate AES secret key from user input
            Key key = generateKey(secretKeyString);

            // specify the cipher algorithm using AES
            Cipher c = Cipher.getInstance("AES");

            // specify the encryption mode
            c.init(Cipher.ENCRYPT_MODE, key);

            // encrypt
            returnArray = c.doFinal(msgContentString.getBytes());

            return returnArray;

        } catch (Exception e) {
            e.printStackTrace();
            byte[] returnArray = null;
            return returnArray;
        }

    }

    private static Key generateKey(String secretKeyString) throws Exception {
        // generate secret key from string
        Key key = new SecretKeySpec(secretKeyString.getBytes(), "AES");
        return key;
    }

}
