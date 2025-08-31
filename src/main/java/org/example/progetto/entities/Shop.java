package org.example.progetto.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(exclude = "seller") // evita loop con user.shop
@ToString(exclude = "products")
@Entity
@Table(name = "shop", schema = "orders")

public class Shop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "seller_id", unique = true, nullable = false)
    @JsonManagedReference //evitare errori nell'aggiunta di un prodotto - loop infinito
    private User seller;

    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL)
    @JsonManagedReference //evitare errori nell'aggiunta di un prodotto - loop infinito
    private List<Product> products;

}
