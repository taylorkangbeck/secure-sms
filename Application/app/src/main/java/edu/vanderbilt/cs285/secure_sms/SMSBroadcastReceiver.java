package edu.vanderbilt.cs285.secure_sms;

/**
 * Created by Taylor on 11/30/2014.
 */

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class SMSBroadcastReceiver extends BroadcastReceiver {
    private final String TAG = "SmsReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        SMSHandler smsHandler = new SMSHandler();
        Intent in=new Intent(context,ConversationActivity.class);
        in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        in.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);


        String SMS_DELIVERED = "SMS_DELIVERED";
        String SMS_SENT = "SMS_SENT";
        String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
        if (action.equals(SMS_RECEIVED)) {

            //in case multiple sender messages come in at the same time
            Map<String, String> msg = retrieveMessages(intent);

            if (msg != null) {
                String message = "";
                for (String sender : msg.keySet()) {
                    message = msg.get(sender);

                    Log.w(TAG, "message received is " + message);



                    //handle message for each sender
                    //smsHandler.handleMessage(message, sender, context, intent);
                    //fill the sender's phone number into Intent
                    //in.putExtra("originNum", sender);

                    //fill the entire message body into Intent
                    in.putExtra("msgContent", message);

                    //start the DisplaySMSActivity.java
                    context.startActivity(in);
                }
            }
        } else if (action.equals(SMS_SENT)) {
            Log.w(TAG, "received sent sms report");
            handleSentSms(context);
        } else if (action.equals(SMS_DELIVERED)) {
            Log.w(TAG, "received delivered sms report");
            handleDeliveredSms(context);
        }

    }


    private void handleSentSms(Context context) {
        switch (getResultCode()) {
            case Activity.RESULT_OK:
                Log.w(TAG, "SMS sent");
                Toast.makeText(context, "SMS sent", Toast.LENGTH_SHORT).show();

                // TextView debug = (TextView) findViewById(R.id.DebugMessages);
                // debug.append("SMS sent");
                break;
            case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                Toast.makeText(context, "Generic failure", Toast.LENGTH_SHORT)
                        .show();
                Log.w(TAG, "Generic failure");
                break;
            case SmsManager.RESULT_ERROR_NO_SERVICE:
                Toast.makeText(context, "No service", Toast.LENGTH_SHORT).show();
                Log.w(TAG, "No service");
                break;
            case SmsManager.RESULT_ERROR_NULL_PDU:
                Toast.makeText(context, "Null PDU", Toast.LENGTH_SHORT).show();
                Log.w(TAG, "Null PDU");
                break;
            case SmsManager.RESULT_ERROR_RADIO_OFF:
                Toast.makeText(context, "Radio off", Toast.LENGTH_SHORT).show();
                Log.w(TAG, "Radio off");
                break;
        }
    }

    private void handleDeliveredSms(Context context) {
        switch (getResultCode()) {
            case Activity.RESULT_OK:
                Log.w(TAG, "SMS delivered");
                Toast.makeText(context, "SMS delivered", Toast.LENGTH_SHORT).show();

                // TextView debug = (TextView) findViewById(R.id.DebugMessages);
                // debug.append("SMS sent");
                break;
            case Activity.RESULT_CANCELED:
                Toast.makeText(context, "SMS not delivered", Toast.LENGTH_SHORT)
                        .show();
                Log.w(TAG, "SMS not delivered");
                break;
        }
    }


    private Map<String, String> retrieveMessages(Intent intent) {
        Map<String, String> msg = null;
        SmsMessage[] msgs = null;
        Bundle bundle = intent.getExtras();

        if (bundle != null && bundle.containsKey("pdus")) {
            Object[] pdus = (Object[]) bundle.get("pdus");

            if (pdus != null) {
                int nbrOfpdus = pdus.length;
                msg = new HashMap<String, String>(nbrOfpdus);
                msgs = new SmsMessage[nbrOfpdus];

                // There can be multiple SMS from multiple senders, there can be
                // a maximum of nbrOfpdus different senders
                // However, send long SMS of same sender in one message
                for (int i = 0; i < nbrOfpdus; i++) {
                    msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);

                    String originatingAddress = msgs[i].getOriginatingAddress();

                    // Check if index with number exists
                    if (!msg.containsKey(originatingAddress)) {
                        // Index with number doesn't exist
                        // Save string into associative array with sender number
                        // as index
                        msg.put(msgs[i].getOriginatingAddress(),
                                msgs[i].getMessageBody());

                    } else {
                        // so just add the part of the current PDU
                        String previousparts = msg.get(originatingAddress);
                        String msgString = previousparts
                                + msgs[i].getMessageBody();
                        msg.put(originatingAddress, msgString);
                    }
                    //abort broadcast to cellphone inbox
                    abortBroadcast();
                }
            }
        }

        return msg;
    }


}
