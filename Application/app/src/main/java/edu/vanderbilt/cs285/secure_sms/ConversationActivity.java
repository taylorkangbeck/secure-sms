package edu.vanderbilt.cs285.secure_sms;

import java.util.Locale;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v13.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;


public class ConversationActivity extends Activity {
//represents single conversation with another person, with tabs for insecure and secure messaging

    String convoId;
    ListView listview;
    String recipient;

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
