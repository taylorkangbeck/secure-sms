package edu.vanderbilt.cs285.secure_sms;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;


public class ConversationActivity extends Activity {
//represents single conversation with another person, with tabs for insecure and secure messaging

    String convoId;
    ListView listview;
    String recipient;
    Menu mOptionsMenu;
    boolean activeSession = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        //get convoId
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            convoId = extras.getString("CONVO_ID");
            //Toast.makeText(ConversationActivity.this,convoId, Toast.LENGTH_SHORT).show();
        }



        listview = (ListView) findViewById(R.id.oneConvoListView);
        int[] tvIds = new int[]{R.id.msgSender, R.id.msgBody};

        String[] qColumns = new String[]{"type","body"};

        //Cursor cursor = getContentResolver().query(inboxUri, null, null, null, "date DESC");
        Cursor cursor = getContentResolver().query(Uri.parse("content://sms/"), null,  "thread_id=" + convoId, null, "date ASC");
        cursor.moveToFirst();
        recipient = cursor.getString(cursor.getColumnIndex("address"));
        setTitle(recipient);
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.message_item, cursor, qColumns, tvIds);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                //TODO: allow decryption by tapping?

                //TextView tv = (TextView) findViewById(R.id.msgBody);
                //String text = tv.getText().toString();

                //Toast.makeText(getApplicationContext(), ":" + text, Toast.LENGTH_SHORT);

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
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_encrypt) {
            if (activeSession)
                mOptionsMenu.findItem(R.id.action_encrypt).setIcon(R.drawable.ic_lock_open_black_24dp);
            else
                mOptionsMenu.findItem(R.id.action_encrypt).setIcon(R.drawable.ic_lock_black_24dp);
            activeSession = !activeSession;
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
