package edu.vanderbilt.cs285.secure_sms;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;

import javax.crypto.Cipher;

/**
 * Created by Jay on 11/30/2014.
 */
public class AsymmetricEncrpytor implements Encryptor {

    private PublicKey publicKey;
    private PrivateKey privateKey;

    //generate RSA keypair information
    public void init() throws NoSuchAlgorithmException {
        KeyPairGenerator kpg= KeyPairGenerator.getInstance("RSA");
        SecureRandom random = new SecureRandom();
        kpg.initialize(1024, random);
        KeyPair kp = kpg.generateKeyPair();
        publicKey = kp.getPublic();
        privateKey = kp.getPrivate();
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    //RSA encryption
    public byte[] encryptBytes(byte[] plainBytes) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(plainBytes);
    }

    //RSA decrpytion
    public byte[] decryptBytes(byte[] cipherBytes) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(cipherBytes);
    }


}
