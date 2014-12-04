package edu.vanderbilt.cs285.secure_sms;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

/**
 * Created by Taylor on 11/28/2014.
 */
public class InsecureSMSFragment extends Fragment {

    ListView listview;
    int convoId;

    public InsecureSMSFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //super.onCreate(savedInstanceState);

        //listview = (ListView) getView().findViewById(R.id.insecureListView);
/*
        Bundle bundle = getArguments();
        convoId = bundle.getInt("convoId");

        String[] qColumns = new String[]{"address", "body"};

        Uri inboxUri = Uri.parse("content://mms-sms/conversations/" + convoId);
        Cursor cursor = getActivity().getContentResolver().query(inboxUri, null, null, null, "date DESC");
        int[] tvIds = new int[]{R.id.msgSender, R.id.msgBody};

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(getActivity(), R.layout.message_item, cursor, qColumns, tvIds);
        listview.setAdapter(adapter);
        */
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_insecure_sms, container, false);
        return rootView;
    }
}
