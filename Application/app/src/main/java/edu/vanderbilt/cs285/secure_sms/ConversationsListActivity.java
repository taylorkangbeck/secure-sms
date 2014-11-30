package edu.vanderbilt.cs285.secure_sms;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;


public class ConversationsListActivity extends Activity {
//lists current conversations

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversations_list);

        Uri mSmsinboxQueryUri = Uri.parse("content://sms/inbox");
        Cursor cursor1 = getContentResolver().query(mSmsinboxQueryUri,new String[] { "_id", "thread_id", "address", "person", "date","body", "type" }, null, null, null);

        /*
        ContentResolver contentResolver = getContentResolver();
        final String[] projection = new String[]{"*"};
        Uri uri = Uri.parse("content://mms-sms/conversations/");
        Cursor query = contentResolver.query(uri, projection, null, null, null);
        query.moveToFirst();
        String [] cols = query.getColumnNames();
        for(String s : cols) {
            Log.i("Column",s);
        }

        */
        ListView listview = (ListView) findViewById(R.id.convoListView);
        listview.setAdapter(new ConversationsListAdapter(this, cursor1));


        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Toast.makeText(ConversationsListActivity.this, "" + position, Toast.LENGTH_SHORT).show();
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}