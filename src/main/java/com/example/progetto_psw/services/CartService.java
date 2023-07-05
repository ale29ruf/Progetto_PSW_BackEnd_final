package com.example.progetto_psw.services;

import com.example.progetto_psw.entities.Cart;
import com.example.progetto_psw.entities.Product;
import com.example.progetto_psw.entities.ProductInPurchase;
import com.example.progetto_psw.entities.User;
import com.example.progetto_psw.repositories.CartRepository;
import com.example.progetto_psw.repositories.ProductInPurchaseRepository;
import com.example.progetto_psw.repositories.ProductRepository;
import com.example.progetto_psw.repositories.UserRepository;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import support.authentication.Utils;
import support.exceptions.QuantityProductUnavailableException;
import support.exceptions.UserNotFoundException;

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

    @Transactional(readOnly = false, propagation = Propagation.NESTED,
            rollbackFor = {UserNotFoundException.class})
    public Cart getCart() throws UserNotFoundException{
        List<User> result = userRepository.findByUsername(Utils.getUsername());
        if(result.isEmpty()) throw new UserNotFoundException();
        User u = result.get(0);
        return cartRepository.findByUser(u);
    }

    @Transactional(readOnly = false, propagation = Propagation.NESTED,
            rollbackFor = {UserNotFoundException.class})
    public void addProduct(int idProd) throws UserNotFoundException {
        List<User> result = userRepository.findByUsername(Utils.getUsername());
        if(result.isEmpty()) throw new UserNotFoundException();
        Optional<Product> p = productRepository.findById(idProd);
        if(p.isEmpty()) throw new IllegalArgumentException("Id "+idProd+" non esistente");
        Product product = p.get();
        ProductInPurchase pip = new ProductInPurchase();
        pip.setProduct(product);
        //per il momento non mi interessa settare il pip anche al product
        pip.setQuantity(1);
        pip.setPrice(product.getPrice());
        User u = result.get(0);
        Cart cart = cartRepository.findByUser(u);
        pip.setCart(cart);
        cart.getProductsInPurchase().add(pip);
        productInPurchaseRepository.save(pip);
   }

    @Transactional(readOnly = false, propagation = Propagation.NESTED, rollbackFor = {UserNotFoundException.class})
    public void removeProduct(int idProdInP) throws UserNotFoundException {
        List<User> result = userRepository.findByUsername(Utils.getUsername());
        if(result.isEmpty()) throw new UserNotFoundException();
        Optional<ProductInPurchase> pip = productInPurchaseRepository.findById(idProdInP);
        if(pip.isEmpty()) throw new IllegalArgumentException("PRODUCT_IN_PURCHASE_NOT_EXIST_IN_CART");
        User u = result.get(0);
        Cart cart = cartRepository.findByUser(u);
        cart.getProductsInPurchase().remove(pip.get());
        // cartRepository.save(cart); inutile dato che è gia' in stato managed
        productInPurchaseRepository.delete(pip.get());
    }

    @Transactional(readOnly = false, propagation = Propagation.NESTED, rollbackFor = {UserNotFoundException.class})
    public void plusQntProduct(int idProdInP) throws UserNotFoundException {
        List<User> result = userRepository.findByUsername(Utils.getUsername());
        if(result.isEmpty()) throw new UserNotFoundException();
        Optional<ProductInPurchase> pip = productInPurchaseRepository.findById(idProdInP);
        if(pip.isEmpty()) throw new IllegalArgumentException("PRODUCT_IN_PURCHASE_NOT_EXIST_IN_CART");
        int oldQnt = pip.get().getQuantity();
        pip.get().setQuantity(oldQnt+1);
    }

    @Transactional(readOnly = false, propagation = Propagation.NESTED, rollbackFor = {UserNotFoundException.class})
    public void minusQntProduct(int idProdInP) throws UserNotFoundException {
        List<User> result = userRepository.findByUsername(Utils.getUsername());
        if(result.isEmpty()) throw new UserNotFoundException();
        Optional<ProductInPurchase> pip = productInPurchaseRepository.findById(idProdInP);
        if(pip.isEmpty()) throw new IllegalArgumentException("PRODUCT_IN_PURCHASE_NOT_EXIST_IN_CART");
        int oldQnt = pip.get().getQuantity();
        pip.get().setQuantity(oldQnt-1);
    }

}
