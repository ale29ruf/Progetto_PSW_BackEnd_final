package com.example.progetto_psw.services;



import com.example.progetto_psw.entities.Product;
import com.example.progetto_psw.entities.ProductInPurchase;
import com.example.progetto_psw.entities.Purchase;
import com.example.progetto_psw.entities.User;
import com.example.progetto_psw.repositories.ProductInPurchaseRepository;
import com.example.progetto_psw.repositories.ProductRepository;
import com.example.progetto_psw.repositories.PurchaseRepository;
import com.example.progetto_psw.repositories.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import support.PipDetails;
import support.authentication.Utils;
import support.exceptions.DateWrongRangeException;
import support.exceptions.PriceChangedException;
import support.exceptions.QuantityProductUnavailableException;
import support.exceptions.UserNotFoundException;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class PurchasingService {
    @Autowired
    private PurchaseRepository purchaseRepository;
    @Autowired
    private ProductInPurchaseRepository productInPurchaseRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private ProductRepository productRepository;


    @Transactional(readOnly = false, propagation = Propagation.NESTED,
            rollbackFor = {QuantityProductUnavailableException.class,UserNotFoundException.class,PriceChangedException.class})
    public Purchase addPurchase(@Valid List<PipDetails> pipDetailsList) throws QuantityProductUnavailableException, UserNotFoundException, PriceChangedException {
        if(!userRepository.existsByUsername(Utils.getUsername())) throw new UserNotFoundException();
        User u = userRepository.findByUsername(Utils.getUsername()).get(0);
        Purchase purchase = new Purchase();
        purchase.setBuyer(u);
        purchase.setPurchaseTime(new Date(System.currentTimeMillis()));
        purchase.setProductsInPurchase(new LinkedList<>());
        purchaseRepository.save(purchase);

        for(PipDetails pipDetails : pipDetailsList){
            Optional<Product> p = productRepository.findById(pipDetails.getPid());
            if(p.isEmpty()) throw new IllegalArgumentException("Prodotto con id "+pipDetails.getPid()+" non esistente");
            Product product = p.get();
            if(product.getQuantity() < pipDetails.getQta()) throw new QuantityProductUnavailableException(product.getId());
            if(product.getPrice() != pipDetails.getPrice()) throw new PriceChangedException(product.getId());
            ProductInPurchase pip = new ProductInPurchase();
            pip.setQuantity(pipDetails.getQta());
            pip.setPrice(pipDetails.getPrice());
            pip.setPurchase(purchase);
            pip.setProduct(product);
            pip.setCart(u.getCart());
            productInPurchaseRepository.save(pip);
            purchase.getProductsInPurchase().add(pip);
            product.setQuantity(product.getQuantity() - pip.getQuantity());
        }

        return purchase;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<Purchase> getPurchasesByUser() throws UserNotFoundException {
        if ( !userRepository.existsByUsername(Utils.getUsername()) ) {
            throw new UserNotFoundException();
        }
        User u = userRepository.findByUsername(Utils.getUsername()).get(0);
        return purchaseRepository.findByBuyer(u);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<Purchase> getPurchasesByUserInPeriod(User user, Date startDate, Date endDate) throws UserNotFoundException, DateWrongRangeException {
        if ( !userRepository.existsByUsername(user.getUsername()) ) {
            throw new UserNotFoundException();
        }
        if ( startDate.compareTo(endDate) >= 0 ) {
            throw new DateWrongRangeException();
        }
        User u = userRepository.findByUsername(user.getUsername()).get(0);
        return purchaseRepository.findByBuyerInPeriod(startDate, endDate, u);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<Purchase> getAllPurchases() {
        return purchaseRepository.findAll();
    }


}
