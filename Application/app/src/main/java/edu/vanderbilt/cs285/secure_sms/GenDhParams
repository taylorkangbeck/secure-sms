/**
 * Generating a Parameter Set for the Diffie-Hellman Key
 *	Agreement Algorithm
 */
package cs285.vanderbilt.edu.keyexchange;

import java.security.AlgorithmParameterGenerator;
import java.security.AlgorithmParameters;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidParameterSpecException;
import javax.crypto.spec.DHParameterSpec;

/**
 * Created by Lian on 12/2/2014.
 */
public class GenDhParams {

    public GenDhParams() {

        // TODO Auto-generated constructor stub

    }

    /**
     * @param args
     */
    public static void main(String[] args) {

        // TODO Auto-generated method stub

        if(args.length!=1) {
            System.out.println("Usage:GenDhParams <keysize>");
        }
        else{
            try {
                int keySize = Integer.parseInt(args[0]);

                // Create the parameter generator for a 1024-bit DH key pair

                AlgorithmParameterGenerator paramGen = AlgorithmParameterGenerator.getInstance("DH");
                paramGen.init(keySize);

                // Generate the parameters

                AlgorithmParameters params = paramGen.generateParameters();
                DHParameterSpec dhSpec
                        = (DHParameterSpec)params.getParameterSpec(DHParameterSpec.class);

                // Returns a comma-separated string of 3 values.
                // The first number is the prime modulus P.
                // The second number is the base generator G.
                // The third number is bit size of the random exponent L.
                System.out.println(""+dhSpec.getP()+","+dhSpec.getG()+","+dhSpec.getL());
            } catch (NoSuchAlgorithmException e) {
            } catch (InvalidParameterSpecException e) {
            }
        }
    }
}
