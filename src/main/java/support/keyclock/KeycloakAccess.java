package support.keyclock;

import lombok.Getter;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;

/**
 * La seguente classe rappresenta l'authentication server keycloak
 */

@Getter
public enum KeycloakAccess {

    KEYCLOAK_ACCESS;

    //Se specifico le costanti nel file yaml non vengono considerate
    private final String usernameAdmin = "ale";
    private final String passwordAdmin = "computer";
    private final String clientId = "server-store";
    private final String serverUrl = "http://localhost:8180/";
    private final String realm = "realm_prog";
    private final String clientSecret = "nuMDmf301DJgDJrx8UvCIoW47BWl8rv8";
    private final Keycloak keycloak;

    KeycloakAccess() {
        keycloak = KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(realm)
                .grantType(OAuth2Constants.PASSWORD)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .username(usernameAdmin)
                .password(passwordAdmin)
                .build();
    }

}
