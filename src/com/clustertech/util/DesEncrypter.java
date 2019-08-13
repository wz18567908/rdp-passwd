package com.clustertech.util;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

final public class DesEncrypter {
    private static DesEncrypter desEncrypter = null;
    private Cipher ecipher = null;
    private Cipher dcipher = null;

    private Logger logger = Logger.getLogger(DesEncrypter.class);
    public DesEncrypter(String passPhrase) {
        try {
            KeySpec keySpec = new DESKeySpec(passPhrase.getBytes());
            SecretKey key = SecretKeyFactory.getInstance("DES").generateSecret(keySpec);

            ecipher = Cipher.getInstance("DES");
            dcipher = Cipher.getInstance("DES");

            ecipher.init(Cipher.ENCRYPT_MODE, key);
            dcipher.init(Cipher.DECRYPT_MODE, key);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException e) {
            logger.error(e.getMessage());
        }
    }

    public String decrypt(String str) {
        byte[] dec = null;
        byte[] utf8 = null;
        String newStr = null;
        dec = Base64.decodeBase64(str);
        try {
            utf8 = dcipher.doFinal(dec);
            newStr = new String(utf8, "UTF8");
        } catch (IllegalBlockSizeException | BadPaddingException | UnsupportedEncodingException e) {
            logger.error("fail to decrypt str :" + str + ", because of " + e.getMessage());
        }
        return newStr;
    }

    public String encrypt(String str) {
        byte[] utf8 = null;
        byte[] enc = null;
        try {
            utf8 = str.getBytes("UTF8");
            enc = ecipher.doFinal(utf8);
        } catch (UnsupportedEncodingException | IllegalBlockSizeException | BadPaddingException e) {
            logger.error(e);
        }
        return new String(Base64.encodeBase64(enc));
    }
    
    public synchronized static DesEncrypter getInstance() {
        if (desEncrypter == null) {
            desEncrypter = new DesEncrypter(".-'/W2@ce03=fky#vw6&H");
        }
        return desEncrypter;
    }

}
