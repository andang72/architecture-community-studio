package tests;

import java.security.SecureRandom;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.junit.Test;
 
public class AesCryptoTest {
 
    public static void main(String args[]) throws Exception {
        //String ciphertext =  "CocISsg6bM1nEyIYDA+kpKL1z3/kAugZvVg4QHaoNPxbd4qbqIdBFviiIrlLp79w"; 
       String ciphertext =  "CocISsg6bM1nEyIYDA+kpKL1z3/kAugZvVg4QHaoNPxbd4qbqIdBFviiIrlLp79w" ; 
        String passPhrase = "1234";
        String salt = "947e41cb1fe5783b1462013ffd2d03ed"; //"18b00b2fc5f0e0ee40447bba4dabc952"; 
        String iv = "1b5ded7b4e2dfd0ae7c19d65960e0a7f"; //"4378110db6392f93e95d5159dabdee9b";
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
 
    @Test
    public void testASE() throws Exception{
        
        //String originalString = "";
        String key = "podonamu!@#45";
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG"); //SHA1PRNG
        sr.setSeed(key.getBytes());

        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgen.init(128, sr);

        // Generate the secret key specs.
		SecretKey skey = kgen.generateKey();
		String keyString = Hex.encodeHexString(skey.getEncoded());
		SecretKeySpec skeySpec = new SecretKeySpec(skey.getEncoded(), "AES");
		
		Cipher cipher = Cipher.getInstance("AES");
		//cipher.init(Cipher.ENCRYPT_MODE, skeySpec);

        //byte[] encrypted = cipher.doFinal(originalString.getBytes());
        //String encryptedString = Hex.encodeHexString(encrypted);

        //System.out.println( encryptedString );
        String encryptedString=  "30014d19ee773e16df38d863e7d2db3a";

        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        byte[] decrypted = cipher.doFinal(Hex.decodeHex(encryptedString.toCharArray()));
        String decryptedString = new String(decrypted);
        System.out.println( decryptedString );
    }

    @Test
    public void testASE2() throws Exception {
        String iv  = "1d6798fae3c1-88f" ;// "1d6798fae3c1-88ff-4724-28e2-0bcbe9c1";
       // String iv = key.substring(0, 16);
        SecretKeySpec keySpec = new SecretKeySpec(iv.getBytes("UTF-8"), "AES");
        System.out.println(iv);
        String plainText = "hrd";
        Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
        c.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(iv.getBytes()));
        byte[] encrypted = c.doFinal(plainText.getBytes("UTF-8"));
        String encryptedString = new String(Base64.encodeBase64(encrypted));
        System.out.println(encryptedString);

        
        c.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(iv.getBytes()));
        byte[] decoded = Base64.decodeBase64(encryptedString);
        String decodedString = new String(c.doFinal(decoded), "UTF-8");
        System.out.println(decodedString);
    }
 
}
