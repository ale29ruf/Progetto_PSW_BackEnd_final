package com.example.progetto_psw.entities;


import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@EqualsAndHashCode
@ToString
@Entity
@Table(name = "product_in_purchase", schema = "public")
public class ProductInPurchase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @ManyToOne
    @JoinColumn(name = "related_purchase")
    @JsonIgnore
    @ToString.Exclude
    private Purchase purchase;

    @Basic
    @Column(name = "quantity", nullable = false)
    @Nonnull
    private int quantity; //introduciamo questa entity proprio per poter specificare la quantità per ogni prodotto all'interno dello specifico ordine

    @Basic
    @Column(name = "price", nullable = false)
    @Nonnull
    private float price; //introduciamo questa entity proprio per poter specificare la quantità per ogni prodotto all'interno dello specifico ordine

    /**
     * Sarebbe sbagliato propagare Persist o Remove sui prodotti
     */
    @ManyToOne()
    @JoinColumn(name = "product")
    @Nonnull
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private Product product;

    @ManyToOne()
    @JoinColumn(name = "related_cart")
    @JsonIgnore
    private Cart cart;

    @Version
    @Column(name = "version", nullable = false)
    @JsonIgnore
    private long version;

    public ProductInPurchase(Product product, int quantity){
        this.product = product;
        this.quantity = quantity;
    }


    public ProductInPurchase() {

    }
}
