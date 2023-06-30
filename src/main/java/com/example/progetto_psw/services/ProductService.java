package com.example.progetto_psw.services;

import com.example.progetto_psw.entities.Categories;
import com.example.progetto_psw.entities.Product;
import com.example.progetto_psw.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import support.exceptions.BarCodeAlreadyExistException;
import support.exceptions.NameProductAlreadyExistException;
import support.exceptions.ValidationFailed;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;


    @Transactional(readOnly = false, rollbackFor = {BarCodeAlreadyExistException.class, NameProductAlreadyExistException.class})
    public Product addProduct(Product product) throws BarCodeAlreadyExistException, NameProductAlreadyExistException {
        if(product.getBarCode() == null || product.getName() == null )
            throw new ValidationFailed();
        if ( productRepository.existsByBarCode(product.getBarCode())) {
            throw new BarCodeAlreadyExistException();
        }
        if( productRepository.existsProductByNameIgnoreCase(product.getName())){
            throw new NameProductAlreadyExistException();
        }
        return productRepository.save(product);
    }

    @Transactional(readOnly = false)
    public void updateQtProduct(int idProduct, int qnt) {
        Optional<Product> result = productRepository.findById(idProduct);
        if(result.isEmpty() || qnt <= 0)
            throw new ValidationFailed();
        Product p = result.get();
        int oldQnt = p.getQuantity();
        p.setQuantity(oldQnt+qnt);
    }

    @Transactional(readOnly = true)
    public List<Product> showAllProducts() {
        return productRepository.findAll();
    }

    //Restituisce tutti i prodotti paginati
    @Transactional(readOnly = true)
    public List<Product> showAllProducts(int pageNumber, int pageSize, String sortBy) {
        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy)); //costruzione dell'oggetto pageable
        Page<Product> pagedResult = productRepository.findAll(paging);
        if ( pagedResult.hasContent() ) {
            return pagedResult.getContent();
        }
        else {
            return new ArrayList<>();
        }
    }

    @Transactional(readOnly = true)
    public List<Product> showProductsByName(String name) {
        return productRepository.findByNameIgnoreCase(name);
    }

    @Transactional(readOnly = true)
    public List<Product> showProductsByBarCode(String barCode) {
        return productRepository.findByBarCode(barCode);
    }


}
