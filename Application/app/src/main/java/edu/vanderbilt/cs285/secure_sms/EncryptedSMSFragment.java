package edu.vanderbilt.cs285.secure_sms;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Taylor on 11/28/2014.
 */
public class EncryptedSMSFragment extends Fragment {

    public EncryptedSMSFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_encrypted_sms, container, false);
        return rootView;
    }
}
