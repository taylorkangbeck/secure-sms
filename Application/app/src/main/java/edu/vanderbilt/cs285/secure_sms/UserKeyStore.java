package edu.vanderbilt.cs285.secure_sms;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by Jake on 12/4/2014.
 */
public class UserKeyStore implements Serializable {
    private HashMap<String, UserKeyPair> keyStore;

    public UserKeyStore() {
        this.keyStore = new HashMap<String, UserKeyPair>();
    }

    // does not copy HashMap values, simply makes keyStore a reference to uks.keyStore!
    public UserKeyStore(UserKeyStore uks) {
        this.keyStore = uks.keyStore;
    }

    public String getPublicKey(String number) {
        return keyStore.get(number).getPublicKey();
    }

    public String getSymmetricKey(String number) {
        return keyStore.get(number).getSymmetricKey();
    }

    public boolean hasPublicKey(String number) {
        return keyStore.get(number).hasPublicKey();
    }

    public boolean hasPrivateKey(String number) {
        return keyStore.get(number).hasPrivateKey();
    }
}
