package com.example.progetto_psw.rest.authController;

import com.example.progetto_psw.entities.User;
import com.example.progetto_psw.services.keycloakservice.KeycloackService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import support.ResponseMessage;
import support.exceptions.MailUserAlreadyExistsException;
import support.exceptions.UserConflitException;
import support.exceptions.UsernameUserAlreadyExistsException;

@RestController
@CrossOrigin
@RequestMapping("/manage")
public class KeycloackController {

    @Autowired
    KeycloackService keycloackService;

    @PostMapping("/addUser")
    public ResponseEntity addUser(@RequestBody @Valid User user) {
        try{
            return new ResponseEntity<>(keycloackService.addUser(user), HttpStatus.OK);
        } catch(UsernameUserAlreadyExistsException e){
            return new ResponseEntity<>(new ResponseMessage("USERNAME_ALREADY_IN_USE"),HttpStatus.BAD_REQUEST);
        } catch (MailUserAlreadyExistsException e){
            return new ResponseEntity<>(new ResponseMessage("EMAIL_ALREADY_IN_USE"),HttpStatus.BAD_REQUEST);
        } catch (UserConflitException e){
            return new ResponseEntity<>(new ResponseMessage("USER_ALREADY_EXIST"), HttpStatus.FORBIDDEN);
        } catch (Exception e){
            return new ResponseEntity<>(new ResponseMessage(e.getMessage()), HttpStatus.FORBIDDEN);
        }
    }
}
