package cs285.vanderbilt.edu.keyexchange;

import java.io.*;
import java.net.*;
import java.math.BigInteger;

import java.security.AlgorithmParameterGenerator;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.security.spec.InvalidParameterSpecException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.spec.DHParameterSpec;
import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.telephony.SmsManager;
import android.widget.Toast;
import android.os.Bundle;
import android.app.Activity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class KeyExchangeMainActivity extends Activity {

    String valuesInStr = genDhParams();
    String[] values = valuesInStr.split(",");
    BigInteger p = new BigInteger(values[0]);
    BigInteger g = new BigInteger(values[1]);
    int l = Integer.parseInt(values[2]);



    TextView public_Key;
    String publicKeyHex;
    TextView secretKeyStored;
    EditText txtphoneNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_key_exchange_main);

        try {

            // Use the values to generate a key pair
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DH");
            DHParameterSpec dhSpec = new DHParameterSpec(p, g, l);
            keyGen.initialize(dhSpec);
            KeyPair keypair = keyGen.generateKeyPair();

            // Get the generated public and private keys
            PrivateKey privateKey = keypair.getPrivate();
            PublicKey publicKey = keypair.getPublic();

            // Send the public key bytes to the other party
            byte[] aPublicKeyBytes = publicKey.getEncoded();

            System.out.println("Key length:"+aPublicKeyBytes.length);

            public_Key = (TextView) findViewById(R.id.public_key);
            publicKeyHex = "aPublicKey:\n"+asHex(aPublicKeyBytes);
            public_Key.setText("This is my public key: "+publicKeyHex);


            Log.i("Send public key", "");

            txtphoneNo = (EditText) findViewById(R.id.editTextPhoneNo);
            String phoneNo = txtphoneNo.getText().toString();
            String message = publicKeyHex;

            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, message, null, null);
            Toast.makeText(getApplicationContext(), "Public key sent.",
            Toast.LENGTH_LONG).show();


            Intent intent = getIntent();
            String PublicKeyReceived = intent.getStringExtra("public key received");
            byte[] bPublicKeyBytes = PublicKeyReceived.getBytes();

            // Convert the public key bytes into a PublicKey object
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(bPublicKeyBytes);
            KeyFactory keyFact = KeyFactory.getInstance("DH");
            PublicKey bPublicKey = keyFact.generatePublic(x509KeySpec);

            secretKeyStored = (TextView)findViewById(R.id.secretKeyStored);
            secretKeyStored.setText("SecretKey generated and stored");


            // Prepare to generate the secret key with the private key and public key of the other party
            KeyAgreement ka = KeyAgreement.getInstance("DH");
            ka.init(privateKey);
            ka.doPhase(bPublicKey, true);

            // Specify the type of key to generate;
            String algorithm = "AES";

            // Generate the secret key
            SecretKey secretKey = ka.generateSecret(algorithm);

            System.out.println("SecretKey generated:"+secretKey);

            // Use the secret key to encrypt/decrypt data;
        }  catch (InvalidKeyException e) {
        } catch (InvalidKeySpecException e) {
        } catch (InvalidAlgorithmParameterException e) {
        } catch (NoSuchAlgorithmException e) {
        } catch (Exception e) {
                Toast.makeText(getApplicationContext(),
                        "Sending failed, please try again.",
                        Toast.LENGTH_LONG).show();
                e.printStackTrace();
         }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_key_exchange_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Returns a comma-separated string of 3 values.
    // The first number is the prime modulus P.
    // The second number is the base generator G.
    // The third number is bit size of the random exponent L.
    public static String genDhParams() {
        try {

            // Create the parameter generator for a 1024-bit DH key pair
            AlgorithmParameterGenerator paramGen = AlgorithmParameterGenerator.getInstance("DH");
            paramGen.init(1024);

            // Generate the parameters
            AlgorithmParameters params = paramGen.generateParameters();
            DHParameterSpec dhSpec
                    = (DHParameterSpec)params.getParameterSpec(DHParameterSpec.class);

            // Return the three values in a string
            return ""+dhSpec.getP()+","+dhSpec.getG()+","+dhSpec.getL();
        } catch (NoSuchAlgorithmException e) {
        } catch (InvalidParameterSpecException e) {
        }
        return null;
    }

    final private static String asHex (byte[] buf)
    {
        StringBuffer strbuf = new StringBuffer(buf.length * 2);
        int i;

        for (i = 0; i < buf.length; i++)
        {
            if (((int) buf[i] & 0xff) < 0x10)
                strbuf.append("0");

            strbuf.append(Long.toString((int) buf[i] & 0xff, 16));
        }
        return strbuf.toString();
    }

    //private static Key generateKey(String secretKeyString) throws Exception {

        // generate AES secret key from a String
      //  Key key = new SecretKeySpec(secretKeyString.getBytes(), "AES");
        //return key;
    //}

    public static byte[] hex2byte(byte[] b) {
        if ((b.length % 2) != 0)
            throw new IllegalArgumentException("hello");

        byte[] b2 = new byte[b.length / 2];

        for (int n = 0; n < b.length; n += 2) {
            String item = new String(b, n, 2);
            b2[n / 2] = (byte) Integer.parseInt(item, 16);
        }
        return b2;
    }

}
