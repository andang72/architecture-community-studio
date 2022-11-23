package tests;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;

public class AesCrypto {

    public static void main(String args[]) throws Exception {

        String encKey = "12345qwert";
        String encryptedString = "ac6ad2041dfd004a83ca60bdac489251"; 
        String decryptedString =  decodeWithAes(encryptedString, encKey);
        System.out.println(String.format("decode : %s -> %s ", encryptedString, decryptedString) ); 


        String plainText = "1234";
        encryptedString =  encodeWithAes(plainText, encKey);
        System.out.println(String.format("encode : %s -> %s ", plainText, encryptedString) ); 
        
    }

    public static String encodeWithAes(String originalString, String encKey) throws Exception {
        byte[] seedB = encKey.getBytes();
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        sr.setSeed(seedB);
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgen.init(128, sr);
        SecretKey skey = kgen.generateKey();
        SecretKeySpec skeySpec = new SecretKeySpec(skey.getEncoded(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(originalString.getBytes());
        return Hex.encodeHexString(encrypted);
    }

    public static String decodeWithAes(String encryptedString, String encKey) throws Exception {
        String rtnVal = ""; 
        byte[] seedB = encKey.getBytes(); 
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        sr.setSeed(seedB); 
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgen.init(128, sr);  
        SecretKey skey = kgen.generateKey();
        SecretKeySpec skeySpec = new SecretKeySpec(skey.getEncoded(), "AES"); 
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        byte[] decrypted = cipher.doFinal(Hex.decodeHex(encryptedString.toCharArray())); 
        rtnVal = new String(decrypted); 
        return rtnVal;
    }

}
