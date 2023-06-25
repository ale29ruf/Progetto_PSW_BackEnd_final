package com.example.progetto_psw.repositories;


import com.example.progetto_psw.entities.ProductInPurchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ProductInPurchaseRepository extends JpaRepository<ProductInPurchase, Integer> {

}
