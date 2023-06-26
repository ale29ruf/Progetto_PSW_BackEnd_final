package com.example.progetto_psw.repositories;


import com.example.progetto_psw.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    List<User> findByFirstName(String firstName);
    List<User> findByLastName(String lastName);
    List<User> findByFirstNameAndLastName(String firstName, String lastName);
    List<User> findByEmail(String email);
    List<User> findByCodFiscale(String code);
    boolean existsByEmail(String email);
    boolean existsByUsername(String email);

}
