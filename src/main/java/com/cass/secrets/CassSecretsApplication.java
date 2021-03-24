package com.cass.secrets;

import com.cass.secrets.exception.EncryptionException;
import com.cass.secrets.secrets.SecretsData;
import com.cass.secrets.secrets.SecretsManager;
import org.apache.logging.log4j.util.Strings;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.nio.file.FileAlreadyExistsException;
import java.security.InvalidKeyException;

@SpringBootApplication
public class CassSecretsApplication {

	// --dbpassword <clear text password>

	public static void main(String[] args) throws BadPaddingException, InvalidKeyException, IllegalBlockSizeException {

		String dbUserPassword = null;
		String defaultDir = System.getProperty("user.dir") + "/secrets";

		for ( int i = 0; i < args.length; i++ ) {
			if ( args[i].equalsIgnoreCase("--dbUserPassword") == true ) {
				if ((i + 1) < args.length) {

					dbUserPassword = args[i + 1];
					i++;
				} else {
					System.out.println("Insufficient number of arguments");
					System.exit(1);
				}

			} else if (args[i].equalsIgnoreCase("--placeSecretsDir") == true) {

				if ((i + 1) < args.length) {

					defaultDir = args[i + 1];
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

		SecretsManager secretsManager = new SecretsManager(defaultDir);

		try {
			secretsManager.generate(dbUserPassword);
		} catch (EncryptionException e) {
			e.printStackTrace();
		} catch (FileAlreadyExistsException e) {
			e.printStackTrace();
		}


		System.exit(0);
	}

}
