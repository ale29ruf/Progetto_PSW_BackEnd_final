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
    private ProductRepository productRepository;
    @Autowired
    private EntityManager entityManager;


    //L'oggetto purchase (result) vine ricevuto dal client al server
    @Transactional()
    public Purchase addPurchaseAle(Purchase result) throws QuantityProductUnavailableException {
        for ( ProductInPurchase pip : result.getProductsInPurchase() ) {
            int idProd = pip.getProduct().getId();
            Optional<Product> optionalProd = productRepository.findById(idProd);
            if(!optionalProd.isPresent()) throw new IllegalArgumentException();
            Product prod = optionalProd.get();
            int newQuantity = prod.getQuantity() - pip.getQuantity();
            if(newQuantity < 0) throw new QuantityProductUnavailableException("Id: "+idProd);
        }

        return processaAcquisto(result);//restituiamo ciò che ci è stato passato
    }

    public Purchase processaAcquisto(Purchase result){
        Purchase purchase = purchaseRepository.save(result); //salva l'acquisto (e non le entity in relazione) nella tabella e lo restituisce attached
        for ( ProductInPurchase pip : result.getProductsInPurchase() ) { //iteriamo su tutti i prodotti della lista di quelli da acquistare
            pip.setPurchase(purchase);//settiamo l'acquisto al prodotto    //e li inseriamo nella tabella dei prodotti degli acquisti
            ProductInPurchase justAdded = productInPurchaseRepository.save(pip); //salva il prodotto nella tabella "product_in_purchase" e lo restituisce attached
            entityManager.refresh(justAdded); //in questo modo i restanti campi vuoti vengono riempiti con quelli del database (in questo modo possiamo accedere al campo "prodotto")
            //Il client passa all'interno dell'oggetto "result" solo gli id dei ProductInPurchase e non gli oggetti completi, quindi quando
            //viene invocato il metodo getProduct su un ProductInPurchase, se non facciamo prima la refresh allora ci viene restituito solo
            //l'id e non tutte le informazioni di quel ProductInPurchase (per recuperare le informazioni Spring effettua dietro le quinte le varie
            //join e riempie le relazioni).
            Product product = justAdded.getProduct(); //restituisce il prodotto (campo di "ProductInPurchase")
            int newQuantity = product.getQuantity() - pip.getQuantity();
            product.setQuantity(newQuantity);
            entityManager.refresh(pip);
        }
        entityManager.refresh(purchase);
        return purchase;
    }

    @Transactional(readOnly = true)
    public List<Purchase> getPurchasesByUser(User user) throws UserNotFoundException {
        if ( !userRepository.existsById(user.getId()) ) {
            throw new UserNotFoundException();
        }
        return purchaseRepository.findByBuyer(user);
    }

    @Transactional(readOnly = true)
    public List<Purchase> getPurchasesByUserInPeriod(User user, Date startDate, Date endDate) throws UserNotFoundException, DateWrongRangeException {
        if ( !userRepository.existsById(user.getId()) ) {
            throw new UserNotFoundException();
        }
        if ( startDate.compareTo(endDate) >= 0 ) {
            throw new DateWrongRangeException();
        }
        return purchaseRepository.findByBuyerInPeriod(startDate, endDate, user);
    }

    @Transactional(readOnly = true)
    public List<Purchase> getAllPurchases() {
        return purchaseRepository.findAll();
    }


}
