package com.example.progetto_psw.rest.controller;

import com.example.progetto_psw.entities.Cart;
import com.example.progetto_psw.services.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import support.ResponseMessage;
import support.exceptions.UserNotFoundException;

@RestController //unione di @Controller e @ResponseBody
@RequestMapping("/cart")
public class CartController {

    @Autowired
    CartService cartService;

    @PreAuthorize("hasAuthority('user')")
    @PostMapping("/get")
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
    @PostMapping("/addProd")
    public ResponseEntity addProductInCart(@RequestParam(value = "idProd", required = true) int idProd){
        try{
            cartService.addProduct(idProd);
        } catch (IllegalArgumentException e){
            return new ResponseEntity<>(new ResponseMessage(e.getMessage()),HttpStatus.BAD_REQUEST);
        } catch (UserNotFoundException e){
            return new ResponseEntity<>(new ResponseMessage("USERNAME_NOT_FOUND"),HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new ResponseMessage("OK"), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('user')")
    @PostMapping("/removeProd")
    public ResponseEntity removeProductInCart(@RequestParam(value = "idProd", required = true) int idProd){
        try{
            cartService.removeProduct(idProd);
        } catch (IllegalArgumentException e){
            return new ResponseEntity<>(new ResponseMessage(e.getMessage()),HttpStatus.BAD_REQUEST);
        } catch (UserNotFoundException e){
            return new ResponseEntity<>(new ResponseMessage("USERNAME_NOT_FOUND"),HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new ResponseMessage("OK"), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('user')")
    @PostMapping("/plusQntProd")
    public ResponseEntity plusQntProductInCart(@RequestParam(value = "idProd", required = true) int idProd){
        try{
            cartService.plusQntProduct(idProd);
        } catch (IllegalArgumentException e){
            return new ResponseEntity<>(new ResponseMessage(e.getMessage()),HttpStatus.BAD_REQUEST);
        } catch (UserNotFoundException e){
            return new ResponseEntity<>(new ResponseMessage("USERNAME_NOT_FOUND"),HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new ResponseMessage("OK"), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('user')")
    @PostMapping("/minusQntProd")
    public ResponseEntity minusQntProductInCart(@RequestParam(value = "idProd", required = true) int idProd){
        try{
            cartService.minusQntProduct(idProd);
        } catch (IllegalArgumentException e){
            return new ResponseEntity<>(new ResponseMessage(e.getMessage()),HttpStatus.BAD_REQUEST);
        } catch (UserNotFoundException e){
            return new ResponseEntity<>(new ResponseMessage("USERNAME_NOT_FOUND"),HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new ResponseMessage("OK"), HttpStatus.OK);
    }

}
