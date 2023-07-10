package com.example.progetto_psw.rest.controller;

import com.example.progetto_psw.services.CartService;
import jakarta.persistence.OptimisticLockException;
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
    final int MAX_TENTATIVE = 5;

    /**
     * Attenzione: il carrello per un relativo utente viene creato nel momento in cui aggiunge il primo prodotto.
     * Prima di procedere con l'acquisto faccio sempre ricaricare il carrello in modo da diminuire eventuali eccezioni sollevate
     * in fase di acquisto.
     */
    @PreAuthorize("hasAuthority('user')")
    @GetMapping("/get")
    public ResponseEntity getCart(){
        int i = 0;
        try{
            while(i < MAX_TENTATIVE){
                try{
                    return new ResponseEntity<>(cartService.getCart(), HttpStatus.OK);
                } catch(OptimisticLockException e){
                    i++;
                }
            }
        } catch (UserNotFoundException e){
            return new ResponseEntity<>(new ResponseMessage("USERNAME_NOT_FOUND"),HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new ResponseMessage("INNER_ERROR_TRY_LATER"), HttpStatus.OK);
    }

    /**
     * ATTENZIONE: se cerchiamo di aggiungere lo stesso prodotto nello stesso carrello, OptimisticLockException non viene sollevata
     * dato che il responsabile della relazione è ProductInPurchase, dunque non si ha conflitto sulle tuple del carrello.
     * Bisognerebbe, ad esempio, aggiungere una nuova tabella che mantenga la relazione in modo da avere conflitto su quelle
     * tuple oppure rendere il metodo transazionale serializable (poco efficiente).
     */
    @PreAuthorize("hasAuthority('user')")
    @GetMapping("/addProd")
    public ResponseEntity addProductInCart(@RequestParam(value = "idProd", required = true) int idProd){
        int i = 0;
        try{
            while(i < MAX_TENTATIVE){
                try{
                    cartService.addProduct(idProd);
                    return new ResponseEntity<>(new ResponseMessage("OK"), HttpStatus.OK);
                } catch(OptimisticLockException e){
                    i++;
                }
            }
        } catch (IllegalArgumentException e){
            return new ResponseEntity<>(new ResponseMessage("PRODUCT_NOT_EXIST"),HttpStatus.BAD_REQUEST);
        } catch (UserNotFoundException e){
            return new ResponseEntity<>(new ResponseMessage("USERNAME_NOT_FOUND"),HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new ResponseMessage("INNER_ERROR_TRY_LATER"), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('user')")
    @PostMapping("/addAllProd")
    public ResponseEntity addAllProductInCart(@RequestBody List<PipDetails> listaProd){
        int i = 0;
        try{
            while(i < MAX_TENTATIVE){
                try{
                    cartService.addAllProduct(listaProd);
                    return new ResponseEntity<>(new ResponseMessage("OK"), HttpStatus.OK);
                } catch(OptimisticLockException e){
                    i++;
                }
            }
        } catch (IllegalArgumentException e){
            return new ResponseEntity<>(new ResponseMessage("PRODUCT_NOT_EXIST"),HttpStatus.BAD_REQUEST);
        } catch (UserNotFoundException e){
            return new ResponseEntity<>(new ResponseMessage("USERNAME_NOT_FOUND"),HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new ResponseMessage("INNER_ERROR_TRY_LATER"), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('user')")
    @GetMapping("/removeProd")
    public ResponseEntity removeProductInCart(@RequestParam(value = "idProd", required = true) int idProd){
        try{
            cartService.removeProduct(idProd);
        } catch (IllegalArgumentException e){
            return new ResponseEntity<>(new ResponseMessage("PRODUCT_IN_PURCHASE_NOT_EXIST_IN_CART"),HttpStatus.BAD_REQUEST);
        } catch (UserNotFoundException e){
            return new ResponseEntity<>(new ResponseMessage("USERNAME_NOT_FOUND"),HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new ResponseMessage("OK"), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('user')")
    @GetMapping("/removeAllProd")
    public ResponseEntity removeAllProductInCart(){
        try{
            cartService.removeAllProduct();
        } catch (UserNotFoundException e){
            return new ResponseEntity<>(new ResponseMessage("USERNAME_NOT_FOUND"),HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new ResponseMessage("OK"), HttpStatus.OK);
    }

    /**
     * Nei seguenti due metodi OptimisticLockException potrebbe essere sollevata perchè si ha conflitto sulle singole tuple di ProductInPurchase.
     */
    @PreAuthorize("hasAuthority('user')")
    @GetMapping("/plusQntProd")
    public ResponseEntity plusQntProductInCart(@RequestParam(value = "idProd", required = true) int idProd){
        int i = 0;
        try{
            while(i < MAX_TENTATIVE){
                try{
                    cartService.plusQntProduct(idProd);
                    return new ResponseEntity<>(new ResponseMessage("OK"), HttpStatus.OK);
                } catch(OptimisticLockException e){
                    i++;
                }
            }
        } catch (IllegalArgumentException e){
            return new ResponseEntity<>(new ResponseMessage("PRODUCT_IN_PURCHASE_NOT_EXIST_IN_CART"),HttpStatus.BAD_REQUEST);
        } catch (UserNotFoundException e){
            return new ResponseEntity<>(new ResponseMessage("USERNAME_NOT_FOUND"),HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new ResponseMessage("INNER_ERROR_TRY_LATER"), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('user')")
    @GetMapping("/minusQntProd")
    public ResponseEntity minusQntProductInCart(@RequestParam(value = "idProd", required = true) int idProd){
        int i = 0;
        try{
            while(i < MAX_TENTATIVE){
                try{
                    cartService.minusQntProduct(idProd);
                    return new ResponseEntity<>(new ResponseMessage("OK"), HttpStatus.OK);
                } catch(OptimisticLockException e){
                    i++;
                }
            }
        } catch (IllegalArgumentException e){
            return new ResponseEntity<>(new ResponseMessage("PRODUCT_IN_PURCHASE_NOT_EXIST_IN_CART"),HttpStatus.BAD_REQUEST);
        } catch (UserNotFoundException e){
            return new ResponseEntity<>(new ResponseMessage("USERNAME_NOT_FOUND"),HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new ResponseMessage("INNER_ERROR_TRY_LATER"), HttpStatus.OK);
    }

}
