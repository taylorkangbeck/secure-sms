package edu.vanderbilt.cs285.secure_sms;

import java.io.Serializable;

/**
 * Created by Jake on 12/4/2014.
 */
public class UserKeyPair implements Serializable {
    private String publicKey;
    private String symmetricKey;

    public UserKeyPair(String publicKey, String symmetricKey) {
        this.publicKey = publicKey;
        this.symmetricKey = symmetricKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public String getSymmetricKey() {
        return symmetricKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public void setSymmetricKey(String symmetricKey) {
        this.symmetricKey = symmetricKey;
    }

    public boolean hasPublicKey() {
        return publicKey != null;
    }

    public boolean hasPrivateKey() {
        return symmetricKey != null;
    }
}
