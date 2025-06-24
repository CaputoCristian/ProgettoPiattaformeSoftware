package org.example.progetto.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;


@Getter
@Setter
@EqualsAndHashCode
@ToString
@Entity
@Table(name = "cart", schema = "orders")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int idCarrello;

    @JoinColumn(name = "user_id", nullable = false)
    private int idCliente;

    @Column(name ="total_price", precision = 10, scale = 2)
    private BigDecimal prezzoTotale = BigDecimal.ZERO;

    @OneToMany(mappedBy = "cart", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<ProductInCart> carrelloProdottos = new LinkedHashSet<>();




}