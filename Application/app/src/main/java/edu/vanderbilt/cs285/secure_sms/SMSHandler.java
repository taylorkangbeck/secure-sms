package edu.vanderbilt.cs285.secure_sms;

import java.math.BigInteger;
import java.security.spec.RSAPrivateKeySpec;

//import org.apache.commons.codec.binary.Base64;
import android.util.Base64;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Jay on 12/2/2014.
 */
public class SMSHandler {


        private final String TAG = "SmsHandler";

        // SharedPreferences
        private final String PREFS = "MyKeys";
        private final String PREF_PUBLIC_MOD = "PublicModulus";
        private final String PREF_PUBLIC_EXP = "PublicExponent";
        private final String PREF_PRIVATE_MOD = "PrivateModulus";
        private final String PREF_PRIVATE_EXP = "PrivateExponent";

        EncryptionHelper publicEncryptionHelper;
        EncryptionHelper symmetricEncryptionHelper;



        private final String DEFAULT_PREF = "";

        //private int msgProcessedCount = 0;
        public void handleMessage(String message, String sender, Context context, Intent i) {
            if(SMSTypeDecoder.isPreKeyBundle(message)) {
            }
            else if(SMSTypeDecoder.isKeyExchange(message)) {
                Log.i(TAG, "message received is a key exchange message");
                handleKeyExchangeMsg(message, sender, context, i);
            }
            else if(SMSTypeDecoder.isEncryptedMessage(message)) {
                Log.i(TAG, "received a secure text message");
                handleEncryptedMsg(message, sender, context);
            }
            else if(SMSTypeDecoder.isEndSession(message)) {
            }
            else {
                Log.i(TAG, "Message not recognised, not doing anything");
            }
        }

        //initiate key exchange
        private void handleKeyExchangeMsg(String message, String sender,
                                          Context context, Intent i) {

            String contactNum = sender;
            String[] parts = message.split(" "); // expected structure of the key
            // exchange message:
            // "keyx modBase64Encoded expBase64Encoded"
            if (parts.length == 3) {
                String recipientPubModBase64Str = parts[1];
                String recipientPubExpBase64Str = parts[2];


                byte[] recipientPubModBA = Base64.decode(recipientPubModBase64Str,
                        Base64.DEFAULT);
                byte[] recipientPubExpBA = Base64.decode(recipientPubExpBase64Str,
                        Base64.DEFAULT);
                BigInteger recipientPubMod = new BigInteger(recipientPubModBA);
                BigInteger recipientPubExp = new BigInteger(recipientPubExpBA);

                Log.i(TAG, "the recipient's public key modulus is "
                        + recipientPubMod + " and exponent is " + recipientPubExp);

                SharedPreferences prefs = context.getSharedPreferences(contactNum,
                        Context.MODE_PRIVATE);
                SharedPreferences.Editor prefsEditor = prefs.edit();

                prefsEditor.putString(PREF_PUBLIC_MOD, recipientPubModBase64Str);
                prefsEditor.putString(PREF_PUBLIC_EXP, recipientPubExpBase64Str);
                // prefsEditor.putString(PREF_PHONE_NUMBER, recipient);
                prefsEditor.commit();

                Log.i(TAG,
                        "successfully remembered the contact " + contactNum
                                + " and its public key module "
                                + prefs.getString(PREF_PUBLIC_MOD, DEFAULT_PREF)
                                + " and exponent "
                                + prefs.getString(PREF_PUBLIC_EXP, PREF_PUBLIC_EXP));
                Toast.makeText(context, "Ready to send secure message to "+contactNum, Toast.LENGTH_LONG).show();
                // call the UI Activity to verify that the updated sharedpreferences are available for its us
                int activitySwitch = 1;
                if (activitySwitch == 1) {
                    //MainActivity.onPublicKeyReceived(i, contactNum, context); // without
                    // calling
                    // this the activity UI won't see the updated
                    // SharedPreferences
                } else if (activitySwitch == 2) {

                }
            } else {
                Log.e(TAG,
                        "something is wrong with the key exchange message, it's supposed to have 3 parts: the code 'keyx', the modulus and the exponent");
            }

        }

        private void handleEncryptedMsg(String message, String sender,
                                        Context context) {
            String contactNum = sender;

            String[] parts = message.split(" ");
            if (parts.length == 2) {

                SharedPreferences prefs = context.getSharedPreferences(PREFS,
                        Context.MODE_PRIVATE);

                String privateMod = prefs.getString(PREF_PRIVATE_MOD, DEFAULT_PREF);
                String priavteExp = prefs.getString(PREF_PRIVATE_EXP, DEFAULT_PREF);
                // String recipient = prefs.getString(PREF_RECIPIENT_NUM,
                // DEFAULT_PREF);
                if (!privateMod.equals(DEFAULT_PREF)
                        && !priavteExp.equals(DEFAULT_PREF)) {
                    byte[] recipientPrivateModBA = Base64.decode(privateMod,
                            Base64.DEFAULT);
                    byte[] recipientPrivateExpBA = Base64.decode(priavteExp,
                            Base64.DEFAULT);
                    BigInteger recipientPrivateMod = new BigInteger(
                            recipientPrivateModBA);
                    BigInteger recipientPrivateExp = new BigInteger(
                            recipientPrivateExpBA);
                    RSAPrivateKeySpec recipientPrivateKeySpec = new RSAPrivateKeySpec(
                            recipientPrivateMod, recipientPrivateExp);

                    AsymmetricEncrpytor.setReceiverKey(recipientPrivateKeySpec);
                    try {
                        String decryptedMsg = publicEncryptionHelper.decryptBody(parts[1]);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e(TAG, "private key could not be retrieved");
                }
            }
        }
    }


