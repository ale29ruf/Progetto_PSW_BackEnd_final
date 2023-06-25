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
import java.util.Optional;


@Service
public class PurchasingService {
    @Autowired
    private PurchaseRepository purchaseRepository;
    @Autowired
    private ProductInPurchaseRepository productInPurchaseRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EntityManager entityManager;


    @Transactional(readOnly = false, isolation = Isolation.REPEATABLE_READ, propagation = Propagation.NESTED,
            rollbackFor = QuantityProductUnavailableException.class)
    public Purchase addPurchase(Purchase purchase) throws QuantityProductUnavailableException {
        Purchase result = purchaseRepository.save(purchase);
        for ( ProductInPurchase pip : result.getProductsInPurchase() ) {
            pip.setPurchase(result);
            ProductInPurchase justAdded = productInPurchaseRepository.save(pip);
            entityManager.refresh(justAdded);
            Product product = justAdded.getProduct();
            int newQuantity = product.getQuantity() - pip.getQuantity();
            if ( newQuantity < 0 ) {
                throw new QuantityProductUnavailableException("Id: "+product.getId());
            }
            product.setQuantity(newQuantity);
            entityManager.refresh(pip);
        }
        entityManager.refresh(result);
        return result;
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public List<Purchase> getPurchasesByUser(User user) throws UserNotFoundException {
        if ( !userRepository.existsById(user.getId()) ) {
            throw new UserNotFoundException();
        }
        return purchaseRepository.findByBuyer(user);
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public List<Purchase> getPurchasesByUserInPeriod(User user, Date startDate, Date endDate) throws UserNotFoundException, DateWrongRangeException {
        if ( !userRepository.existsById(user.getId()) ) {
            throw new UserNotFoundException();
        }
        if ( startDate.compareTo(endDate) >= 0 ) {
            throw new DateWrongRangeException();
        }
        return purchaseRepository.findByBuyerInPeriod(startDate, endDate, user);
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public List<Purchase> getAllPurchases() {
        return purchaseRepository.findAll();
    }


}
