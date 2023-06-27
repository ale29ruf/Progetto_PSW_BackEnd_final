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
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import support.exceptions.DateWrongRangeException;
import support.exceptions.QuantityProductUnavailableException;
import support.exceptions.UserNotFoundException;

import java.util.Date;
import java.util.List;

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


    @Transactional(readOnly = false, isolation = Isolation.REPEATABLE_READ, propagation = Propagation.NESTED,
            rollbackFor = {QuantityProductUnavailableException.class,UserNotFoundException.class})
    public Purchase addPurchase( @Valid Purchase purchase) throws QuantityProductUnavailableException, UserNotFoundException {
        if(!userRepository.existsByUsername(purchase.getBuyer().getUsername())) throw new UserNotFoundException();

        User u = userRepository.findByUsername(purchase.getBuyer().getUsername()).get(0);
        purchase.setBuyer(u);
        Purchase result = purchaseRepository.save(purchase);

        for ( ProductInPurchase pip : result.getProductsInPurchase() ) {
            if(!productRepository.existsById(pip.getProduct().getId())) throw new IllegalArgumentException("Id "+pip.getProduct().getId()+" non esistente");
            pip.setPurchase(result);
            ProductInPurchase justAdded = productInPurchaseRepository.save(pip);
            entityManager.refresh(justAdded); //necessario dato che non c'è propagazione di refresh in ProductInPurchase
            Product product = justAdded.getProduct();
            int newQuantity = product.getQuantity() - pip.getQuantity();
            if ( newQuantity < 0 )  throw new QuantityProductUnavailableException("Id: "+product.getId());
            product.setQuantity(newQuantity);
            entityManager.refresh(pip);
        }
        entityManager.refresh(result);
        return result;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<Purchase> getPurchasesByUser(User user) throws UserNotFoundException {
        if ( !userRepository.existsByUsername(user.getUsername()) ) {
            throw new UserNotFoundException();
        }
        User u = userRepository.findByUsername(user.getUsername()).get(0);
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
