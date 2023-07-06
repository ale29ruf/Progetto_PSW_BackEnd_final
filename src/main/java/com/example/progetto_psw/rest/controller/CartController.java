package com.example.progetto_psw.rest.controller;

import com.example.progetto_psw.entities.Cart;
import com.example.progetto_psw.entities.Product;
import com.example.progetto_psw.services.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import support.PipDetails;
import support.ResponseMessage;
import support.exceptions.UserNotFoundException;

import java.util.List;

@RestController //unione di @Controller e @ResponseBody
@RequestMapping("/cart")
public class CartController {

    @Autowired
    CartService cartService;

    /**
     * Attenzione: il carrello per un relativo utente viene creato nel momento in cui aggiunge il primo prodotto.
     * Prima di procedere con l'acquisto faccio sempre ricaricare il carrello in modo da diminuire eventuali eccezioni sollevate.
     */
    @PreAuthorize("hasAuthority('user')")
    @GetMapping("/get")
    public ResponseEntity getCart(){
        Cart cart;
        try{
            cart = cartService.getCart();
        } catch (UserNotFoundException e){
            return new ResponseEntity<>(new ResponseMessage("USERNAME_NOT_FOUND"),HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(cart, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('user')")
    @GetMapping("/addProd")
    public ResponseEntity addProductInCart(@RequestParam(value = "idProd", required = true) int idProd){
        try{
            cartService.addProduct(idProd);
        } catch (IllegalArgumentException e){
            return new ResponseEntity<>(new ResponseMessage("PRODUCT_NOT_EXIST"),HttpStatus.BAD_REQUEST);
        } catch (UserNotFoundException e){
            return new ResponseEntity<>(new ResponseMessage("USERNAME_NOT_FOUND"),HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new ResponseMessage("OK"), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('user')")
    @PostMapping("/addAllProd")
    public ResponseEntity addProductInCart(@RequestBody List<PipDetails> listaProd){
        try{
            cartService.addAllProduct(listaProd);
        } catch (IllegalArgumentException e){
            return new ResponseEntity<>(new ResponseMessage("PRODUCT_NOT_EXIST"),HttpStatus.BAD_REQUEST);
        } catch (UserNotFoundException e){
            return new ResponseEntity<>(new ResponseMessage("USERNAME_NOT_FOUND"),HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new ResponseMessage("OK"), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('user')")
    @GetMapping("/removeProd")
    public ResponseEntity removeProductInCart(@RequestParam(value = "idProd", required = true) int idProd){
        try{
            cartService.removeProduct(idProd);
        } catch (IllegalArgumentException e){
            return new ResponseEntity<>(new ResponseMessage("PRODUCT_DOESNT_EXIST"),HttpStatus.BAD_REQUEST);
        } catch (UserNotFoundException e){
            return new ResponseEntity<>(new ResponseMessage("USERNAME_NOT_FOUND"),HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new ResponseMessage("OK"), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('user')")
    @GetMapping("/removeAllProd")
    public ResponseEntity removeProductInCart(){
        try{
            cartService.removeAllProduct();
        } catch (UserNotFoundException e){
            return new ResponseEntity<>(new ResponseMessage("USERNAME_NOT_FOUND"),HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new ResponseMessage("OK"), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('user')")
    @GetMapping("/plusQntProd")
    public ResponseEntity plusQntProductInCart(@RequestParam(value = "idProd", required = true) int idProd){
        try{
            cartService.plusQntProduct(idProd);
        } catch (IllegalArgumentException e){
            return new ResponseEntity<>(new ResponseMessage("PRODUCT_IN_PURCHASE_NOT_EXIST_IN_CART"),HttpStatus.BAD_REQUEST);
        } catch (UserNotFoundException e){
            return new ResponseEntity<>(new ResponseMessage("USERNAME_NOT_FOUND"),HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new ResponseMessage("OK"), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('user')")
    @GetMapping("/minusQntProd")
    public ResponseEntity minusQntProductInCart(@RequestParam(value = "idProd", required = true) int idProd){
        try{
            cartService.minusQntProduct(idProd);
        } catch (IllegalArgumentException e){
            return new ResponseEntity<>(new ResponseMessage("PRODUCT_IN_PURCHASE_NOT_EXIST_IN_CART"),HttpStatus.BAD_REQUEST);
        } catch (UserNotFoundException e){
            return new ResponseEntity<>(new ResponseMessage("USERNAME_NOT_FOUND"),HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new ResponseMessage("OK"), HttpStatus.OK);
    }

}
