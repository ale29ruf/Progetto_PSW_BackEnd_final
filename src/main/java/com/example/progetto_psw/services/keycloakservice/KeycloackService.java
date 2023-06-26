package com.example.progetto_psw.services.keycloakservice;

import com.example.progetto_psw.entities.User;
import com.example.progetto_psw.repositories.UserRepository;
import com.example.progetto_psw.services.AccountingService;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import support.exceptions.MailUserAlreadyExistsException;
import support.exceptions.UsernameUserAlreadyExistsException;
import support.keyclock.KeycloakAccess;

import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.Collections;

@Service
public class KeycloackService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    AccountingService accountingService;

    private final String role = "user";
    private String email;
    private String userName;
    private String password;

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = {UsernameUserAlreadyExistsException.class, MailUserAlreadyExistsException.class})
    public User addUser(User userE) throws MailUserAlreadyExistsException, UsernameUserAlreadyExistsException {
        email = userE.getEmail();
        userName = userE.getUsername();
        password = userE.getPassword();

        if ( userRepository.existsByEmail(userE.getEmail()) ) {
            throw new MailUserAlreadyExistsException();
        }
        if(userRepository.existsByUsername(userE.getUsername())){
            throw new UsernameUserAlreadyExistsException();
        }

        Keycloak keycloak = KeycloakAccess.KEYCLOAK_ACCESS.getKeycloak();

        UserRepresentation user = new UserRepresentation();
        user.setEnabled(true); //utente è abilitato per usare la piattaforma
        user.setUsername(userName);
        user.setEmail(email);

        user.setAttributes(Collections.singletonMap("origin", Arrays.asList("demo")));

        // Get realm
        RealmResource realmResource = keycloak.realm(KeycloakAccess.KEYCLOAK_ACCESS.getRealm());
        UsersResource usersRessource = realmResource.users();

        Response response = usersRessource.create(user); //crea l'utente
        System.out.printf("Response: %s %s%n", response.getStatus(), response.getStatusInfo());
        System.out.println(response.getLocation());
        String userId = CreatedResponseUtil.getCreatedId(response); //restituisce l'id dell'utente
        System.out.printf("User created with userId: %s%n", userId);

        CredentialRepresentation passwordCred = new CredentialRepresentation();
        passwordCred.setTemporary(false);
        passwordCred.setType(CredentialRepresentation.PASSWORD);
        passwordCred.setValue(password);

        UserResource userResource = usersRessource.get(userId);

        userResource.resetPassword(passwordCred);

        ClientRepresentation app1Client = realmResource.clients().findByClientId(KeycloakAccess.KEYCLOAK_ACCESS.getClientId()).get(0);

        RoleRepresentation userClientRole = realmResource.clients().get(app1Client.getId()).roles().get(role).toRepresentation();

        userResource.roles().clientLevel(app1Client.getId()).add(Arrays.asList(userClientRole));

        return accountingService.verifyUser(userE);
    }



    public UserResource removeUser(User userE) throws MailUserAlreadyExistsException, UsernameUserAlreadyExistsException {
        // Delete User
        //userResource.remove();
        return null;
    }


}
