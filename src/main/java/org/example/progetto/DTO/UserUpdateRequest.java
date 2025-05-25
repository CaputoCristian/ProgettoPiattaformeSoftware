package org.example.progetto.DTO;

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
    private String firstName;
    private String lastName;
    private String telephoneNumber;
    private String address;
    private Date birthDate;
}

