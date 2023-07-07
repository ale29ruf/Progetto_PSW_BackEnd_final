package com.example.progetto_psw.services;

import com.example.progetto_psw.entities.Cart;
import com.example.progetto_psw.entities.Product;
import com.example.progetto_psw.entities.ProductInPurchase;
import com.example.progetto_psw.entities.User;
import com.example.progetto_psw.repositories.CartRepository;
import com.example.progetto_psw.repositories.ProductInPurchaseRepository;
import com.example.progetto_psw.repositories.ProductRepository;
import com.example.progetto_psw.repositories.UserRepository;
import jakarta.persistence.OptimisticLockException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import support.PipDetails;
import support.authentication.Utils;
import support.exceptions.UserNotFoundException;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class CartService {
    @Autowired
    private ProductInPurchaseRepository productInPurchaseRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CartRepository cartRepository;

    /**
     * Se un prodotto cambia prezzo o quantità allora lo modifico all'interno del carrello.
     * Tuttavia se il prodotto raggiunge quantità nulla allora lo lascio in modo che l'utente possa accorgersi che
     * quel prodotto non è piu' disponibile. Ovviamente se procede con l'acquisto riceverà un'eccezione.
     */
    @Transactional(readOnly = false, propagation = Propagation.NESTED,
            rollbackFor = {UserNotFoundException.class, OptimisticLockException.class}, isolation = Isolation.READ_COMMITTED)
    public Cart getCart() throws UserNotFoundException{
        List<User> result = userRepository.findByUsername(Utils.getUsername());
        if(result.isEmpty()) throw new UserNotFoundException();
        User u = result.get(0);
        Cart cart = cartRepository.findByUser(u);
        if(cart != null){ // L'utente potrebbe non aver mai aggiunto qualcosa nel carrello
            for(ProductInPurchase pip : cart.getProductsInPurchase()){
                Product p = pip.getProduct();
                pip.setPrice(p.getPrice());
                if(pip.getQuantity() > p.getQuantity())
                    pip.setQuantity(p.getQuantity());
            }
        }
        return cart;
    }

    /**
     * Vedere nota nel controller.
     */
    @Transactional(readOnly = false, propagation = Propagation.NESTED,
            rollbackFor = {UserNotFoundException.class, OptimisticLockException.class}, isolation = Isolation.READ_COMMITTED)
    public void addProduct(int idProd) throws UserNotFoundException {
        List<User> result = userRepository.findByUsername(Utils.getUsername());
        if(result.isEmpty()) throw new UserNotFoundException();
        Optional<Product> p = productRepository.findById(idProd);
        if(p.isEmpty()) throw new IllegalArgumentException();
        Product product = p.get();
        ProductInPurchase pip = new ProductInPurchase();
        pip.setProduct(product);
        //per il momento non mi interessa settare il pip anche al product
        pip.setQuantity(1);
        pip.setPrice(product.getPrice());
        User u = result.get(0);
        Cart cart = cartRepository.findByUser(u);
        if(cart == null) { // L'utente non ha un carrello
            cart = new Cart();
            cart.setProductsInPurchase(new LinkedList<>());
            cart.setUser(u);
            u.setCart(cart);

            pip.setCart(cart);
            cart.getProductsInPurchase().add(pip);
            productInPurchaseRepository.save(pip);
        } else {
            for(ProductInPurchase productInPurchase : cart.getProductsInPurchase()){
                if(productInPurchase.getProduct().getId() == product.getId())
                    return;
            }
            pip.setCart(cart);
            cart.getProductsInPurchase().add(pip);
            productInPurchaseRepository.save(pip);

        }

   }

    @Transactional(readOnly = false, propagation = Propagation.NESTED,
            rollbackFor = {UserNotFoundException.class, OptimisticLockException.class}, isolation = Isolation.READ_COMMITTED)
    public void addAllProduct(List<PipDetails> listaProd) throws UserNotFoundException {
        List<User> result = userRepository.findByUsername(Utils.getUsername());
        if(result.isEmpty()) throw new UserNotFoundException();
        User u = result.get(0);
        Cart cart = cartRepository.findByUser(u);
        if(cart == null) { // L'utente non ha un carrello
            cart = new Cart();
            cart.setProductsInPurchase(new LinkedList<>());
            cart.setUser(u);
            u.setCart(cart);
        }

        for(PipDetails pipDetails : listaProd){
            Optional<Product> p = productRepository.findById(pipDetails.getPid());
            if(p.isEmpty()) throw new IllegalArgumentException();
            Product product = p.get();

            boolean exist = false;
            for(ProductInPurchase productInPurchase : cart.getProductsInPurchase()){
                if(productInPurchase.getProduct().getId() == product.getId())
                    exist = true;
                if(exist) break;
            }
            if(!exist) {
                ProductInPurchase pip = new ProductInPurchase();
                pip.setProduct(product);
                pip.setQuantity(pipDetails.getQta());
                pip.setPrice(product.getPrice());
                pip.setCart(cart);
                cart.getProductsInPurchase().add(pip);
                productInPurchaseRepository.save(pip);
            }
        }

    }

    @Transactional(readOnly = false, propagation = Propagation.NESTED, rollbackFor = {UserNotFoundException.class}, isolation = Isolation.READ_COMMITTED)
    public void removeProduct(int idProdInP) throws UserNotFoundException {
        List<User> result = userRepository.findByUsername(Utils.getUsername());
        if(result.isEmpty()) throw new UserNotFoundException();
        Optional<ProductInPurchase> pip = productInPurchaseRepository.findById(idProdInP);
        if(pip.isEmpty()) throw new IllegalArgumentException("PRODUCT_IN_PURCHASE_NOT_EXIST_IN_CART");
        Cart cart = pip.get().getCart();
        cart.getProductsInPurchase().remove(pip.get()); // cartRepository.save(cart); inutile dato che è gia' in stato managed
        productInPurchaseRepository.delete(pip.get());
    }

    @Transactional(readOnly = false, propagation = Propagation.NESTED, rollbackFor = {UserNotFoundException.class}, isolation = Isolation.READ_COMMITTED)
    public void removeAllProduct() throws UserNotFoundException {
        List<User> result = userRepository.findByUsername(Utils.getUsername());
        if(result.isEmpty()) throw new UserNotFoundException();
        Cart cart = cartRepository.findByUser(result.get(0));
        productInPurchaseRepository.deleteAll(cart.getProductsInPurchase());
        cart.getProductsInPurchase().clear();
    }

    @Transactional(readOnly = false, propagation = Propagation.NESTED, rollbackFor = {UserNotFoundException.class, OptimisticLockException.class}, isolation = Isolation.READ_COMMITTED)
    public void plusQntProduct(int idProdInP) throws UserNotFoundException {
        List<User> result = userRepository.findByUsername(Utils.getUsername());
        if(result.isEmpty()) throw new UserNotFoundException();
        Optional<ProductInPurchase> pip = productInPurchaseRepository.findById(idProdInP);
        if(pip.isEmpty()) throw new IllegalArgumentException();
        int oldQnt = pip.get().getQuantity();
        pip.get().setQuantity(oldQnt+1);
    }

    @Transactional(readOnly = false, propagation = Propagation.NESTED, rollbackFor = {UserNotFoundException.class, OptimisticLockException.class}, isolation = Isolation.READ_COMMITTED)
    public void minusQntProduct(int idProdInP) throws UserNotFoundException {
        List<User> result = userRepository.findByUsername(Utils.getUsername());
        if(result.isEmpty()) throw new UserNotFoundException();
        Optional<ProductInPurchase> pip = productInPurchaseRepository.findById(idProdInP);
        if(pip.isEmpty()) throw new IllegalArgumentException("PRODUCT_IN_PURCHASE_NOT_EXIST_IN_CART");
        int oldQnt = pip.get().getQuantity();
        if(oldQnt-1 == 0) {
            Cart cart = pip.get().getCart();
            cart.getProductsInPurchase().remove(pip.get()); // cartRepository.save(cart); inutile dato che è gia' in stato managed
            productInPurchaseRepository.delete(pip.get());
        }
        pip.get().setQuantity(oldQnt-1);

    }

}
