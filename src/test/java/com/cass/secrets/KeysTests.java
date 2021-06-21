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
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.security.PublicKey;

//@SpringBootTest
class KeysTests {

	private static String defaultDir = System.getProperty("user.dir") + "/run/keys";
	private static File privateKeyFile = Paths.get(defaultDir + "/private.key").toFile();
	private static File publicKeyFile = Paths.get(defaultDir + "/public.key").toFile();

	@BeforeAll
	public static void setup() {
		if ( publicKeyFile.exists() ) {
			publicKeyFile.delete();
		}
		if (privateKeyFile.exists() ) {
			privateKeyFile.delete();
		}
	}

	@Test
	void generateKeyFiles() throws FileAlreadyExistsException, EncryptionException, FileNotFoundException {
		assert privateKeyFile.exists() == false;
		assert publicKeyFile.exists() == false;

		KeysManager keysManager = new KeysManager(defaultDir);
		keysManager.generate();

		assert privateKeyFile.exists() == true;
		assert publicKeyFile.exists() == true;
	}

	@Test
	void loadPublicKey() throws FileNotFoundException {
		assert publicKeyFile.exists() == true;

		KeysManager keysManager = new KeysManager(defaultDir);

		PublicKey publicKey = keysManager.loadPublicKey();

	}

	@Test
	void loadPrivateKey() throws FileNotFoundException {
		assert privateKeyFile.exists() == true;

		KeysManager keysManager = new KeysManager(defaultDir);

		PrivateKey privateKey = keysManager.loadPrivateKey();
	}

	@AfterAll
	public static void cleanUp() {
		if ( publicKeyFile.exists() ) {
			publicKeyFile.delete();
		}
		if (privateKeyFile.exists() ) {
			privateKeyFile.delete();
		}
	}
}
