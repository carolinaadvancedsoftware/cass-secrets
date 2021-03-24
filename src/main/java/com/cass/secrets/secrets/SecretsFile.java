package com.cass.secrets.secrets;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.io.Serializable;
import java.security.InvalidKeyException;

public class SecretsFile implements Serializable {
    private static final long serialVersionUID = 1L;
    private byte[] encodedPrivateKey;
    private String dbPassword;

    public SecretsFile(byte[] encodedPrivateKey) {

        this.encodedPrivateKey = encodedPrivateKey;
    }

    public String getDbPassword() {
        return this.dbPassword;
    }

    public void setDbPassword(String dbPassword) throws BadPaddingException, InvalidKeyException, IllegalBlockSizeException {
        this.dbPassword = dbPassword;
    }
}
