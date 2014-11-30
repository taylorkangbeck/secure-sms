package edu.vanderbilt.cs285.secure_sms;
import java.security.MessageDigest;
import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * Created by Jay on 11/30/2014.
 */
public class SymmetricEncrpytor implements Encryptor {

    static{
        Security.addProvider(new BouncyCastleProvider());
    }

    private static byte[] iv;
    private static byte[] symmetricKey;
    private static SecretKeySpec aesKeySpec;

    public byte[] encryptBytes(byte[] plainText) throws Exception {

        // create a digest for key
        MessageDigest sha = MessageDigest.getInstance("SHA-256");

        byte[] hashedKey = sha.digest(symmetricKey);

        // create a hashed secret key
        SecretKeySpec sk = new SecretKeySpec(hashedKey, "AES");
        // Create the cipher
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding", "BC");
        // Initialize the cipher for encryption
        cipher.init(Cipher.ENCRYPT_MODE, sk);
        iv = cipher.getIV();
        aesKeySpec = sk;
        // Encrypt the plaintext
        return cipher.doFinal(plainText);
    }

    public  byte[] decryptBytes(byte[] cipherText) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding","BC");
        // Initialize the same cipher for decryption
        cipher.init(Cipher.DECRYPT_MODE, aesKeySpec, new IvParameterSpec(iv));
        // Decrypt the ciphertext
        return cipher.doFinal(cipherText);
    }

}
