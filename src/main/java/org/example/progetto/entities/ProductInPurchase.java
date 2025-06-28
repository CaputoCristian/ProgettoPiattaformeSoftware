package org.example.progetto.entities;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "product_in_purchase", schema = "orders")
@Getter
@Setter
@EqualsAndHashCode
@ToString


public class ProductInPurchase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @Basic
    @Column(name = "product", nullable = true, length = 90)
    private String product;

    @Basic
    @Column(name = "quantity", nullable = true, length = 90)
    private String quantity;

    @ManyToOne
    @JoinColumn(name = "related_purchase") //???
    private Purchase relatedPurchase;
}