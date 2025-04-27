package org.example.progetto.repositories;

import org.example.progetto.entities.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShopRepository extends JpaRepository<Shop, Long> {
    List<Shop> findBySeller_Id(Integer sellerId);
    List<Shop> findById(Integer id);

}
