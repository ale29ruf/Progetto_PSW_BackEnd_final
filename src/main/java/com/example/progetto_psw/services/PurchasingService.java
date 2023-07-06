package com.example.progetto_psw.services;



import com.example.progetto_psw.entities.*;
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
import support.exceptions.*;

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
    private ProductRepository productRepository;


    @Transactional(readOnly = false, propagation = Propagation.NESTED,
            rollbackFor = {QuantityProductUnavailableException.class,UserNotFoundException.class,PriceChangedException.class,InconsistencyCartException.class})
    public Purchase addPurchase(@Valid List<PipDetails> pipDetailsList) throws QuantityProductUnavailableException, UserNotFoundException, PriceChangedException, InconsistencyCartException {
        if(!userRepository.existsByUsername(Utils.getUsername())) throw new UserNotFoundException();
        User u = userRepository.findByUsername(Utils.getUsername()).get(0);
        Cart cart = u.getCart();

        if(cart.getProductsInPurchase().size() != pipDetailsList.size()) throw new InconsistencyCartException("INCONSISTENCY_CART_TRY_LATER"); // Un utente da un altro dispositivo ha provato a modificare i prodotti nel carrello mentre un altro sta procedendo con l'acquisto
        Purchase purchase = new Purchase();
        purchase.setBuyer(u);
        purchase.setPurchaseTime(new Date(System.currentTimeMillis()));
        purchase.setProductsInPurchase(new LinkedList<>());
        purchaseRepository.save(purchase);

        for(PipDetails pipDetails : pipDetailsList){
            Optional<ProductInPurchase> pipFinded = productInPurchaseRepository.findById(pipDetails.getId());
            if(pipFinded.isEmpty() || pipFinded.get().getProduct().getId() != pipDetails.getPid()) throw new InconsistencyCartException("PRODUCT_IN_CART_"+pipDetails.getId()+"_NOT_EXIST_TRY_LATER");
            Optional<Product> p = productRepository.findById(pipDetails.getPid());
            if(p.isEmpty()) throw new IllegalArgumentException("PRODUCT_"+pipDetails.getPid()+"_NOT_EXIST");
            Product product = p.get();
            if(product.getQuantity() < pipDetails.getQta()) throw new QuantityProductUnavailableException(product.getId());
            if(product.getPrice() != pipDetails.getPrice()) throw new PriceChangedException(product.getId());
            if(pipDetails.getQta() > 0){ // Se la quantità del prodotto nel carrello è nulla allora viene ignorato nell'acquisto. Tuttavia lo faccio rimanere nel carrello così se torna disponibile può essere acquistato
                ProductInPurchase pip = new ProductInPurchase();
                pip.setQuantity(pipDetails.getQta());
                pip.setPrice(pipDetails.getPrice());
                pip.setPurchase(purchase);
                pip.setProduct(product);
                //pip.setCart(u.getCart()); dato che il responsabile della relazione è pip, se gli settiamo il carrello, al termine pip sara' anche all'interno del carrello
                productInPurchaseRepository.save(pip);
                purchase.getProductsInPurchase().add(pip);
                product.setQuantity(product.getQuantity() - pip.getQuantity());

                // Procedo con la rimozione del vecchio pip che era nel carrello
                cart.getProductsInPurchase().remove(pipFinded.get());
                productInPurchaseRepository.delete(pipFinded.get());
            }

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
