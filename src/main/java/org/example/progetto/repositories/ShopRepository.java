package org.example.progetto.repositories;

import org.example.progetto.entities.Product;
import org.example.progetto.entities.Shop;
import org.example.progetto.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShopRepository extends JpaRepository<Shop, Long> {
    List<Shop> findBySeller_Id(Integer sellerId);
    Shop findById(Integer id);
    List<Product> getProductsById(Integer shopId);
    Shop findBySeller(User seller);

    Optional<Shop> findBySellerEmail(String email);


}
