package cs285.vanderbilt.edu.keyexchange;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

/**
 * Created by Lian on 12/3/2014.
 */
public class KeyExchangeBroadcast extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle bundle = intent.getExtras();
        SmsMessage[] msgs = null;
        String str = "";
        if (bundle != null)
        {
            //---retrieve the public key received---
            Object[] pdus = (Object[]) bundle.get("pdus");
            msgs = new SmsMessage[pdus.length];
            for (int i=0; i<msgs.length; i++){
                msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                str += "public key received " + msgs[i].getOriginatingAddress();
                str += msgs[i].getMessageBody().toString();
            }
            //---display the new SMS message---
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();

        Intent bpublickey = new Intent(context,KeyExchangeMainActivity.class);
        bpublickey.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        bpublickey.putExtra("public key received", msgs.toString());
        context.startActivity(bpublickey);

        }
    }
}
