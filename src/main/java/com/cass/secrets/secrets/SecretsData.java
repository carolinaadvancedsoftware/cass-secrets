package com.cass.secrets.secrets;

import java.io.Serializable;

public class SecretsData implements Serializable {
    private static final long serialVersionUID = 1L;

    @EncryptableField
    public String dbUserPassword;

    @EncryptableField
    public String keycloakPassword;

    @EncryptableField
    public String resourceServerKey;

    @EncryptableField
    public String awsServicesKey;
}
