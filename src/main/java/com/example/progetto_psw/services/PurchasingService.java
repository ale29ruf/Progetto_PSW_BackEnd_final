package com.example.progetto_psw.services;



import com.example.progetto_psw.entities.*;
import com.example.progetto_psw.repositories.ProductInPurchaseRepository;
import com.example.progetto_psw.repositories.ProductRepository;
import com.example.progetto_psw.repositories.PurchaseRepository;
import com.example.progetto_psw.repositories.UserRepository;
import jakarta.persistence.OptimisticLockException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import support.PipDetails;
import support.authentication.Utils;
import support.exceptions.*;

import java.util.*;

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


    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW,
            rollbackFor = {QuantityProductUnavailableException.class,UserNotFoundException.class,PriceChangedException.class,InconsistencyCartException.class, OptimisticLockException.class})
    public Purchase addPurchase(@Valid List<PipDetails> pipDetailsList) throws QuantityProductUnavailableException, UserNotFoundException, PriceChangedException, InconsistencyCartException {
        if(!userRepository.existsByUsername(Utils.getUsername())) throw new UserNotFoundException();
        User u = userRepository.findByUsername(Utils.getUsername()).get(0);
        Cart cart = u.getCart();

        if(cart.getProductsInPurchase().size() != pipDetailsList.size()) throw new InconsistencyCartException("PRODUCT_ARE_INCONSISTENCY_IN_CART_TRY_LATER"); // Un utente da un altro dispositivo ha provato a modificare i prodotti nel carrello mentre un altro sta procedendo con l'acquisto
        Purchase purchase = new Purchase();
        purchase.setBuyer(u);
        purchase.setPurchaseTime(new Date(System.currentTimeMillis()));
        purchase.setProductsInPurchase(new LinkedList<>());
        purchaseRepository.save(purchase);

        for(PipDetails pipDetails : pipDetailsList){
            Optional<ProductInPurchase> pipFinded = productInPurchaseRepository.findById(pipDetails.getId());
            if(pipFinded.isEmpty() || pipFinded.get().getProduct().getId() != pipDetails.getProduct()) throw new InconsistencyCartException("PRODUCT_"+pipDetails.getId()+"_IN_CART_NOT_EXIST_TRY_LATER");
            Optional<Product> p = productRepository.findById(pipDetails.getProduct());
            if(p.isEmpty()) throw new IllegalArgumentException("PRODUCT_"+pipFinded.get().getProduct()+"_NOT_EXIST");
            Product product = p.get();
            if(product.getQuantity() < pipDetails.getQuantity()) throw new QuantityProductUnavailableException(product.getName());
            if(product.getPrice() != pipDetails.getPrice()) throw new PriceChangedException(product.getName());
            if(pipDetails.getQuantity() > 0){ // Se la quantità del prodotto nel carrello è nulla allora viene ignorato nell'acquisto. Tuttavia lo faccio rimanere nel carrello così se torna disponibile può essere acquistato
                ProductInPurchase pip = new ProductInPurchase();
                pip.setQuantity(pipDetails.getQuantity());
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

    @Transactional(readOnly = true, propagation = Propagation.NESTED, isolation = Isolation.READ_COMMITTED, rollbackFor = {UserNotFoundException.class})
    public List<Purchase> getPurchasesByUser(int pageNumber, int pageSize,String sortBy) throws UserNotFoundException {
        if ( !userRepository.existsByUsername(Utils.getUsername()) ) {
            throw new UserNotFoundException();
        }
        User u = userRepository.findByUsername(Utils.getUsername()).get(0);
        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Purchase> pagedResult = purchaseRepository.findAllByBuyer(u,paging);

        if ( pagedResult.hasContent() ) {
            return pagedResult.getContent();
        }
        else {
            return new ArrayList<>();
        }

    }

    @Transactional(readOnly = true, propagation = Propagation.NESTED, isolation = Isolation.READ_COMMITTED, rollbackFor = {UserNotFoundException.class, DateWrongRangeException.class})
    public List<Purchase> getPurchasesByUserInPeriod(Date startDate, Date endDate) throws UserNotFoundException, DateWrongRangeException {
        if ( !userRepository.existsByUsername(Utils.getUsername()) ) {
            throw new UserNotFoundException();
        }
        if ( startDate.compareTo(endDate) >= 0 ) {
            throw new DateWrongRangeException();
        }
        User u = userRepository.findByUsername(Utils.getUsername()).get(0);
        return purchaseRepository.findByBuyerInPeriod(startDate, endDate, u);
    }

    @Transactional(readOnly = true, propagation = Propagation.NESTED, isolation = Isolation.READ_COMMITTED)
    public List<Purchase> getAllPurchases() {
        return purchaseRepository.findAll();
    }


}
