package org.example.progetto.DTO;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.example.progetto.entities.ProductInPurchase;
import org.example.progetto.entities.User;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class PurchaseDTO {


    private int id;

    private LocalDateTime date;

    private BigDecimal totalPrice = BigDecimal.ZERO;

    private List<ProductPurchasedDTO> products;


}
