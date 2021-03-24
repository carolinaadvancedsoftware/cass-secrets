package com.cass.secrets.secrets;


import com.cass.secrets.exception.EncryptionException;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import java.io.*;
import java.nio.file.*;
import java.security.*;
import java.util.Base64;
import java.util.Random;

public class SecretsManager {

    public Logger logger = LoggerFactory.getLogger(getClass().getName());

    // generate() - new key and file
    // load() - load an existing key file

    private Cipher cipher;
    private javax.crypto.SecretKey secretKey;

    private static final Random RANDOM = new SecureRandom();
    private static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int KEY_LENGTH = 256;
    private static final int SALT_LENGTH = 12;
    private static final String SECRETS_FILE_NAME = "secrets";

    private Path secretsFileLocation = null;
    private Path secretsFileDirLocation = null;
    private SecretsFile secretsFile = null;

    public SecretsManager(String secretsDirPath)  {
        this.secretsFileDirLocation = Paths.get(secretsDirPath);
        this.secretsFileLocation = Paths.get(secretsDirPath + "/" + SECRETS_FILE_NAME);
    }

    public void generate(String dbPassword) throws EncryptionException, FileAlreadyExistsException {
        File secrets = secretsFileLocation.toFile();
        if ( secrets.exists() == true ) {
            throw new FileAlreadyExistsException("Cannot overwrite existing secrets file");
        }

        File secretsDir = secretsFileDirLocation.toFile();
        if ( secretsDir.canWrite() == false) {
            throw new EncryptionException("Cannot write new keys to key directory");
        }

        // -----------------------------------
        // Get Cipher algorithm
        // -----------------------------------

        /*
        Cipher Info
        Algorithm : for the encryption of electronic data
        mode of operation : to avoid repeated blocks encrypt to the same values.
                padding: ensuring messages are the proper length necessary for certain ciphers
        mode/padding are not used with stream ciphers.
        */

        try {
            cipher = Cipher.getInstance("AES"); //SunJCE provider AES algorithm, mode(optional) and padding schema(optional)
        } catch (Exception e) {
            throw new EncryptionException(e);
        }

        // -----------------------------------
        // Create a new secret key
        // -----------------------------------

        // KeyGenerator keyGenerator = null;
        KeyGenerator keyGenerator = null;
        try {

            keyGenerator = KeyGenerator.getInstance("AES");

        } catch (Exception e) {

            throw new EncryptionException(e);

        }

        keyGenerator.init(KEY_LENGTH);
        secretKey = keyGenerator.generateKey();

        // ---------------------------------
        // Save private key
        // ---------------------------------
        try {
            SecretsFile secretsFile = new SecretsFile(secretKey.getEncoded());
            secretsFile.setDbPassword(encrypt(dbPassword));

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = null;
            out = new ObjectOutputStream(bos);
            out.writeObject(secretsFile);
            out.flush();

            Files.write(secretsFileLocation, bos.toByteArray(), StandardOpenOption.CREATE_NEW);

            out.close();
            bos.close();

        } catch (IOException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException e) {
            throw new EncryptionException(e);
        }
    }

    public SecretsData load() throws FileNotFoundException, EncryptionException {
        File secrets = secretsFileLocation.toFile();
        if ( secrets.exists() == false ) {
            throw new FileNotFoundException("Secrets file not found");
        }

        File secretsDir = secretsFileDirLocation.toFile();
        if ( secretsDir.canRead() == false) {
            throw new EncryptionException("Cannot read from keys directory");
        }

        try {
            cipher = Cipher.getInstance("AES"); //SunJCE provider AES algorithm, mode(optional) and padding schema(optional)
        } catch (Exception e) {
            throw new EncryptionException(e);
        }

        InputStream is = null;

        SecretsData secretsData = new SecretsData();

        try {

            byte[] encodedBytes = Files.readAllBytes(secretsFileLocation);

            ByteArrayInputStream bis = new ByteArrayInputStream(encodedBytes);
            ObjectInput in = new ObjectInputStream(bis);
            Object o = in.readObject();
            SecretsFile secretsFile = (SecretsFile) o;

            secretsData.dbPassword = decrypt(secretsFile.getDbPassword());

        } catch (IOException| ClassNotFoundException |BadPaddingException | IllegalBlockSizeException | InvalidKeyException e) {
            throw new EncryptionException(e);
        }

        return secretsData;
    }


    private String encrypt(String plainTextPassowrd) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        String plainText = getSalt(SALT_LENGTH) + plainTextPassowrd;
        byte[] plainTextByte = plainText.getBytes();
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedByte = cipher.doFinal(plainTextByte);
        Base64.Encoder encoder = Base64.getEncoder();
        String encryptedText = encoder.encodeToString(encryptedByte);
        return encryptedText;
    }

    private String decrypt(String encryptedText) throws BadPaddingException,
            IllegalBlockSizeException, InvalidKeyException {
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] encryptedTextByte = decoder.decode(encryptedText);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decryptedByte = cipher.doFinal(encryptedTextByte);
        String decryptedText = new String(decryptedByte);
        return decryptedText.substring(SALT_LENGTH);
    }

    private String getSalt(@NotNull int length) {
        StringBuilder returnValue = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            returnValue.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }

        return new String(returnValue);
    }

}
