package com.example.progetto_psw.rest.authController;

import com.example.progetto_psw.entities.User;
import com.example.progetto_psw.services.keycloakservice.KeycloackService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import support.ResponseMessage;
import support.exceptions.MailUserAlreadyExistsException;
import support.exceptions.UsernameUserAlreadyExistsException;

@RestController
@RequestMapping("/manage")
public class KeycloackController {

    @Autowired
    KeycloackService keycloackService;

    @PostMapping("/addUser")
    public ResponseEntity addUser(@RequestBody @Valid User user) {
        try{
            return new ResponseEntity<>(keycloackService.addUser(user), HttpStatus.OK);
        } catch(UsernameUserAlreadyExistsException e){
            return new ResponseEntity<>(new ResponseMessage("Username già in uso"),HttpStatus.BAD_REQUEST);
        } catch (MailUserAlreadyExistsException e){
            return new ResponseEntity<>(new ResponseMessage("E-mail già in uso"),HttpStatus.BAD_REQUEST);
        }
    }
}
