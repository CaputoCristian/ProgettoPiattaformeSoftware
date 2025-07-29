package org.example.progetto.DTO;

import jakarta.validation.constraints.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class UserUpdateRequest {

    @NotBlank(message = "Il nome non può essere vuoto")
    @Size(max = 30, message = "Il nome non può superare i 15 caratteri")
    private String firstName;

    @NotBlank(message = "Il cognome non può essere vuoto")
    @Size(max = 30, message = "Il cognome non può superare i 15 caratteri")
    private String lastName;

    @NotNull(message = "Il numero è obbligatorio")
    @Pattern(regexp = "^\\s*(?:\\+?(\\d{1,3}))?[-. (]*(\\d{3})[-. )]*(\\d{3})[-. ]*(\\d{4})(?: *x(\\d+))?\\s*$", message = "Il numero di telefono non è valido")
    private String telephoneNumber;

    @Size(max = 30, message = "L'indirizzo non può superare i 40 caratteri")
    private String address;

    @NotNull(message = "La data è obbligatoria")
    @Past //Deve essere una data passata
    private Date birthDate;
}

