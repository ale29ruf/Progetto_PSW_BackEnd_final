package com.example.progetto_psw.rest.controller;


import com.example.progetto_psw.entities.Purchase;
import com.example.progetto_psw.entities.User;
import com.example.progetto_psw.services.PurchasingService;
import jakarta.persistence.OptimisticLockException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import support.PipDetails;
import support.ResponseMessage;
import support.authentication.Utils;
import support.exceptions.*;

import javax.validation.ConstraintViolationException;
import java.util.Date;
import java.util.List;


@RestController
@RequestMapping("/purchase")
public class PurchasingController {
    @Autowired
    private PurchasingService purchasingService;
    final int MAX_TENTATIVE = 5;

    /**
     * Consento solo agli utenti loggati di effettuare acquisti.
     * ATTENZIONE: Se si sta processando un acquisto c'è la possibilità che un nuovo prodotto venga aggiunto al carrello. Tuttavia
     * non è un problema tanto non viene considerato nell'acquisto attuale. Magari nel successivo.
     */
    @PreAuthorize("hasAuthority('user')")
    @PostMapping
    @ResponseStatus(code = HttpStatus.OK)
    public ResponseEntity createPurchase(@RequestBody List<PipDetails> pipDetailsList) { // è buona prassi ritornare l'oggetto inserito
        Purchase result;
        int i = 0;
        try {
            while(i < MAX_TENTATIVE){
                try{
                    result = purchasingService.addPurchase(pipDetailsList);
                    return new ResponseEntity<>(result, HttpStatus.OK); //Ritorno al client un resoconto dell'acquisto
                } catch(OptimisticLockException e){
                    i++;
                }
            }
        } catch (QuantityProductUnavailableException e) {
            return new ResponseEntity<>(new ResponseMessage("PRODUCT_"+e.getName()+"_QUANTITY_UNAVAILABLE"), HttpStatus.BAD_REQUEST); // Realmente il messaggio dovrebbe essrere più esplicativo (es. specificare il prodotto di cui non vi è disponibilità)
        } catch (IllegalArgumentException e){
            return new ResponseEntity<>(new ResponseMessage(e.getMessage()),HttpStatus.BAD_REQUEST);
        } catch (UserNotFoundException e){
            return new ResponseEntity<>(new ResponseMessage("USERNAME_NOT_FOUND"),HttpStatus.BAD_REQUEST);
        } catch (PriceChangedException e){
            return new ResponseEntity<>(new ResponseMessage("PRODUCT_"+e.getName()+"_PRICE_UNAVAILABLE"),HttpStatus.BAD_REQUEST);
        } catch (InconsistencyCartException e) {
            return new ResponseEntity<>(new ResponseMessage(e.getMsg()),HttpStatus.BAD_REQUEST);
        } catch (ConstraintViolationException e){ // l'eccezione viene sollevata nel caso in cui si prova a inserire un prodotto che non rispetta i vincoli sugli attributi
            return new ResponseEntity<>(new ResponseMessage("INTERNAL_ERROR_TRY_LATER"),HttpStatus.OK); //Per precauzione
        }
        return new ResponseEntity<>(new ResponseMessage("INNER_ERROR_TRY_LATER"), HttpStatus.OK);
    }


    @PreAuthorize("hasAuthority('user')")
    @GetMapping("/purchases")
    public ResponseEntity getPurchases() {
        try {
            return new ResponseEntity<>(purchasingService.getPurchasesByUser(), HttpStatus.OK);
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
