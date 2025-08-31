package org.example.progetto.repositories;

import org.example.progetto.entities.ProductInSale;
import org.example.progetto.entities.SaleAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductInSaleRepository extends JpaRepository<ProductInSale, Integer> {
    List<ProductInSale> findBySaleAlert(SaleAlert sale);
}
