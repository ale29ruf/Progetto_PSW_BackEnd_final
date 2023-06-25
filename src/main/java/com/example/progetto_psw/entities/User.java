package com.example.progetto_psw.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;


@Getter
@Setter
@EqualsAndHashCode
@ToString
@Entity
@Table(name = "user", schema = "public", uniqueConstraints = {
        @UniqueConstraint(columnNames = "email")})
public class User {

    /**
     * Gli utenti sono identificati dalla mail.
     * @JsonIgnore viene utilizzata per indicare a una libreria di serializzazione (ad esempio Jackson) d'ignorare una specifica proprietà di un oggetto durante la conversione da oggetto a JSON o viceversa.
     * @Basic significa che un attributo deve essere persistente
     */

    // TODO Aggiungere le annotazioni che verranno usate poi per la validazione(attraverso @Valid) degli oggetti che vengono passati come argomento a un metodo o come parametro di una richiesta

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @Basic
    @Column(name = "code", length = 70)
    private String code;

    @Basic
    @Column(name = "first_name", length = 50)
    private String firstName;

    @Basic
    @Column(name = "last_name", length = 50)
    private String lastName;

    @Basic
    @Column(name = "telephone_number", nullable = false, length = 20)
    private String telephoneNumber;

    @Basic
    @NonNull
    @Column(name = "email", nullable = false, length = 90)
    private String email;

    @Basic
    @Column(name = "address", nullable = false, length = 150)
    private String address;

    @OneToMany(mappedBy = "buyer", cascade = CascadeType.MERGE)
    @JsonIgnore //se non ci fosse si avrebbe un ciclo se venisse chiesto un User
    private List<Purchase> purchases;

    @Transient
    private String password;

    public User(int id){
        this.id = id;
    }

    public User(String email){
        this.email = email;
    }

    public User() {

    }
}
