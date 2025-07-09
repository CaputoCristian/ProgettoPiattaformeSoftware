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

    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id")
    private Product product;

    @Basic
    @Column(name = "quantity", nullable = true, length = 90)
    private int quantity;

    @ManyToOne(optional = false)
    @JoinColumn(name = "related_purchase")
    private Purchase relatedPurchase;
}