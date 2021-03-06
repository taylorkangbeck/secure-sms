package edu.vanderbilt.cs285.secure_sms;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.os.Messenger;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;


public class ConversationActivity extends Activity {
//represents single conversation with another person

    ListView listview;
    String recipient;
    Menu mOptionsMenu;
    boolean activeSession = false;
    ConversationAdapter mAdapter;
    MainHandler handler;
    String thread_id;

    // moved from ConversationAdapter
    private ArrayList<Message> messages;

    public Messenger messenger = new Messenger(new MainHandler(this));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        handler = new MainHandler(this);

        listview = (ListView) findViewById(R.id.oneConvoListView);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            recipient = extras.getString("recipient");
            thread_id = extras.getString("convoId");
        }

        // moved from ConversationAdapter
        messages = new ArrayList<Message>();

        Cursor cursor = getContentResolver().query(Uri.parse("content://sms/"), null,  "thread_id=" + thread_id, null, "date ASC");
        if(cursor != null && cursor.moveToFirst()) {
            while(!cursor.isAfterLast()) {
                String a = cursor.getString(cursor.getColumnIndex("address"));
                if(cursor.getString(cursor.getColumnIndex("type")).equals("2"))
                    a = "me";
                String b = cursor.getString(cursor.getColumnIndex("body"));
                messages.add(new Message(a, "", b));
                cursor.moveToNext();
            }
            cursor.close();
        }

        mAdapter = new ConversationAdapter(this, messages);
        listview.setAdapter(mAdapter);

        SMSHandler.setMessenger(messenger);




        setTitle(recipient);

        Button sendButton = (Button) findViewById(R.id.sendButton);
        sendButton.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                EditText txt = (EditText) findViewById(R.id.textText);
                if (activeSession) {
                    try {
                        String toSend = txt.getText().toString();
                        SMSSender sender = new SMSSender(recipient, toSend);
                        sender.sendSecureSMS(getApplicationContext(), toSend);
                        mAdapter.addMsg(new Message("me", recipient, toSend, true));
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "error encrypting", Toast.LENGTH_SHORT);
                    }

                } else{
                    SMSSender sender = new SMSSender(recipient, txt.getText().toString());
                    sender.sendLongSMS(getApplicationContext());
                    mAdapter.addMsg(new Message("me", recipient, txt.getText().toString(), false));
                }
                txt.setText("");
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        mOptionsMenu = menu;
        getMenuInflater().inflate(R.menu.menu_conversation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_encrypt) {
            if (activeSession) {
                mOptionsMenu.findItem(R.id.action_encrypt).setIcon(R.drawable.ic_lock_open_black_24dp);
            }
            else {
                mOptionsMenu.findItem(R.id.action_encrypt).setIcon(R.drawable.ic_lock_black_24dp);
            }
            activeSession = !activeSession;
            return true;
        }
        else {
            Intent in = new Intent(getBaseContext(), ViewTheirFingerprintActivity.class);
            in.putExtra("theirNumber", recipient);
            startActivity(in);
            return true;
        }
    }

    private static class MainHandler extends Handler {
        WeakReference<ConversationActivity> wr;

        MainHandler(ConversationActivity activity){
            wr = new WeakReference<ConversationActivity> (activity);
        }

        @Override
        public void handleMessage (android.os.Message message){
            ConversationActivity activity = wr.get();
            if (activity != null) {
                switch(message.what) {
                    case 1:
                        activity.handleNewMessage(((Message) message.obj));
                        break;
                    default:
                        super.handleMessage(message);
                }

            }
            else{
                Log.i("MainActivity", "activity returned null");
            }
        }

    }

    private void handleNewMessage(Message obj) {
        //Toast.makeText(getApplicationContext(),"This is a test" + obj.getBody(), Toast.LENGTH_LONG).show();
        if(obj.getSender().equals(recipient)) {
            mAdapter.addMsg(obj);
        }
    }
}
