package org.example.progetto.entities;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@EqualsAndHashCode
@ToString
@Table(name = "sale_alert", schema = "orders")
public class SaleAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @ManyToOne
    @JoinColumn(name = "related_purchase", nullable = false)
    private Purchase purchase;

    @Column(name = "shipping_address", nullable = false)
    private String shippingAddress;

    @Column(name = "total_amount", nullable = false)
    private float totalAmount;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "viewed", nullable = false)
    private boolean viewed;

    @OneToMany(mappedBy = "saleAlert", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductInSale> products = new ArrayList<>();

}
