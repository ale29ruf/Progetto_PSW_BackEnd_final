package com.example.progetto_psw.rest.freeController;


import com.example.progetto_psw.entities.Purchase;
import com.example.progetto_psw.entities.User;
import com.example.progetto_psw.services.PurchasingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import support.ResponseMessage;
import support.exceptions.DateWrongRangeException;
import support.exceptions.QuantityProductUnavailableException;
import support.exceptions.UserNotFoundException;

import java.util.Date;
import java.util.List;


@RestController
@RequestMapping("/purchases")
public class PurchasingController {
    @Autowired
    private PurchasingService purchasingService;

    @PostMapping
    @ResponseStatus(code = HttpStatus.OK)
    public ResponseEntity create(@RequestBody  Purchase purchase) { // è buona prassi ritornare l'oggetto inserito
        try {
            return new ResponseEntity<>(purchasingService.addPurchaseAle(purchase), HttpStatus.OK);
        } catch (QuantityProductUnavailableException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product quantity unavailable!", e); // realmente il messaggio dovrebbe essrere più esplicativo (es. specificare il prodotto di cui non vi è disponibilità)
        }
    }

    @GetMapping("/{user}")
    public List<Purchase> getPurchases(@RequestBody  User user) { //l'utente non deve essere passato come parametro ma l'autenticazione deve avvenire tramite token
        try {
            return purchasingService.getPurchasesByUser(user);
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found!", e);
        }
    }

    @GetMapping("/{user}/{startDate}/{endDate}")
    public ResponseEntity getPurchasesInPeriod(@PathVariable("user") User user, @PathVariable("startDate") @DateTimeFormat(pattern = "dd-MM-yyyy") Date start, @PathVariable("endDate") @DateTimeFormat(pattern = "dd-MM-yyyy") Date end) {
        try {
            List<Purchase> result = purchasingService.getPurchasesByUserInPeriod(user, start, end);
            if ( result.size() <= 0 ) {
                return new ResponseEntity<>(new ResponseMessage("No results!"), HttpStatus.OK);
            }
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found XXX!", e); //catturiamo le specifiche eccezioni -> la buona prassi infatti ci dice di creare e sollevare nei service le specifiche eccezioni
        } catch (DateWrongRangeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start date must be previous end date XXX!", e);
        }
    }


}
