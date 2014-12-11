package edu.vanderbilt.cs285.secure_sms;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by Taylor on 12/7/2014.
 */
public class Message implements Parcelable {
    private String sender;
    private String recipient;
    private String body;
    private boolean isSecure;
    
    private int mData;

    public Message(String sndr, String rec, String msg) {
        this(sndr, rec, msg, false);
    }

    public Message(String sndr, String rec, String msg, boolean isS) {
        sender = sndr;
        recipient = rec;
        body = msg;
        isSecure = isS;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getBody() {
        return body;
    }

    public String getSender() {
        return sender;
    }

    public boolean isSecure() {
        return isSecure;
    }

    public String toString() {
        return body;
    }

    //parcel stuff
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        String[] arr = {sender, recipient, body, Boolean.toString(isSecure)};
        out.writeStringArray(arr);
    }

    public static final Parcelable.Creator<Message> CREATOR
            = new Parcelable.Creator<Message>() {
        public Message createFromParcel(Parcel in) {
            return new Message(in);
        }

        public Message[] newArray(int size) {
            return new Message[size];
        }
    };

    private Message(Parcel in) {
        String[] arr = new String[4];
        in.readStringArray(arr);
        this.sender = arr[0];
        this.recipient = arr[1];
        this.body = arr[2];
        this.isSecure = Boolean.getBoolean(arr[3]);
    }
}
