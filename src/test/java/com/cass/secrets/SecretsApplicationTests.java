package com.cass.secrets;


import com.cass.secrets.exception.EncryptionException;
import com.cass.secrets.keys.KeysManager;
import com.cass.secrets.secrets.SecretsData;
import com.cass.secrets.secrets.SecretsManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.AccessDeniedException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.security.PublicKey;

//@SpringBootTest
class SecretsApplicationTests {

    private static String defaultSecretsDir = System.getProperty("user.dir") + "/run/secrets";
    private static File secretsFile = Paths.get(defaultSecretsDir + "/secrets").toFile();

    private static String defaultKeysDir = System.getProperty("user.dir") + "/run/keys";
    private static File privateKeyFile = Paths.get(defaultKeysDir + "/private.key").toFile();
    private static File publicKeyFile = Paths.get(defaultKeysDir + "/public.key").toFile();

    private static PublicKey publicKey;
    private static PrivateKey privateKey;

    @BeforeAll
    public static void setup() throws FileAlreadyExistsException, EncryptionException, FileNotFoundException {

        if ( publicKeyFile.exists() == true ) {
            publicKeyFile.delete();
        }
        if (privateKeyFile.exists() == true ) {
            privateKeyFile.delete();
        }
        if (secretsFile.exists() == true ) {
            secretsFile.delete();
        }

        // Generate keys for all tests
        KeysManager keysManager = new KeysManager(defaultKeysDir);
		keysManager.generate();

        publicKey = keysManager.loadPublicKey();
        privateKey = keysManager.loadPrivateKey();
    }

	@Test
	void generateSecret() throws FileAlreadyExistsException, EncryptionException, FileNotFoundException, IllegalAccessException {

        SecretsData secretsData = new SecretsData();
        secretsData.dbUserPassword = "casskube";

		SecretsManager secretsManager = new SecretsManager(defaultSecretsDir);
        secretsManager.encrypt(publicKey, secretsData);

        assert secretsFile.exists() == true;

        secretsFile.delete();
	}

	@Test
    public void useSecret() throws FileAlreadyExistsException, EncryptionException, IllegalAccessException, FileNotFoundException, AccessDeniedException {

        SecretsData secretsData = new SecretsData();
        secretsData.dbUserPassword = "casskube";

        SecretsManager secretsManager = new SecretsManager(defaultSecretsDir);
        secretsManager.encrypt(publicKey, secretsData);

        assert secretsFile.exists() == true;

        secretsManager = new SecretsManager(defaultSecretsDir);
        Object data = secretsManager.decrypt(privateKey);

        secretsData = (SecretsData) data;

        assert secretsData.dbUserPassword.equalsIgnoreCase("casskube") == true;
    }

    @AfterAll
    public static void cleanup() {
        if ( publicKeyFile.exists() ) {
            publicKeyFile.delete();
        }
        if (privateKeyFile.exists() ) {
            privateKeyFile.delete();
        }
        if ( secretsFile.exists() ) {
            secretsFile.delete();
        }
    }

}
