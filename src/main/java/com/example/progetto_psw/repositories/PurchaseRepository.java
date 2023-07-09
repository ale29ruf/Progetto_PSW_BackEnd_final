package com.example.progetto_psw.repositories;


import com.example.progetto_psw.entities.Purchase;
import com.example.progetto_psw.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;


@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Integer> {

    //Per fare la ricerca passiamo un oggetto utente e non l'id come facevamo in sql.
    //Conviene passare, dal client, un utente che abbia come unica variabile specificata solo l'identificativo
    //e non tutti gli altri campi valorizzati che non servono.
    List<Purchase> findByBuyer(User user); //Bisogna paginare gli acquisti.
    List<Purchase> findByPurchaseTime(Date date);

    @Query("select p from Purchase p where p.purchaseTime >= ?1 and p.purchaseTime < ?2 and p.buyer = ?3")
    List<Purchase> findByBuyerInPeriod(Date startDate, Date endDate, User user);

    Page<Purchase> findAllByBuyer(User u, Pageable paging);
}
