## Carolina Advanced Software Solutions, Inc.

(c) 2021 Carolina Advanced Software Solutions, Inc.

This program is used to generate secrets and the keys used to encrypted and decrypt those
secrets. You can either generate the keys or generate the secrets. 

### Supported Secrets

1. dbpassword: password for the database.
2. keycloakPassword: password for keycloak server admin user
3. resourceServerKey: key providing entitlement to resource server
4. awsServicesKey: key to providing entitlement to aws services

### Notes

1. Use uuidgen on bash or linux to generate random uuid.
