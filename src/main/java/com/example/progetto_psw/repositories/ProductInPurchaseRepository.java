package com.example.progetto_psw.repositories;


import com.example.progetto_psw.entities.Cart;
import com.example.progetto_psw.entities.Product;
import com.example.progetto_psw.entities.ProductInPurchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ProductInPurchaseRepository extends JpaRepository<ProductInPurchase, Integer> {

    @Query("select pip.product from ProductInPurchase pip where pip.cart = :cart and pip.product = :prod")
    List<Product> existPipInCartByProduct(Cart cart, Product prod);

}
