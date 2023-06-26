package com.example.progetto_psw.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;


@Getter
@Setter
@EqualsAndHashCode
@ToString
@Entity
@Table(name = "user", schema = "public", uniqueConstraints = {
        @UniqueConstraint(columnNames = "email"),@UniqueConstraint(columnNames = "username")})
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
    @Column(name = "username", length = 10, nullable = false)
    @Size(min = 7, max = 7)
    @Nonnull
    private String username;

    @Basic
    @Column(name = "code", length = 20)
    @Size(min = 20, max = 20)
    @Nonnull
    private String codFiscale;

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
    @Nonnull
    @Column(name = "email", nullable = false, length = 90)
    @Email
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
