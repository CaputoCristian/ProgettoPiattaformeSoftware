package org.example.progetto.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
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
@EqualsAndHashCode(exclude = "shop") // evita loop con shop.seller
@Entity
@Table(name = "app_user", schema = "orders")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

//    @Basic
//    @Column(name = "cf", nullable = true, length = 16)
//    private String cf;

    @Basic
    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Basic
    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Basic
    @Column(name = "telephone_number", unique = true, nullable = false, length = 20)
    private String telephoneNumber;

    @Basic
    @Column(name = "email", unique = true, nullable = false, length = 90)
    private String email;

    @Basic
    @Column(name = "address", nullable = false, length = 150)
    private String address;

    @Basic
    @Column(name = "birth_date", nullable = false, length = 150)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Europe/Rome")
    private Date birthDate;

    @OneToMany(mappedBy = "buyer", cascade = CascadeType.MERGE)
    @JsonIgnore
    private List<Purchase> purchases;

    @OneToOne(mappedBy = "seller", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference //evitare errori nell'aggiunta di un prodotto - loop infinito
    private Shop shop;

}
