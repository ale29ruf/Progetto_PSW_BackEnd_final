package com.example.progetto_psw.repositories;

import com.example.progetto_psw.entities.Cart;
import com.example.progetto_psw.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart, Integer> {

    Cart findByUser(User user);
}
