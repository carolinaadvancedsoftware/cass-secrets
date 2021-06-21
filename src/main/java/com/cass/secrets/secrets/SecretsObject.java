package com.cass.secrets.secrets;

import java.io.Serializable;

public class SecretsObject implements Serializable {

    private String dbUserPassword;
    private String keycloakPassword;

    public SecretsObject(String dbUserPassword, String keycloakPassword)
    {
        this.dbUserPassword = dbUserPassword;
        this.keycloakPassword = keycloakPassword;
    }

    public String getDbName() {
        return dbUserPassword;
    }

    public String getKeycloakPassword() {
        return keycloakPassword;
    }
}
