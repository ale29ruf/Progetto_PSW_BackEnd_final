package com.example.progetto_psw.rest.controller;


import com.example.progetto_psw.entities.Purchase;
import com.example.progetto_psw.entities.User;
import com.example.progetto_psw.services.PurchasingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import support.ResponseMessage;
import support.authentication.Utils;
import support.exceptions.DateWrongRangeException;
import support.exceptions.QuantityProductUnavailableException;
import support.exceptions.UserNotFoundException;

import java.util.Date;
import java.util.List;


@RestController
@RequestMapping("/purchase")
public class PurchasingController {
    @Autowired
    private PurchasingService purchasingService;

    @PreAuthorize("hasAuthority('user')")
    @PostMapping
    @ResponseStatus(code = HttpStatus.OK)
    public ResponseEntity createPurchase(@RequestBody Purchase purchase) { // è buona prassi ritornare l'oggetto inserito
        User u = new User();
        u.setUsername(Utils.getUsername());
        purchase.setBuyer(u);
        try {
            return new ResponseEntity<>(purchasingService.addPurchase(purchase), HttpStatus.OK);
        } catch (QuantityProductUnavailableException e) {
            return new ResponseEntity<>(new ResponseMessage("PRODUCT_QUANTITY_UNAVAILABLE"), HttpStatus.BAD_REQUEST); // realmente il messaggio dovrebbe essrere più esplicativo (es. specificare il prodotto di cui non vi è disponibilità)
        } catch (IllegalArgumentException e){
            return new ResponseEntity<>(new ResponseMessage(e.getMessage()),HttpStatus.BAD_REQUEST);
        } catch (UserNotFoundException e){
            return new ResponseEntity<>(new ResponseMessage("USERNAME_NOT_FOUND"),HttpStatus.BAD_REQUEST);
        }
    }


    @PreAuthorize("hasAuthority('user')")
    @GetMapping("/purchases")
    public ResponseEntity getPurchases() { //l'utente non deve essere passato come parametro ma l'autenticazione deve avvenire tramite token
        User user = new User();
        user.setUsername(Utils.getUsername());
        try {
            return new ResponseEntity<>(purchasingService.getPurchasesByUser(user), HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(new ResponseMessage("USER_NOT_FOUND"), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasAuthority('user')")
    @GetMapping("/{user}/{startDate}/{endDate}")
    public ResponseEntity getPurchasesInPeriod(@PathVariable("startDate") @DateTimeFormat(pattern = "dd-MM-yyyy") Date start,
                                               @PathVariable("endDate") @DateTimeFormat(pattern = "dd-MM-yyyy") Date end) {
        try {
            User u = new User();
            u.setUsername(Utils.getUsername());
            List<Purchase> result = purchasingService.getPurchasesByUserInPeriod(u, start, end);
            if ( result.size() <= 0 ) {
                return new ResponseEntity<>(new ResponseMessage("NO_RESULT"), HttpStatus.OK);
            }
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "USER_NOT_FOUND", e); //catturiamo le specifiche eccezioni -> la buona prassi infatti ci dice di creare e sollevare nei service le specifiche eccezioni
        } catch (DateWrongRangeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "START_DATE_MUST_BE_PREVIUS_END_DATE", e);
        }
    }

    @PreAuthorize("hasAuthority('admin')")
    @GetMapping("/all")
    public ResponseEntity getAllPurchases() { //l'utente non deve essere passato come parametro ma l'autenticazione deve avvenire tramite token
        try {
            return new ResponseEntity<>(purchasingService.getAllPurchases(), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new ResponseMessage("INTERNAL_ERROR"), HttpStatus.BAD_REQUEST);
        }
    }


}
