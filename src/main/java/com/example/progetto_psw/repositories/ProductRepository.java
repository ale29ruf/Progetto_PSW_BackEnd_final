package com.example.progetto_psw.repositories;


import com.example.progetto_psw.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    List<Product> findByNameContaining(String name);
    List<Product> findByBarCode(String name);
    boolean existsByBarCode(String barCode);
    List<Product> findByNameIgnoreCase(String name);
    boolean existsProductByNameIgnoreCase(String name);

    @Query("SELECT p " +
            "FROM Product p " +
            "WHERE (p.name LIKE :name OR :name IS NULL) AND " +
            "      (p.quantity > :quantity OR :quantity IS NULL) AND " +
            "      (p.price > :price OR :price IS NULL) ")
    List<Product> advanceSearch (String name, Integer quantity, Integer price);
    //In questo modo possiamo fare un unico metodo con molti parametri e usarlo per molte combinazioni di query

}
