package edu.vanderbilt.cs285.secure_sms;

import java.math.BigInteger;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

//import org.apache.commons.codec.binary.Base64;
import android.util.Base64;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by Jay on 12/2/2014.
 */
class SMSHandler {


        private final String TAG = SMSHandler.class.getName();

    private final String SYMMETRIC_KEY = "SymmetricKey";

        EncryptionHelper publicEncryptionHelper;
        EncryptionHelper symmetricEncryptionHelper;
        private String sender = "";


    public boolean handleMessage(String message, String sender, Context context, Intent i) {
            if(SMSTypeDecoder.isEncrypted(message)) {
                handleSymmetricKeyMsg(message, sender, context, i);
            }
            else if(SMSTypeDecoder.isKeyExchange(message)) {
                Log.i(TAG, "message received is a key exchange message");
                handleKeyExchangeMsg(message, sender, context, i);
            }
            else if(SMSTypeDecoder.isEncryptedMessage(message)) {
                Log.i(TAG, "received a secure text message");
                handleEncryptedMsg(message, sender, context);
                return true;
            }
            else if(SMSTypeDecoder.isEndSession(message)) {
            }
            else {
                Log.i(TAG, "Message not recognised, not doing anything");
            }
        return false;
        }

    
        private void handleSymmetricKeyMsg(String message, String sender, Context context, Intent i) {
            String contactNum = sender;
            String symmetricKeyEncrypted = message.substring(SMSTypeDecoder.PREFIX_SIZE);
            String PREFS = "MyKeys";
            SharedPreferences prefs = context.getSharedPreferences(PREFS,
                    Context.MODE_PRIVATE);

            String DEFAULT_PREF = "";
            String PREF_PRIVATE_MOD = "PrivateModulus";
            String privateMod = prefs.getString(PREF_PRIVATE_MOD, DEFAULT_PREF);
            String PREF_PRIVATE_EXP = "PrivateExponent";
            String priavteExp = prefs.getString(PREF_PRIVATE_EXP, DEFAULT_PREF);
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
                try {
                    String symmetricKey = EncryptionHelper.decryptBody(symmetricKeyEncrypted, recipientPrivateKeySpec);

                } catch (Exception e) {
                }
            }


        }
    //initiate key exchange
        private void handleKeyExchangeMsg(String message, String sender,
                                          Context context, Intent i) {

            String contactNum = sender;
            String[] parts = message.substring(SMSTypeDecoder.PREFIX_SIZE).split(" "); // expected structure of the key
            // exchange message:
            if (parts.length == 2) {
                String recipientPubModBase64Str = parts[1];
                String recipientPubExpBase64Str = parts[2];


                byte[] recipientPubModBA = Base64.decode(recipientPubModBase64Str,
                        Base64.DEFAULT);
                byte[] recipientPubExpBA = Base64.decode(recipientPubExpBase64Str,
                        Base64.DEFAULT);
                BigInteger recipientPubMod = new BigInteger(recipientPubModBA);
                BigInteger recipientPubExp = new BigInteger(recipientPubExpBA);

                SharedPreferences prefs = context.getSharedPreferences(contactNum,
                        Context.MODE_PRIVATE);
                SharedPreferences.Editor prefsEditor = prefs.edit();

                String PREF_PUBLIC_MOD = "PublicModulus";
                prefsEditor.putString(PREF_PUBLIC_MOD, recipientPubModBase64Str);
                String PREF_PUBLIC_EXP = "PublicExponent";
                prefsEditor.putString(PREF_PUBLIC_EXP, recipientPubExpBase64Str);
                // prefsEditor.putString(PREF_PHONE_NUMBER, recipient);
                prefsEditor.apply();


                //ready to encrypt symmetric key
                try {
                    SMSSender smsSender = new SMSSender(sender);
                    smsSender.sendSymmetricKey(context);

                } catch (Exception e) {
                    // handle exception
                }
            }
        }

        private void handleEncryptedMsg(String message, String sender,
                                        Context context) {
            String messageEncrypted = message.substring(SMSTypeDecoder.PREFIX_SIZE);
            String decrypted = "";
            String symmetricKey = SymmetricEncryptor.getRecipientsSymmetricKey(sender, context);
            if(symmetricKey != null)
            {
                try {
                    decrypted = EncryptionHelper.decryptBody(message, symmetricKey);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }


