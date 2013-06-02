package edu.umflix.authenticationhandler.encryption;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * Encrypts and decrypts Strings
 */
public class Encrypter {

    private MessageDigest digest;
    private SecretKeySpec key;
    private Cipher cipher;
    private String passPhrase = "Joffrey-Cersei-IlynPayne";
    private static final Charset ISO_8859_1 = Charset.forName("ISO-8859-1");

    public Encrypter() {
        try {
            digest = MessageDigest.getInstance("SHA");
            digest.update(passPhrase.getBytes());
            key = new SecretKeySpec(digest.digest(), 0, 16, "AES");
            cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e.getMessage());
        } catch (NoSuchPaddingException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    /**
     * Encrypts a String
     * @param clearText the String to encrypt
     * @return encrypted string
     */
    public String encrypt(String clearText) {
        if (clearText != null) {
            try {
                cipher.init(Cipher.ENCRYPT_MODE, key);
                byte[] cipherText = cipher.doFinal(clearText.getBytes());

                return new String(cipherText, ISO_8859_1);

            } catch (InvalidKeyException e) {
                throw new IllegalStateException(e.getMessage());
            } catch (BadPaddingException e) {
                throw new IllegalStateException(e.getMessage());
            } catch (IllegalBlockSizeException e) {
                throw new IllegalStateException(e.getMessage());
            }
        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Decrypts an encrypted string
     * @param cipherText the encrypted string
     * @return decrypted string
     */
    public String decrypt(String cipherText) {
        if (cipherText != null) {
            try {
                cipher.init(Cipher.DECRYPT_MODE, key);
                byte[] text = cipherText.getBytes(ISO_8859_1);
                return new String(cipher.doFinal(text));
            } catch (InvalidKeyException e) {
                throw new IllegalStateException(e.getMessage());
            } catch (BadPaddingException e) {
                throw new IllegalStateException(e.getMessage());
            } catch (IllegalBlockSizeException e) {
                throw new IllegalStateException(e.getMessage());
            }
        } else {
            throw new IllegalArgumentException();
        }

    }

}
