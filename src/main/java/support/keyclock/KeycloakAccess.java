package support.keyclock;

import lombok.Getter;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import support.Costants;

/**
 * La seguente classe rappresenta l'authentication server keycloak
 */

@Getter
public enum KeycloakAccess {

    KEYCLOAK_ACCESS;

    private final Keycloak keycloak;

    KeycloakAccess() {
        keycloak = KeycloakBuilder.builder()
                .serverUrl(Costants.AUTHSERVERURL)
                .realm(Costants.REALM)
                .grantType(OAuth2Constants.PASSWORD)
                .clientId(Costants.CLIENTID)
                .clientSecret(Costants.CLIENTSECRET)
                .username(Costants.USERNAMEADMIN)
                .password(Costants.PASSWORDADMIN)
                .build();
    }

}
