package edu.vanderbilt.cs285.secure_sms;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Taylor on 12/7/2014.
 */
public class ConversationAdapter extends BaseAdapter    {

    private Context mContext;
    private ArrayList<Message> messages;

    public ConversationAdapter(Context c) {
        mContext = c;
        messages = new ArrayList<Message>();
    }

    public ConversationAdapter(Context c, ArrayList<Message> msgs) {
        mContext = c;
        messages = msgs;
    }

    public void addMsg(Message msg) {
        messages.add(msg);
    }

    public int getCount() {
        return messages.size();
    }

    public Message getItem(int index) {
        return messages.get(index);
    }

    public long getItemId(int index) {
        return 0;
    }

    // create a new view for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView;
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            itemView = inflater.inflate(R.layout.message_item, null);
            TextView senderView = (TextView) itemView.findViewById(R.id.msgSender);
            TextView bodyView = (TextView) itemView.findViewById(R.id.msgBody);
            LinearLayout msgItem = (LinearLayout) itemView.findViewById(R.id.msgitem);


            Message curMsg = messages.get(position);
            senderView.setText(curMsg.getSender());
            bodyView.setText(curMsg.getBody());
            if(curMsg.isSecure()) {
                msgItem.setBackgroundColor(Color.GREEN);
            }

        } else {
            itemView = (View) convertView;
        }
        return itemView;
    }

}
