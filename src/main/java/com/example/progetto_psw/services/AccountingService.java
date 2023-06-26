package com.example.progetto_psw.services;


import com.example.progetto_psw.entities.User;
import com.example.progetto_psw.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import support.exceptions.MailUserAlreadyExistsException;
import support.exceptions.UsernameUserAlreadyExistsException;

import java.util.List;


@Service
public class AccountingService {
    @Autowired
    private UserRepository userRepository;


    //registra un nuovo utente
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public User registerUser(User user) throws MailUserAlreadyExistsException {
        if ( userRepository.existsByEmail(user.getEmail()) ) {
            throw new MailUserAlreadyExistsException();
        }

        return userRepository.save(user);
    }
    //In realtà la registrazione la facciamo con un server terzo e mantenuta con un token JWT.


    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = {UsernameUserAlreadyExistsException.class, MailUserAlreadyExistsException.class})
    public User verifyUser(User user) throws MailUserAlreadyExistsException, UsernameUserAlreadyExistsException {
        if ( userRepository.existsByEmail(user.getEmail()) ) {
            throw new MailUserAlreadyExistsException();
        }
        if(userRepository.existsByUsername(user.getUsername())){
            throw new UsernameUserAlreadyExistsException();
        }
        return userRepository.save(user);
    }


    //restituisce la lista di tutti gli utenti
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }



}
