package com.example.progetto_psw.services.keycloakservice;

import com.example.progetto_psw.entities.User;
import com.example.progetto_psw.repositories.UserRepository;
import com.example.progetto_psw.services.AccountingService;
import jakarta.persistence.EntityManager;
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
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import support.Costants;
import support.exceptions.*;
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
    @Autowired
    EntityManager entityManager;

    private final String role = "user";

    @Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
            rollbackFor = {MailUserAlreadyExistsException.class, UsernameUserAlreadyExistsException.class, Exception.class, UserConflitException.class})
    public User addUser(User userE) throws UsernameUserAlreadyExistsException, MailUserAlreadyExistsException, UserConflitException {
        String email = userE.getEmail();
        String userName = userE.getUsername();
        String password = userE.getPassword();
        System.out.println("Nuovo utente: email->"+email+", username->"+userName+", password->"+password);

        if ( userRepository.existsByEmail(email) ) {
            throw new MailUserAlreadyExistsException();
        }

        if(userRepository.existsByUsername(userName)){
            throw new UsernameUserAlreadyExistsException();
        }

        User u = null;
        try{
            u = userRepository.save(userE);
        } catch(Exception e){
            throw new UserConflitException();
        }


        Keycloak keycloak = KeycloakAccess.KEYCLOAK_ACCESS.getKeycloak();

        UserRepresentation user = new UserRepresentation();
        user.setEnabled(true); //utente è abilitato per usare la piattaforma
        user.setUsername(userName);
        user.setEmail(email);

        user.setAttributes(Collections.singletonMap("origin", Arrays.asList("demo")));

        // Get realm
        RealmResource realmResource = keycloak.realm(Costants.REALM);
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

        ClientRepresentation app1Client = realmResource.clients().findByClientId(Costants.CLIENTID).get(0);

        RoleRepresentation userClientRole = realmResource.clients().get(app1Client.getId()).roles().get(role).toRepresentation();

        userResource.roles().clientLevel(app1Client.getId()).add(Arrays.asList(userClientRole));

        System.out.println("Utente aggiunto su keycloak");
        return u;
    }

}
