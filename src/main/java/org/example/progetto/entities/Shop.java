package org.example.progetto.entities;

import jakarta.persistence.*;
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
@Table(name = "shop", schema = "orders")

public class Shop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "seller_id", unique = true, nullable = false)
    private User seller;

    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL)
    private List<Product> products;

}
