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
                String recip = cursor.getString(cursor.getColumnIndex("address"));
                //Toast.makeText(ConversationsListActivity.this,convoId, Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getBaseContext(), ConversationActivity.class);
                intent.putExtra("convoId", convoId);
                intent.putExtra("recipient", recip);
                startActivity(intent);
            }
        });
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
        if (id == R.id.action_newsms) {
            Intent in = new Intent(getBaseContext(), SimpleSendSMSActivity.class);
            startActivity(in);
            return true;
        }
        else {
            Intent in = new Intent(getBaseContext(), ViewMyFingerprintActivity.class);
            startActivity(in);
            return true;
        }
    }

}