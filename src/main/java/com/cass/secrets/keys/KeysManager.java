package com.cass.secrets.keys;


import com.cass.secrets.exception.EncryptionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.*;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class KeysManager {

    public Logger logger = LoggerFactory.getLogger(getClass().getName());

    private static final String PUBLIC_KEY_FILE = "public.key";
    private static final String PRIVATE_KEY_FILE = "private.key";


    private Path keysFileDirLocation = null;
    private Path privateKeyFileLocation = null;
    private Path publicKeyFileLocation = null;

    public KeysManager(String keysDirPath)  {
        this.keysFileDirLocation = Paths.get(keysDirPath);
        this.privateKeyFileLocation = Paths.get(keysDirPath + "/" + PRIVATE_KEY_FILE);
        this.publicKeyFileLocation = Paths.get(keysDirPath + "/" + PUBLIC_KEY_FILE);
    }

    // generates public/private keys
    public void generate() throws EncryptionException, FileAlreadyExistsException {
        File publicKeyFile = publicKeyFileLocation.toFile();
        if (publicKeyFile.exists() == true) {
            throw new FileAlreadyExistsException("Cannot overwrite existing public.key file");
        }
        File privateKeyFile = privateKeyFileLocation.toFile();
        if (privateKeyFile.exists() == true) {
            throw new FileAlreadyExistsException("Cannot overwrite existing private.key file");
        }

        File keysDir = keysFileDirLocation.toFile();
        if (keysDir.canWrite() == false) {
            throw new EncryptionException("Cannot write new keys to key directory");
        }

        // -----------------------------------
        // Create key pairs
        // -----------------------------------

        PrivateKey privateKey = null;
        PublicKey publicKey = null;

        try {

            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            privateKey = keyPair.getPrivate();
            publicKey = keyPair.getPublic();

        } catch (Exception e) {

            throw new EncryptionException(e);

        }

        // ---------------------------------
        // Save private key
        // ---------------------------------
        try {

            Files.write(privateKeyFile.toPath(), privateKey.getEncoded(), StandardOpenOption.CREATE_NEW);
            Files.write(publicKeyFile.toPath(), publicKey.getEncoded(), StandardOpenOption.CREATE_NEW);

        } catch (IOException e) {
            logger.error(e.getMessage());
        }

    }

    public PublicKey loadPublicKey() throws FileNotFoundException {
        File publicKeyFile = publicKeyFileLocation.toFile();
        if (publicKeyFile.exists() == false) {
            throw new FileNotFoundException("public.key file does not exist");
        }

        // ---------------------------------
        // Load public key
        // ---------------------------------
        PublicKey publicKey = null;

        try {

            byte[] publicKeyBytes = Files.readAllBytes(publicKeyFile.toPath());
            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            publicKey = keyFactory.generatePublic(publicKeySpec);

        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            logger.error(e.getMessage());
        }

        return publicKey;
    }

    public PrivateKey loadPrivateKey() throws FileNotFoundException {
        File privateKeyFile = privateKeyFileLocation.toFile();
        if (privateKeyFile.exists() == false) {
            throw new FileNotFoundException("private.key file does not exist");
        }

        // ---------------------------------
        // Load public key
        // ---------------------------------
        PrivateKey privateKey = null;

        try {

            byte[] privateKeyBytes = Files.readAllBytes(privateKeyFile.toPath());
            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            privateKey = keyFactory.generatePrivate(privateKeySpec);

        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            logger.error(e.getMessage());
        }

        return privateKey;
    }
}
