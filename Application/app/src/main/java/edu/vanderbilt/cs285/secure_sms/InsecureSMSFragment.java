package edu.vanderbilt.cs285.secure_sms;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Taylor on 11/28/2014.
 */
public class InsecureSMSFragment extends Fragment {

    public InsecureSMSFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_insecure_sms, container, false);
        return rootView;
    }
}
