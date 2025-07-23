package org.example.progetto.DTO;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class SaleDTO {

    private int id;

    private LocalDateTime date;

    private BigDecimal totalPrice = BigDecimal.ZERO;

    private List<ProductInSaleDTO> products;


}
