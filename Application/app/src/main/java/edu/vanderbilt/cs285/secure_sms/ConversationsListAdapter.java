/*
package edu.vanderbilt.cs285.secure_sms;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

*/
/**
 * Created by Taylor on 11/28/2014.
 *//*

public class ConversationsListAdapter extends BaseAdapter {
    private Context mContext;
    //private Cursor mCursor;
    private String[] mNames = {
            "test","test","test","test","test","test","test","test","test","test",
            "test","test","test","test","test","test","test","test","test","test"
    };

    public ConversationsListAdapter(Context c) {
        mContext = c;
    }

    public ConversationsListAdapter(Context c, String[] titles, Integer[] ConversationsListIds) {
        mContext = c;
        this.mNames = titles;
    }

    public ConversationsListAdapter(Context c, Cursor cursor1) {
        mContext = c;
        //startManagingCursor(cursor1);
        String[] columns = new String[] { "address", "person", "date", "body","type" };
        cursor1.moveToFirst();
        if (cursor1.getCount() > 0) {
            String count = Integer.toString(cursor1.getCount());
            while (cursor1.moveToNext()){
                //String address = cursor1.getString(cursor1.getColumnIndex(columns[0]));
                String name = cursor1.getString(cursor1.getColumnIndex(columns[1]));
                //Log.d("NAME", name);
                //String date = cursor1.getString(cursor1.getColumnIndex(columns[2]));
                //String msg = cursor1.getString(cursor1.getColumnIndex(columns[3]));
                //String type = cursor1.getString(cursor1.getColumnIndex(columns[4]));
                if(cursor1.getCount() < mNames.length)
                    mNames[cursor1.getCount()] = name;
            }
        }
    }


    public int getCount() {
        return mNames.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new view for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView;
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            itemView = inflater.inflate(R.layout.conversations_list_item, null);
            TextView contactView = (TextView) itemView.findViewById(R.id.contactView);
            contactView.setText(mNames[position]);

        } else {
            itemView = (View) convertView;
        }
        return itemView;
    }

}
*/
