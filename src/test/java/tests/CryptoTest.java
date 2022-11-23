package tests;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class CryptoTest {

    private String securityKey = "12345qwert";
    private static final String DEFAULT_TRANSFORMATION = "AES/CBC/PKCS5Padding";

    public static void main(String args[]) throws Exception {
        
        CryptoTest test = new CryptoTest();

        Gson gson = new Gson();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("username", "12345qwert");
        jsonObject.addProperty("name", "홍길동");
        String plainJsonText = gson.toJson(jsonObject);
        String encodedJson = test.encrypt(plainJsonText);
        String decodedJson = test.decrypt(encodedJson);
        System.out.println(  String.format("encoding %s -> %s", plainJsonText, encodedJson ));
        System.out.println(  String.format("decoding %s -> %s", encodedJson, decodedJson ));
    }

    private String getKey(){
        return StringUtils.rightPad(securityKey, 32, "0");
    }
    private String getIV(){ 
       return getKey().substring(0, 16); 
    }
    
    public String encrypt(String text) throws Exception {
        Cipher cipher = Cipher.getInstance(DEFAULT_TRANSFORMATION);
        SecretKeySpec keySpec = new SecretKeySpec(getKey().getBytes(), "AES");
        IvParameterSpec ivParamSpec = new IvParameterSpec(getIV().getBytes());
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivParamSpec);

        byte[] encrypted = cipher.doFinal(text.getBytes("UTF-8"));
        return Base64.encodeBase64String(encrypted);
    }

    public String decrypt(String cipherText) throws Exception {
        Cipher cipher = Cipher.getInstance(DEFAULT_TRANSFORMATION);
        SecretKeySpec keySpec = new SecretKeySpec(getKey().getBytes(), "AES");
        IvParameterSpec ivParamSpec = new IvParameterSpec(getIV().getBytes());
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivParamSpec);
        byte[] decodedBytes = Base64.decodeBase64(cipherText);
        byte[] decrypted = cipher.doFinal(decodedBytes);
        return new String(decrypted, "UTF-8");
    }

}
