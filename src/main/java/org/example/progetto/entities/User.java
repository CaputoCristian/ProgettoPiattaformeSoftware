package org.example.progetto.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import jakarta.persistence.*;

import java.util.Date;
import java.util.List;

//TODO Check nullable?

@Getter
@Setter
@EqualsAndHashCode
@ToString
@Entity
@Table(name = "user", schema = "orders")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @Basic
    @Column(name = "cf", nullable = true, length = 16)
    private String cf;

    @Basic
    @Column(name = "first_name", nullable = true, length = 50)
    private String firstName;

    @Basic
    @Column(name = "last_name", nullable = true, length = 50)
    private String lastName;

    @Basic
    @Column(name = "telephone_number", nullable = true, length = 20)
    private String telephoneNumber;

    @Basic
    @Column(name = "email", nullable = true, length = 90)
    private String email;

    @Basic
    @Column(name = "address", nullable = true, length = 150)
    private String address;

    @Basic
    @Column(name = "birth_date", nullable = true, length = 150)
    private Date birthDate;

    @OneToMany(mappedBy = "buyer", cascade = CascadeType.MERGE)
    @JsonIgnore
    private List<Purchase> purchases;

}
