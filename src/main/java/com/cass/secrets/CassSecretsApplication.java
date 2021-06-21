package com.cass.secrets;

import com.cass.secrets.exception.EncryptionException;
import com.cass.secrets.keys.KeysManager;
import com.cass.secrets.secrets.SecretsData;
import com.cass.secrets.secrets.SecretsManager;
import org.apache.logging.log4j.util.Strings;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.io.FileNotFoundException;
import java.nio.file.FileAlreadyExistsException;
import java.security.InvalidKeyException;
import java.security.PublicKey;

@SpringBootApplication
public class CassSecretsApplication {

	/*
		generatekeys : generate public and private keys
		--keysDirDest <directory to write keys to; default is ./keys directory>

		generatesecrets : generate secrets file
		--secretsDirDest <directory to write secrets to; default is ./secrets directory>
		--dbpassword <clear text password>
		--keyclockPassword <clear text password>
		--resourceServerKey <clear text key>
		--awsServicesKey <clear text key>

	 */

	public static void main(String[] args) throws BadPaddingException, InvalidKeyException, IllegalBlockSizeException, FileAlreadyExistsException, EncryptionException, FileNotFoundException, IllegalAccessException {

		boolean generateKeys = false;

		SecretsData secretsData = new SecretsData();
		String defaultDir = System.getProperty("user.dir") + "/secrets";
		String defaultKeysDir = System.getProperty("user.dir") + "/keys";

		// ------

		if ( args.length == 0 ) {
			System.out.println("Primary option not specified");
			System.exit(1);
		}

		if (args[0].equalsIgnoreCase("generatekeys")) {

			generateKeys = true;

		} else if (args[0].equalsIgnoreCase("generatesecrets") ) {

			generateKeys = false;

		} else {
			System.out.println(String.format("primary option %s is invalid", args[0]));
			System.exit(1);
		}

		// ------

		for ( int i = 1; i < args.length; i++ ) {

			if ( args[i].equalsIgnoreCase("--dbUserPassword") == true ) {
				if ((i + 1) < args.length) {

					secretsData.dbUserPassword = args[i + 1];
					i++;
				} else {
					System.out.println("Insufficient number of arguments");
					System.exit(1);
				}

			} else if (args[i].equalsIgnoreCase("--secretsDirDest") == true) {

				if ((i + 1) < args.length) {

					defaultDir = args[i + 1];
					i++;
				} else {
					System.out.println("Insufficient number of arguments");
					System.exit(1);
				}

			} else if (args[i].equalsIgnoreCase("--keysDirDest") == true) {

				if ((i + 1) < args.length) {

					defaultKeysDir = args[i + 1];
					i++;
				} else {
					System.out.println("Insufficient number of arguments");
					System.exit(1);
				}

			} else if ( args[i].equalsIgnoreCase("--keycloakPassword") == true ) {

					if ((i + 1) < args.length) {

						secretsData.keycloakPassword = args[i + 1];
						i++;
					} else {
						System.out.println("Insufficient number of arguments");
						System.exit(1);
					}

			} else if ( args[i].equalsIgnoreCase("--resourceServerKey") == true ) {

				if ((i + 1) < args.length) {

					secretsData.resourceServerKey = args[i + 1];
					i++;
				} else {
					System.out.println("Insufficient number of arguments");
					System.exit(1);
				}

			} else if ( args[i].equalsIgnoreCase("--awsServicesKey") == true ) {

				if ((i + 1) < args.length) {

					secretsData.awsServicesKey = args[i + 1];
					i++;
				} else {
					System.out.println("Insufficient number of arguments");
					System.exit(1);
				}

			} else {

				System.out.println(String.format("Parameter %s is not unrecognized", args[i]));
				System.exit(1);
			}

		}

		if ( generateKeys == true ) {

			KeysManager keysManager = new KeysManager(defaultKeysDir);
			keysManager.generate();

		} else {

			if ( secretsData.dbUserPassword == null && secretsData.keycloakPassword == null) {
				System.out.println("ERROR: Failed to include any secrets");
				System.exit(1);
			}

			KeysManager keysManager = new KeysManager(defaultKeysDir);
			PublicKey publicKey = keysManager.loadPublicKey();

			SecretsManager secretsManager = new SecretsManager(defaultDir);
			secretsManager.encrypt(publicKey, secretsData);

		}

		System.exit(0);
	}

}
