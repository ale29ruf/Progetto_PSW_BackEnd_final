package com.example.progetto_psw.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @Column(name = "quantity", nullable = true)
    private int quantity; //introduciamo questa entity proprio per poter specificare la quantità per ogni prodotto all'interno dello specifico ordine

    // TODO aggiungere eventualmente il prezzo (che potrebbe differire da quello del prodotto in Product con il passare del tempo)

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "product")
    private Product product;

    public ProductInPurchase(Product product, int quantity){
        this.product = product;
        this.quantity = quantity;
    }


    public ProductInPurchase() {

    }
}
