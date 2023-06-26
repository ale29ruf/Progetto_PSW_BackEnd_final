package com.example.progetto_psw.rest.freeController;


import com.example.progetto_psw.entities.User;
import com.example.progetto_psw.services.AccountingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import support.ResponseMessage;
import support.exceptions.MailUserAlreadyExistsException;

import java.util.List;


@RestController //unione di @Controller e @ResponseBody
@RequestMapping("/users")
public class AccountingController {
    @Autowired
    private AccountingService accountingService;

    @PreAuthorize("hasAuthority('admin')")
    @PostMapping
    public ResponseEntity createNewUser(@RequestBody @Valid User user) { //l'oggetto JSON nel payload della richiesta viene convertito in un'entità del dominio applicando le regole di validazione nell'entità
                                                                         //Ovviamente lato client i controllo sui campi devono essere sempre fatti a prescindere.
        try {
            User added = accountingService.registerUser(user);
            return new ResponseEntity(added, HttpStatus.OK);
        } catch (MailUserAlreadyExistsException e) {
            return new ResponseEntity<>(new ResponseMessage("ERROR_MAIL_USER_ALREADY_EXISTS"), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasAuthority('admin')")
    @GetMapping
    public List<User> getAll() {
        return accountingService.getAllUsers();
    }



}
