package edu.vanderbilt.cs285.secure_sms;

import android.content.Context;
import android.graphics.Color;
import android.text.Layout;
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
    private LayoutInflater inflater;
    private ArrayList<Message> messages;

    public ConversationAdapter(Context c, ArrayList<Message> msgs) {
        inflater  = LayoutInflater.from(c);
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
        return index;
    }

    // create a new view for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView;
        MessageViewHolder holder;
        if (convertView == null) {
            itemView = inflater.inflate(R.layout.message_item, parent, false);
            holder = new MessageViewHolder();
            holder.senderView = (TextView) itemView.findViewById(R.id.msgSender);
            holder.bodyView = (TextView) itemView.findViewById(R.id.msgBody);
            holder.msgItem = (LinearLayout) itemView.findViewById(R.id.msgitem);
            itemView.setTag(holder);
        } else {
            itemView = convertView;
            holder = (MessageViewHolder) itemView.getTag();
        }

        Message curMsg = messages.get(position);
        holder.senderView.setText(curMsg.getSender());
        holder.bodyView.setText(curMsg.getBody());
        if(curMsg.isSecure()) {
            holder.msgItem.setBackgroundColor(Color.GREEN);
        }

        return itemView;
    }

    // private helper class to hold View contents
    private class MessageViewHolder {
        public TextView senderView;
        public TextView bodyView;
        public LinearLayout msgItem;
    }

}
