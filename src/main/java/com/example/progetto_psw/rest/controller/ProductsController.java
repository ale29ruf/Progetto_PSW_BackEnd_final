package com.example.progetto_psw.rest.controller;


import com.example.progetto_psw.entities.Product;
import com.example.progetto_psw.services.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import support.ResponseMessage;
import support.exceptions.BarCodeAlreadyExistException;
import support.exceptions.NameProductAlreadyExistException;
import support.exceptions.ValidationFailed;

import java.util.List;


@RestController
@RequestMapping("/products")
public class ProductsController {
    @Autowired
    private ProductService productService;

    @PreAuthorize("hasAuthority('admin')")
    @PostMapping("/add")
    public ResponseEntity create(@RequestBody @Valid Product product) { //Attenzione: l'annotazione @Valid non solleva alcuna eccezione
        Product p;
        try {
            p = productService.addProduct(product);
        } catch (BarCodeAlreadyExistException e) {
            return new ResponseEntity<>(new ResponseMessage("BARCODE_ALREADY_EXIST"), HttpStatus.BAD_REQUEST);
        } catch (NameProductAlreadyExistException e) {
            return new ResponseEntity<>(new ResponseMessage("NAME_ALREADY_EXIST"), HttpStatus.BAD_REQUEST);
        } catch (ValidationFailed e) {
            return new ResponseEntity<>(new ResponseMessage("VALIDATION_FAILED"), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(p, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('admin')")
    @GetMapping("/updateQnt")
    public ResponseEntity updateQntProduct(@RequestParam(value = "idProd", required = true) int idProd,
                                           @RequestParam(value = "newQnt", required = true) int qnt) {
        try {
            productService.updateQtProduct(idProd,qnt);
        } catch (ValidationFailed e) {
            return new ResponseEntity<>(new ResponseMessage("VALIDATION_FAILED"), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new ResponseMessage("UPDATE_SUCCESFULLY"), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('admin')")
    @GetMapping
    public List<Product> getAll() {
        return productService.showAllProducts(); //restituisce tutti i prodotti in formato json al client (operazione poco carina infatti solo 'admin puo' farlo)
    }

    @GetMapping("/paged")
    public ResponseEntity getAll(@RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
                                 @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                 @RequestParam(value = "sortBy", defaultValue = "id") String sortBy) {
        List<Product> result = productService.showAllProducts(pageNumber, pageSize, sortBy); //gli oggetti che vengono restituiti sono convertiti da spring automaticamente in json
        if ( result.size() <= 0 ) {
            return new ResponseEntity<>(new ResponseMessage("NO_RESULT"), HttpStatus.OK);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/search_by_name")
    public ResponseEntity getByName(@RequestParam(required = true) String name) { //il parametro nell'url deve chiamarsi "name"
        List<Product> result = productService.showProductsByName(name);
        if ( result.size() <= 0 ) {
            return new ResponseEntity<>(new ResponseMessage("NO_RESULT"), HttpStatus.OK);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


}
