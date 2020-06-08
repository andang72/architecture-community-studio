package tests;

import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
 
public class Crypto2Test {
 
    public static void main(String args[]) throws Exception {
        String ciphertext = "rV2MW3IoCfcCHMa6cZuCcDJpOnOhHDS4R8cOXEL+Z4kqYiBqYbG+zVJNfNUZrKDI"; 
        String passPhrase = "1234";
        String salt = "18b00b2fc5f0e0ee40447bba4dabc952"; 
        String iv = "4378110db6392f93e95d5159dabdee9b";
        String decrypted = decrypt(salt, iv, passPhrase, ciphertext, 10000, 128);
        System.out.println(decrypted);
    }
    
    public static String decrypt(String salt, String iv, String passphrase, String ciphertext, int iterationCount, int keySize) throws Exception {        
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        KeySpec spec = new PBEKeySpec(passphrase.toCharArray(), Hex.decodeHex(salt.toCharArray()), iterationCount, keySize);
        SecretKey key = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");        
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(Hex.decodeHex(iv.toCharArray())));        
        byte[] decrypted = cipher.doFinal(Base64.decodeBase64(ciphertext));        
        return new String(decrypted, "UTF-8");
    }
 
 
}
