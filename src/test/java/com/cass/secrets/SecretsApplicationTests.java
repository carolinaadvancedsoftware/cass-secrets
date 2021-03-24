package com.cass.secrets;

import com.cass.secrets.exception.EncryptionException;
import com.cass.secrets.secrets.SecretsData;
import com.cass.secrets.secrets.SecretsManager;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Paths;

//@SpringBootTest
class SecretsApplicationTests {

	@Test
	void generateSecret() throws FileAlreadyExistsException, EncryptionException, FileNotFoundException {
		File secretsFile = Paths.get("./run/secrets/secrets").toFile();

		SecretsManager secretsManager = new SecretsManager("./run/secrets");
		secretsManager.generate("casskube");

		SecretsData secretsData = secretsManager.load();

		secretsFile.delete();

		assert secretsData.dbPassword.equalsIgnoreCase("casskube") == true;
	}
}
