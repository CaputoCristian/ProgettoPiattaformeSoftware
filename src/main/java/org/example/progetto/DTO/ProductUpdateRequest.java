package org.example.progetto.DTO;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class ProductUpdateRequest {

    private int id;
    private String name;
    private String brand;
    private String type;
    private String description;
    private Float price;
    private Integer quantity;
}
