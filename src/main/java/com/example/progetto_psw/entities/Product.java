package com.example.progetto_psw.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
@Table(name = "product", schema = "public",uniqueConstraints = {
        @UniqueConstraint(columnNames = "bar_code"),@UniqueConstraint(columnNames = "name")})
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @Basic
    @Nonnull
    @Column(name = "name", nullable = false, length = 20)
    private String name;

    @Basic
    @Column(name = "bar_code", nullable = false, length = 20)
    @Size(min = 5, max = 5)
    @Nonnull
    private String barCode;

    @Basic
    @Column(name = "description", nullable = true, length = 100)
    private String description;

    @Basic
    @Column(name = "price", nullable = false)
    private float price;

    @Basic
    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Basic
    @Column(name = "category", nullable = true)
    private Categories category;

    @Version
    @Column(name = "version", nullable = false)
    @JsonIgnore
    private long version; //supportare la gestione ottimistica della concorrenza

    @OneToMany(targetEntity = ProductInPurchase.class, mappedBy = "product", cascade = CascadeType.MERGE)
    @JsonIgnore
    @ToString.Exclude
    private List<ProductInPurchase> productsInPurchase;


    public Product(int id){
        this.id = id;
    }

    public Product(String name){
        this.name = name;
    }

    public Product() {

    }
}
