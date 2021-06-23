package com.margsapp.messenger.utils;

import android.content.Context;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class AES {

    private SecretKey secretKey;
    private byte encryptionKey[] = {9, 115, 51, 86, 105, 4, -31, -23, -68, 88, 17, 20, 3,
            -105, 119, -53};
    private Cipher cipher, deCipher;
    private SecretKeySpec secretKeySpec;

    public AES(Context context) {
        try {
            cipher = Cipher.getInstance("AES");
            deCipher = Cipher.getInstance("AES");
        }
        catch (Exception e){
            e.printStackTrace();
            Toast.makeText(context, "Encryption Error!", Toast.LENGTH_SHORT).show();
        }
        secretKeySpec = new SecretKeySpec(encryptionKey, "AES");
    }

    public String Encrypt(String string, Context context){

        byte[] stringByte = string.getBytes();
        byte[] encryptedByte = new byte[stringByte.length];

        try {
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            encryptedByte = cipher.doFinal(stringByte);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
            Toast.makeText(context, "Encryption Error!", Toast.LENGTH_SHORT).show();
        } catch (BadPaddingException e) {
            e.printStackTrace();
            Toast.makeText(context, "Encryption Error!", Toast.LENGTH_SHORT).show();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
            Toast.makeText(context, "Encryption Error!", Toast.LENGTH_SHORT).show();
        }

        String returnString = null;

        try {
            returnString = new String(encryptedByte, "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Toast.makeText(context, "Encryption Error!", Toast.LENGTH_SHORT).show();
        }
        return returnString;
    }

    public String Decrypt(String string, Context context)  {
        byte[] EncryptedByte = new byte[0];
        EncryptedByte = string.getBytes(StandardCharsets.ISO_8859_1);
        String decryptedString = string;

        byte[] decryption;

        try {
            deCipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            decryption = deCipher.doFinal(EncryptedByte);
            decryptedString = new String(decryption);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
            Toast.makeText(context, "Decryption Error! 1", Toast.LENGTH_SHORT).show();
        } catch (BadPaddingException e) {
            e.printStackTrace();
            Toast.makeText(context, "Decryption Error! 2", Toast.LENGTH_SHORT).show();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
            //Toast.makeText(context, "Decryption Error! 3", Toast.LENGTH_SHORT).show();
        }
        return decryptedString;
    }

}


