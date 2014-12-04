package edu.vanderbilt.cs285.secure_sms;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;


public class ConversationsListActivity extends Activity {
//lists current conversations

    // stores the mapping of phone# -> associated keys
    public static UserKeyStore keyStore;

    ListView listview;
    Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversations_list);
        context = this;

        listview = (ListView) findViewById(R.id.convoListView);
        int[] tvIds = new int[]{R.id.contactView, R.id.bodyView};

        String[] qColumns = new String[]{"address","body"};

        Uri inboxUri = Uri.parse("content://mms-sms/conversations");
        Cursor cursor = getContentResolver().query(inboxUri, null, null, null, "date DESC");

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.conversations_list_item, cursor, qColumns, tvIds);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Cursor cursor  = (Cursor) listview.getItemAtPosition(position);
                String convoId = cursor.getString(cursor.getColumnIndex("thread_id"));
                //Toast.makeText(ConversationsListActivity.this,convoId, Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getBaseContext(), ConversationActivity.class);
                intent.putExtra("CONVO_ID", convoId);
                startActivity(intent);
            }
        });

        // load the keystore
        File file = new File(getDir("data", MODE_PRIVATE), "keyStore");
        try {
            ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file));
            keyStore = (UserKeyStore) inputStream.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(keyStore == null)
            keyStore = new UserKeyStore();
    }

    // saves keystore to memory, so that it persists
    @Override
    protected void onPause() {
        super.onPause();

        File file = new File(getDir("data", MODE_PRIVATE), "keyStore");
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(file));
            outputStream.writeObject(keyStore);
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
        }
        else if (id == R.id.action_newsms) {
            Intent in = new Intent(getBaseContext(), SimpleSendSMSActivity.class);
            startActivity(in);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}