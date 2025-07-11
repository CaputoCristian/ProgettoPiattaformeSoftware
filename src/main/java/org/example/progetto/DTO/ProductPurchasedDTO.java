package org.example.progetto.DTO;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.example.progetto.entities.ProductInPurchase;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class ProductPurchasedDTO {

    private String name;

    private int quantity;

    public ProductPurchasedDTO(String name, int quantity) {
        this.name = name;
        this.quantity = quantity;
    }
}