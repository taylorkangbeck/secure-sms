package edu.vanderbilt.cs285.secure_sms;

/**
 * Created by Jay on 11/30/2014.
 */
interface Encryptor {

    //method signatures
    byte[] decryptBytes(byte[] cipherText) throws Exception;
    byte[] encryptBytes(byte[] plainText) throws Exception;
}
