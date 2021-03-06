package edu.vanderbilt.cs285.secure_sms;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import java.security.spec.RSAPublicKeySpec;
import java.util.ArrayList;

/**
 * Created by Jay on 12/3/2014.
 */
class SMSSender {
    private final String TAG = SMSSender.class.getName();

    private String recipientNum;
    private String message;


    public SMSSender(String phoneNum, String msg) {
        this.recipientNum = phoneNum;
        this.message = msg;
    }

    public SMSSender(String phoneNum) {
        this.recipientNum = phoneNum;
    }

    public String getRecipientNum() {
        return this.recipientNum;
    }

    public void setRecipientNum(String phoneNo) {
        this.recipientNum = phoneNo;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String msgStr) {
        this.message = msgStr;
    }

    void sendLongSMS(Context context) {
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";
        SmsManager sm = SmsManager.getDefault();

        if (this.message != null && !this.message.isEmpty() && this.recipientNum!=null && !this.recipientNum.isEmpty()) {

            ArrayList<String> parts = sm.divideMessage(this.message);

            int numParts = parts.size();

            ArrayList<PendingIntent> sentIntents = new ArrayList<PendingIntent>();
            ArrayList<PendingIntent> deliveryIntents = new ArrayList<PendingIntent>();

            for (int i = 0; i < numParts; i++) {
                sentIntents.add(PendingIntent.getBroadcast(context, 0,
                        new Intent(SENT), 0));
                deliveryIntents.add(PendingIntent.getBroadcast(context, 0,
                        new Intent(DELIVERED), 0));
            }

            sm.sendMultipartTextMessage(this.recipientNum, null, parts,
                    sentIntents, deliveryIntents);
        } else {
            Log.e(TAG, "message or contact num not set, message: "+this.message+" and recipient: "+this.recipientNum);
        }
    }

    void sendSMS(Context context) {
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";

        PendingIntent sentPI = PendingIntent.getBroadcast(context, 0,
                new Intent(SENT), 0);

        PendingIntent deliveredPI = PendingIntent.getBroadcast(context, 0,
                new Intent(DELIVERED), 0);

        SmsManager sms = SmsManager.getDefault();
        try {
            sms.sendTextMessage(this.recipientNum, null, message, sentPI,
                    deliveredPI);
        } catch (IllegalArgumentException e) {
            Toast.makeText(context, "info not correct "
                    + this.recipientNum, Toast.LENGTH_LONG).show();
        }
    }

    void sendKeyExchangeSMS(String recipient, String key,
                            Context context) {
            String message = SMSTypeDecoder.calculateKeyExchangePrefix(key) + key;
            this.recipientNum = recipient;
            this.message = message;
            if (this.message.length() > 160) {
                sendLongSMS(context);
            } else {
                sendSMS(context);
            }

    }



    void sendKeyExchangeSMS(Context context) {
        if (this.recipientNum==null || this.recipientNum.isEmpty()) {
            Log.e(TAG, "Recipient number is not set: "+this.recipientNum);
        } else {
            String pubMod = AsymmetricEncrpytor.getPubMod(AsymmetricEncrpytor.PREFS_MY_KEYS, context);
            String pubExp = AsymmetricEncrpytor.getPubExp(AsymmetricEncrpytor.PREFS_MY_KEYS, context);
            // EditText recipient = (EditText) findViewById(R.id.InputRecipientNum);
            if (pubMod.length() != 0 && pubExp.length() != 0) {
                sendKeyExchangeSMS(this.recipientNum, pubMod + " " + pubExp, context);
            } else {
                Toast.makeText(context, "mod or exp pub key not found "
                       , Toast.LENGTH_LONG).show();
            }
        }
    }

    void sendReplyKeyExchangeSMS(String recipient, String key,
                            Context context) {
        String message = SMSTypeDecoder.calculateReplyKeyExchangePrefix(key) + key;
        this.recipientNum = recipient;
        this.message = message;
        if (this.message.length() > 160) {
            sendLongSMS(context);
        } else {
            sendSMS(context);
        }

    }


    public void sendSecureSMS(Context context, String message) {
        // "sending a secure SMS, recipient is "+this.recipientNum+" original message is "+this.message);
        String encrypted;
        String symmetricKey = SymmetricEncryptor.getRecipientsSymmetricKey(this.recipientNum, context);
        Toast.makeText(context, "Ths is the symmetricKey" +symmetricKey
                , Toast.LENGTH_LONG).show();
        if(symmetricKey != null && !symmetricKey.isEmpty())
        {
            try {
                encrypted = EncryptionHelper.encryptBody(message, symmetricKey);
                this.message = SMSTypeDecoder.calculateEncryptedMesagePrefix(encrypted) + encrypted;
                if (this.message.length() > 160) {
                    sendLongSMS(context);
                } else {
                    sendSMS(context);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else{
            RSAPublicKeySpec recipientsPubKey = AsymmetricEncrpytor.getRecipientsPublicKey(
                    this.recipientNum, context);
            if (recipientsPubKey == null) {
                Toast.makeText(context, "recipient's public key could not be retrieved for "
                        + this.recipientNum, Toast.LENGTH_LONG).show();
                sendKeyExchangeSMS(context);
            }
            else
            try {
                sendSymmetricKey(context);
            } catch (Exception e) {
                e.printStackTrace();
            }



    }


    }

    public void sendSymmetricKey(Context context) {
       try{
        byte [] key = SymmetricEncryptor.generateKey();
        SymmetricEncryptor.saveSymmetricKey(new String(key), recipientNum, context);
        RSAPublicKeySpec recipientsPubKey = AsymmetricEncrpytor.getRecipientsPublicKey( this.recipientNum, context);
        message = EncryptionHelper.encryptAndEncodeBytes(key, recipientsPubKey);
        message = SMSTypeDecoder.calculateEncryptedKeyPrefix(message) + message;
        if (this.message.length() > 160) {
            sendLongSMS(context);
        } else {
            sendSMS(context);
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    }
}
