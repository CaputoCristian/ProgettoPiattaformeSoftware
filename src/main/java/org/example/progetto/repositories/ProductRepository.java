package org.example.progetto.repositories;

import org.example.progetto.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByType(String type);
    List<Product> findByNameContaining(String name);
    List<Product> findByPriceBetween(Float minPrice, Float maxPrice);
    Product findById(Integer id);

    @Query("""
        SELECT p FROM Product p
        WHERE 
            (LOWER(p.name) LIKE %:query% 
             OR LOWER(p.brand) LIKE %:query% 
             OR LOWER(p.description) LIKE %:query%)
        AND (:minPrice IS NULL OR p.price >= :minPrice)
        AND (:maxPrice IS NULL OR p.price <= :maxPrice)
        AND (:availableOnly = false OR p.quantity > 0)
    """)
    List<Product> search(@Param("query") String query,
                         @Param("minPrice") Float minPrice,
                         @Param("maxPrice") Float maxPrice,
                         @Param("availableOnly") boolean availableOnly);

}
