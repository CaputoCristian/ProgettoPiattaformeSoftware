package org.example.progetto.DTO;

import jakarta.validation.constraints.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class ProductUpdateRequest {

    @NotBlank(message = "Il nome non può essere vuoto")
    @Size(max = 30, message = "Il nome non può superare i 30 caratteri")
    private String name;

    @Size(max = 100, message = "La descrizione non può superare i 100 caratteri")
    private String description;

    @NotNull(message = "Il prezzo è obbligatorio")
    @DecimalMin(value = "0.0", inclusive = true, message = "Il prezzo non può essere negativo")
    private Double price;

    @NotNull(message = "La quantità è obbligatoria")
    @Min(value = 0, message = "La quantità non può essere negativa")
    private Integer quantity;

    @NotBlank(message = "La marca non può essere vuota")
    @Size(max = 15, message = "La marca non può superare i 15 caratteri")
    private String brand;

}
