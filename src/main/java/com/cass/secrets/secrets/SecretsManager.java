package com.cass.secrets.secrets;


import com.cass.secrets.exception.EncryptionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.*;
import java.io.*;
import java.lang.reflect.Field;
import java.nio.file.*;
import java.security.*;
import java.util.Base64;
import java.util.Random;

public class SecretsManager {

    public Logger logger = LoggerFactory.getLogger(getClass().getName());

    private static final Random RANDOM = new SecureRandom();
    private static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int SALT_LENGTH = 12;
    private static final String SECRETS_FILE_NAME = "secrets";

    private Path secretsFileLocation = null;
    private Path secretsFileDirLocation = null;
    private SecretsFile secretsFile = null;


    public SecretsManager(String secretsDirPath)  {
        this.secretsFileDirLocation = Paths.get(secretsDirPath);
        this.secretsFileLocation = Paths.get(secretsDirPath + "/" + SECRETS_FILE_NAME);
    }

    // generates public/private keys and secrets file
    public void encrypt(PublicKey publicKey, Object data) throws EncryptionException, FileAlreadyExistsException, IllegalAccessException {
        File secrets = secretsFileLocation.toFile();
        if ( secrets.exists() == true ) {
            throw new FileAlreadyExistsException("Cannot overwrite existing secrets file");
        }

        File secretsDir = secretsFileDirLocation.toFile();
        if ( secretsDir.canWrite() == false) {
            throw new EncryptionException("Cannot write to secrets directory");
        }

        // -----------------------------------
        // Scan object for encrypt-able data
        // -----------------------------------
        try {

            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);

            for (Field field : data.getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(EncryptableField.class)) {
                    String value = (String) field.get(data);
                    if ( value.length() > 117 ) {
                        throw new EncryptionException("Field length too long to encrypt");  // see encrypter function in licensegenerator if needed
                    }
                    String encryptedValue = encryptField(value, cipher);
                    field.set(data, encryptedValue);
                }
            }

        } catch (Exception e) {

            throw new EncryptionException(e.getMessage());

        }

        // ---------------------------------
        // Save encrypted secret data object
        // ---------------------------------
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(data);
            out.flush();

            Files.write(secretsFileLocation, bos.toByteArray(), StandardOpenOption.CREATE_NEW);

            out.close();
            bos.close();

        } catch (IOException e) {
            throw new EncryptionException(e);
        }
    }

    public Object decrypt(PrivateKey privateKey) throws FileNotFoundException, AccessDeniedException, EncryptionException {
        File secrets = secretsFileLocation.toFile();
        if ( secrets.exists() == false ) {
            throw new FileNotFoundException("Secrets file not found");
        }

        File secretsDir = secretsFileDirLocation.toFile();
        if ( secretsDir.canRead() == false) {
            throw new AccessDeniedException("Cannot read secrets directory");
        }

        // ---------------------------------
        // Read encrypted secret data object
        // ---------------------------------
        Object data;

        try {

            FileInputStream fileIn = new FileInputStream(secretsFileLocation.toString());
            ObjectInputStream objectIn = new ObjectInputStream(fileIn);
            data = objectIn.readObject();

        } catch (IOException | ClassNotFoundException e) {
            throw new EncryptionException(e);
        }

        // -----------------------------------
        // Scan object for decrypt-able data
        // -----------------------------------
        try {

            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);

            for (Field field : data.getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(EncryptableField.class)) {
                    String value = (String) field.get(data);
                    String plainTextValue = decryptField(value, cipher);
                    field.set(data, plainTextValue);
                }
            }

        } catch (Exception e) {

            throw new EncryptionException(e.getMessage());

        }

        return data;
    }


    private String encryptField(String inputText, Cipher cipher) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        String plainText = getSalt(SALT_LENGTH) + inputText;
        byte[] plainTextByte = plainText.getBytes();
        byte[] encryptedByte = cipher.doFinal(plainTextByte);
        Base64.Encoder encoder = Base64.getEncoder();
        String encryptedText = encoder.encodeToString(encryptedByte);
        return encryptedText;
    }

    private String decryptField(String encryptedText, Cipher cipher) throws BadPaddingException, IllegalBlockSizeException {
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] encryptedTextByte = decoder.decode(encryptedText);
        byte[] decryptedByte = cipher.doFinal(encryptedTextByte);
        String decryptedText = new String(decryptedByte);
        return decryptedText.substring(SALT_LENGTH);
    }

    private String getSalt(int length) {
        StringBuilder returnValue = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            returnValue.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }

        return new String(returnValue);
    }

}
