package ca.philipyoung.philssampler.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/*
Advanced Encryption System (AES)
Demonstrates the use of symmetric encryption on Android (AES-256)
From: Enterprise Android (Mednieks, Meike, Dornin, Pan) Chapter 12
 */
public class AdvancedEncryptionSystemHelper {
    private String padding =
            "ISO10126Padding"; //"ISO10126Padding", "PKCS5Padding"
    private byte[] iv;
    private byte[] key;
    private Cipher encryptCipher;
    private Cipher decryptCipher;

    public AdvancedEncryptionSystemHelper() {
    }

    public AdvancedEncryptionSystemHelper(byte[] key, byte[] iv) throws Exception {
        this.key = key;
        this.iv = iv;
        initEncryptor();
        initDecryptor();
    }

    private void initEncryptor() throws NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException,
            InvalidAlgorithmParameterException {
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        // Initialize the encryption cipher used to write encryption bytes:
        encryptCipher = Cipher.getInstance("AES/CBC/" + padding);
        encryptCipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
    }

    private void initDecryptor() throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        //   Initialize the decryption cipher used to write encryption bytes:
        decryptCipher = Cipher.getInstance("AES/CBC/" + padding);
        decryptCipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
    }

    /*
    This is a generic method for encrypting an array of bytes and it works by writing all bytes to a
CipherInputStream that has been confi gured for AES. The encrypted bytes collect into a byte array
output stream, which is converted into a byte[] as a return value.
     */
    public byte[] encrypt(byte[] dataBytes) throws IOException {
        ByteArrayInputStream bIn =
                new ByteArrayInputStream(dataBytes);
        @SuppressWarnings("resource")
        CipherInputStream cIn =
                new CipherInputStream(bIn, encryptCipher);
        ByteArrayOutputStream bOut =
                new ByteArrayOutputStream();
        int ch;
        while ((ch = cIn.read()) >= 0) {
            bOut.write(ch);
        }
        return bOut.toByteArray();
    }

    /*
    Here is another generic method, this time for decrypting an array of bytes; it works by writing all
bytes to a CipherOutputStream that has been confi gured for AES. As before, the encrypted bytes
collect into a byte array output stream, which is converted into a byte[] as a return value.
     */
    public byte[] decrypt(byte[] dataBytes) throws IOException {
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        CipherOutputStream cOut =
                new CipherOutputStream(bOut, decryptCipher);
        cOut.write(dataBytes);
        cOut.close();
        return bOut.toByteArray();
    }

    // Return a generated encryption key string for personal backup.
    public String generatedEncryptionKey() {
        final String strKeyCodes = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            builder.append(strKeyCodes.charAt((int) (Math.random() * strKeyCodes.length())));
        }
        return builder.toString();
    }

}
