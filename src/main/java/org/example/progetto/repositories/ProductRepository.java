package org.example.progetto.repositories;

import org.example.progetto.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByType(String type);
    List<Product> findByNameContaining(String name);
    List<Product> findByPriceBetween(Float minPrice, Float maxPrice);
    List<Product> findById(Integer id);
}
