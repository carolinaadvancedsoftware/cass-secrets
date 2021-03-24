package com.cass.secrets.secrets;

import java.io.Serializable;

public class SecretsObject implements Serializable {

    private String dbUserPassword;

    public SecretsObject(String dbUserPassword) {
        this.dbUserPassword = dbUserPassword;
    }

    public String getDbName() {
        return dbUserPassword;
    }
}
